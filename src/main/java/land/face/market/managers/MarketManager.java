package land.face.market.managers;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.CategoryContainer;
import land.face.market.data.Listing;
import land.face.market.data.PlayerMarketState;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.SortStyle;
import land.face.market.data.SoldListing;
import land.face.market.data.comparators.LevelComparator;
import land.face.market.data.comparators.PriceComparator;
import land.face.market.data.comparators.RarityComparator;
import land.face.market.data.comparators.TimeComparator;
import land.face.market.events.ListItemEvent;
import land.face.market.menu.main.MarketMenu;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.nunnerycode.mint.MintPlugin;

public class MarketManager {

  private FacelandMarketPlugin plugin;

  private List<Listing> marketListing = new ArrayList<>();
  private Map<UUID, List<SoldListing>> soldListings = new HashMap<>();
  private Map<UUID, PlayerMarketState> marketState = new HashMap<>();

  private Map<SortStyle, List<Listing>> sortCache = new HashMap<>();

  private TimeComparator timeComparator = new TimeComparator();
  private PriceComparator priceComparator = new PriceComparator();
  private RarityComparator rarityComparator = new RarityComparator();
  private LevelComparator levelComparator = new LevelComparator();

  public MarketManager(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  public PlayerMarketState getPlayerState(Player player) {
    if (!marketState.containsKey(player.getUniqueId())) {
      PlayerMarketState state = new PlayerMarketState();
      state.setSortStyle(SortStyle.TIME_DESCENDING);
      state.setSelectedCategory(Category.CATEGORY_1);
      state.setPage(1);
      state.setFilterA(FilterFlagA.ALL);
      state.setFilterB(FilterFlagB.ALL);
      marketState.put(player.getUniqueId(), state);
    }
    return marketState.get(player.getUniqueId());
  }

  public List<Listing> getListings() {
    return marketListing;
  }

  public boolean hasEarnings(Player player) {
    return soldListings.containsKey(player.getUniqueId())
        && soldListings.get(player.getUniqueId()).size() > 0;
  }

  public void collectEarnings(Player player) {
    double amount = 0;
    if (!soldListings.containsKey(player.getUniqueId())) {
      soldListings.put(player.getUniqueId(), new ArrayList<>());
    }
    for (SoldListing sl : soldListings.get(player.getUniqueId())) {
      MessageUtils.sendMessage(player,
          "&2 - Sold " + ItemStackExtensionsKt.getDisplayName(sl.getItemStack())
              + "&r&2 for &e" + (int) sl.getAmount() + " Bits&2!");
      amount += sl.getAmount();
    }
    if (amount == 0) {
      return;
    }
    soldListings.get(player.getUniqueId()).clear();
    MessageUtils.sendMessage(player, "&e  +" + (int) amount + " Bits!");
    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 2.0f);
    MintPlugin.getInstance().getEconomy().depositPlayer(player, amount);
  }

  public boolean buyItem(Player buyer, Listing listing) {
    if (!marketListing.contains(listing)) {
      return false;
    }
    EconomyResponse response = MintPlugin.getInstance().getEconomy()
        .withdrawPlayer(buyer, listing.getPrice());
    if (!response.transactionSuccess()) {
      MessageUtils.sendMessage(buyer, "ur broke");
      return false;
    }
    buyer.getInventory().addItem(listing.getItemStack().clone());
    if (!soldListings.containsKey(listing.getSellerUuid())) {
      soldListings.put(listing.getSellerUuid(), new ArrayList<>());
    }
    soldListings.get(listing.getSellerUuid()).add(SoldListing.fromListing(listing));
    marketListing.remove(listing);
    updateMarket();
    return true;
  }

  public int getListingCount(Player player) {
    int count = 0;
    for (Listing l : marketListing) {
      if (l.getSellerUuid().equals(player.getUniqueId())) {
        count++;
      }
    }
    return count;
  }

  public boolean listItem(Player seller, ItemStack stack, double price) {
    if (stack == null || stack.getType() == Material.AIR) {
      return false;
    }
    ItemStack removalStack = null;
    for (ItemStack i : seller.getInventory().getContents()) {
      if (i == null || i.getType() == Material.AIR) {
        continue;
      }
      if (i.equals(stack)) {
        removalStack = i;
        break;
      }
    }
    if (removalStack == null) {
      return false;
    }

    Category category = getFallbackCatergory(stack.getType());

    Listing listing = new Listing();
    listing.setSellerName(seller.getName());
    listing.setListingTime(System.currentTimeMillis());
    listing.setItemStack(stack.clone());
    listing.setPrice(price);
    listing.setCategory(category);
    listing.setFlagA(FilterFlagA.ALL);
    listing.setFlagB(FilterFlagB.ALL);
    listing.setSellerUuid(seller.getUniqueId());
    listing.setListingId(UUID.randomUUID());

    ListItemEvent listItemEvent = new ListItemEvent(seller, listing);

    Bukkit.getPluginManager().callEvent(listItemEvent);
    if (listItemEvent.isCancelled()) {
      return false;
    }
    if (listing.getCategory() == null) {
      MessageUtils.sendMessage(seller,
          TextUtils.color("&eThis item is not configured to be listen in the market. Sorry!"));
      return false;
    }

    removalStack.setAmount(0);
    marketListing.add(listing);

    String name = ItemStackExtensionsKt.getDisplayName(stack);
    MessageUtils.sendMessage(seller,
        TextUtils.color("&2 - You listed &f" + name + " &r&2for &e" + (int) price + " Bits&2!"));
    updateMarket();
    return true;
  }

  private Category getFallbackCatergory(Material material) {
    for (Category c : PlayerMarketState.CATEGORIES) {
      CategoryContainer container = plugin.getCategoryManager().getCategoryData().get(c);
      if (container.getFallbackMaterials().isEmpty()) {
        return c;
      } else if (container.getFallbackMaterials().contains(material)) {
        return c;
      }
    }
    return null;
  }

  public List<Listing> getViewableListings(PlayerMarketState state) {
    List<Listing> listings = new ArrayList<>(sortCache.get(state.getSortStyle()));
    listings.removeIf(l -> l.getCategory() != state.getSelectedCategory());
    if (state.getFilterA() != FilterFlagA.ALL) {
      listings.removeIf(l -> l.getFlagA() != FilterFlagA.ALL && l.getFlagA() != state.getFilterA());
    }
    if (state.getFilterB() != FilterFlagB.ALL) {
      listings.removeIf(l -> l.getFlagB() != FilterFlagB.ALL && l.getFlagB() != state.getFilterB());
    }
    return listings;
  }

  public void updateMarket() {

    List<Listing> listings;

    listings = new ArrayList<>(marketListing);
    listings.sort(timeComparator);
    sortCache.put(SortStyle.TIME_ASCENDING, listings);
    listings = new ArrayList<>(listings);
    Collections.reverse(listings);
    sortCache.put(SortStyle.TIME_DESCENDING, listings);

    listings = new ArrayList<>(marketListing);
    listings.sort(priceComparator);
    sortCache.put(SortStyle.PRICE_ASCENDING, listings);
    listings = new ArrayList<>(listings);
    Collections.reverse(listings);
    sortCache.put(SortStyle.PRICE_DESCENDING, listings);

    listings = new ArrayList<>(marketListing);
    listings.sort(levelComparator);
    sortCache.put(SortStyle.LEVEL_ASCENDING, listings);
    listings = new ArrayList<>(listings);
    Collections.reverse(listings);
    sortCache.put(SortStyle.LEVEL_DESCENDING, listings);

    listings = new ArrayList<>(marketListing);
    listings.sort(rarityComparator);
    sortCache.put(SortStyle.RARITY_ASCENDING, listings);
    listings = new ArrayList<>(listings);
    Collections.reverse(listings);
    sortCache.put(SortStyle.RARITY_DESCENDING, listings);

    for (Player p : Bukkit.getOnlinePlayers()) {
      MarketMenu.getInstance().update(p);
    }
  }
}
