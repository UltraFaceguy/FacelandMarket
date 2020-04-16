package land.face.market.managers;

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
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import land.face.market.data.PlayerMarketState.SortStyle;
import land.face.market.data.comparators.LevelComparator;
import land.face.market.data.comparators.PriceComparator;
import land.face.market.data.comparators.RarityComparator;
import land.face.market.data.comparators.TimeComparator;
import land.face.market.events.ListItemEvent;
import land.face.market.events.PurchaseItemEvent;
import land.face.market.menu.listings.ListingMenu;
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
  private Map<UUID, PlayerMarketState> marketState = new HashMap<>();

  private Map<SortStyle, List<Listing>> sortCache = new HashMap<>();

  private TimeComparator timeComparator = new TimeComparator();
  private PriceComparator priceComparator = new PriceComparator();
  private RarityComparator rarityComparator = new RarityComparator();
  private LevelComparator levelComparator = new LevelComparator();

  public static final Category[] CATEGORIES = Category.values();
  public static final FilterFlagA[] FILTER_AS = FilterFlagA.values();
  public static final FilterFlagB[] FILTER_BS = FilterFlagB.values();

  private static final long ONE_WEEK_MS = 604800000L;

  public MarketManager(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  public List<Listing> getListings() {
    return new ArrayList<>(marketListing);
  }

  public void loadListings(List<Listing> listings) {
    marketListing.clear();
    marketListing.addAll(listings);
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

  public Listing getListing(UUID uuid) {
    for (Listing l : marketListing) {
      if (l.getListingId().equals(uuid)) {
        return l;
      }
    }
    return null;
  }

  public boolean hasEarnings(Player player) {
    for (Listing l : marketListing) {
      if (l.isSold() && l.getSellerUuid().equals(player.getUniqueId())) {
        return true;
      }
    }
    return false;
  }

  public List<Listing> getListings(Player player) {
    List<Listing> listings = new ArrayList<>();
    for (Listing l : marketListing) {
      if (l.getSellerUuid().equals(player.getUniqueId())) {
        listings.add(l);
      }
    }
    return listings;
  }

  public void expireOldListings() {
    boolean update = false;
    for (Listing l : marketListing) {
      if (l.isSold() || l.isExpired()) {
        continue;
      }
      if (l.getListingTime() < System.currentTimeMillis()) {
        l.setExpired(true);
        update = true;
      }
    }
    if (update) {
      updateMarket();
    }
  }

  public void collectEarnings(Player player) {
    for (Listing l : getListings(player)) {
      collectEarnings(player, l);
    }
  }

  public void collectEarnings(Player player, Listing listing) {
    listing = getListing(listing.getListingId());
    if (listing == null || !listing.getSellerUuid().equals(player.getUniqueId())) {
      return;
    }
    if (!listing.isSold()) {
      return;
    }
    String name = ItemStackExtensionsKt.getDisplayName(listing.getItemStack());
    MessageUtils.sendMessage(player, "&2 - Collected &f" + name + "&r&2!");
    MessageUtils.sendMessage(player, "&e  +" + listing.getPrice() + " Bits!");
    marketListing.remove(listing);
    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1.5f);
    MintPlugin.getInstance().getEconomy().depositPlayer(player, listing.getPrice());
  }

  public void reclaimItem(Player player, Listing listing) {
    listing = getListing(listing.getListingId());
    if (listing == null || !listing.getSellerUuid().equals(player.getUniqueId()) || listing
        .isSold()) {
      return;
    }
    marketListing.remove(listing);
    player.getInventory().addItem(listing.getItemStack().clone());
    if (!listing.isExpired()) {
      updateMarket();
    }
    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1f);
  }

  public boolean buyItem(Player buyer, Listing listing) {
    listing = getListing(listing.getListingId());
    if (listing == null || listing.isSold() || listing.isExpired()) {
      return false;
    }
    EconomyResponse response = MintPlugin.getInstance().getEconomy()
        .withdrawPlayer(buyer, listing.getPrice());
    if (!response.transactionSuccess()) {
      MessageUtils.sendMessage(buyer, "ur broke");
      return false;
    }
    buyer.getInventory().addItem(listing.getItemStack().clone());
    listing.setSold(true);

    PurchaseItemEvent purchaseItemEvent = new PurchaseItemEvent(buyer, listing);
    Bukkit.getPluginManager().callEvent(purchaseItemEvent);

    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getUniqueId().equals(listing.getSellerUuid())) {
        String name = ItemStackExtensionsKt.getDisplayName(listing.getItemStack());
        MessageUtils.sendMessage(p, "&2 - Your market listing of&f " + name
            + " &r&2was purchased! Visit a marketplace to collect your &eBits&2!");
        ListingMenu.getInstance().update(p);
        break;
      }
    }

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

  public boolean listItem(Player seller, ItemStack stack, int price) {
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
    listing.setListingTime(System.currentTimeMillis() + ONE_WEEK_MS);
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
      MessageUtils
          .sendMessage(seller, "&eThis item is not configured to be listed in the market. Sorry!");
      return false;
    }

    removalStack.setAmount(0);
    marketListing.add(listing);

    String name = ItemStackExtensionsKt.getDisplayName(stack);
    MessageUtils.sendMessage(seller,
        "&2 - You listed &f" + name + " &r&2for &e" + price + " Bits&2!");
    updateMarket();
    return true;
  }

  public void reListItems() {
    for (Listing listing : marketListing) {
      ListItemEvent listItemEvent = new ListItemEvent(null, listing);
      Bukkit.getPluginManager().callEvent(listItemEvent);
    }
  }

  private Category getFallbackCatergory(Material material) {
    for (Category c : CATEGORIES) {
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
    listings.removeIf(
        l -> l.getCategory() != state.getSelectedCategory() || l.isSold() || l.isExpired());
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
    listings.removeIf(l -> l.isSold() || l.isExpired());

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
