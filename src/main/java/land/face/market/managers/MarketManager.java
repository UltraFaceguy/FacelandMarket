package land.face.market.managers;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
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
import land.face.market.data.comparators.PriceComparator;
import land.face.market.data.comparators.TimeComparator;
import land.face.market.data.comparators.TypeComparator;
import land.face.market.events.ListItemEvent;
import land.face.market.events.PurchaseItemEvent;
import land.face.market.menu.listings.ListingMenu;
import land.face.market.menu.main.MarketMenu;
import land.face.market.utils.InventoryUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketManager {

  private final FacelandMarketPlugin plugin;

  private final List<Listing> marketListing = new ArrayList<>();

  private final Map<SortStyle, List<Listing>> sortCache = new HashMap<>();

  private final TimeComparator timeComparator = new TimeComparator();
  private final PriceComparator priceComparator = new PriceComparator();
  private final TypeComparator typeComparator = new TypeComparator();

  public static final Category[] CATEGORIES = Category.values();
  public static final FilterFlagA[] FILTER_AS = FilterFlagA.values();
  public static final FilterFlagB[] FILTER_BS = FilterFlagB.values();

  private final Map<Player, MarketMenu> menuMap = new HashMap<>();

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

  public void openMarket(Player player) {
    if (!menuMap.containsKey(player)) {
      menuMap.put(player, new MarketMenu(plugin));
    }
    menuMap.get(player).open(player);
  }

  public PlayerMarketState getPlayerState(Player player) {
    if (!menuMap.containsKey(player)) {
      menuMap.put(player, new MarketMenu(plugin));
    }
    return menuMap.get(player).getMarketState();
  }

  public void updateMarketTitle(Player player, String name) {
    menuMap.get(player).setName(name);
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

        Player seller = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
          if (p.getUniqueId().equals(l.getSellerUuid())) {
            seller = p;
            break;
          }
        }

        String name = ItemStackExtensionsKt.getDisplayName(l.getItemStack());
        if (seller != null) {
          MessageUtils.sendMessage(seller, "&2[Market] &aYour market listing for " + name +
              " has expired! Visit the market to pick it up or re-list!");
        } else {
          sendDiscordMessage(l.getSellerUuid(),
              "**Greetings gamer!** Sadly, nobody bought your market listing of **" +
                  ChatColor.stripColor(name) +
                  "** and the listing has expired. Login and visit the market to reclaim this item.");
        }
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
    MessageUtils.sendMessage(player, "&2[Market] &aCollected &f" + name + "&r&a!");
    MessageUtils.sendMessage(player, "&e  +" + plugin.getEconomy().format(listing.getPrice()));
    marketListing.remove(listing);
    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1.5f);
    plugin.getEconomy().depositPlayer(player, listing.getPrice());
  }

  public void reclaimItem(Player player, Listing listing) {
    listing = getListing(listing.getListingId());
    if (listing == null || !listing.getSellerUuid().equals(player.getUniqueId()) || listing
        .isSold()) {
      return;
    }
    if (!InventoryUtil.addItems(player, true, listing.getItemStack().clone())) {
      return;
    }
    marketListing.remove(listing);
    if (!listing.isExpired()) {
      updateMarket();
    }
    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1f);
  }

  public boolean buyItem(Player buyer, Listing listing) {
    listing = getListing(listing.getListingId());
    if (listing == null || listing.isSold() || listing.isExpired()) {
      MessageUtils
          .sendMessage(buyer, "&eSorry, this listing seems to have expired or been purchased!");
      return false;
    }
    if (!InventoryUtil.addItems(buyer, true, listing.getItemStack().clone())) {
      return false;
    }
    EconomyResponse response = plugin.getEconomy().withdrawPlayer(buyer, listing.getPrice());
    if (!response.transactionSuccess()) {
      MessageUtils.sendMessage(buyer, "ur broke");
      return false;
    }
    listing.setSold(true);

    PurchaseItemEvent purchaseItemEvent = new PurchaseItemEvent(buyer, listing);
    Bukkit.getPluginManager().callEvent(purchaseItemEvent);

    Player seller = null;
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getUniqueId().equals(listing.getSellerUuid())) {
        seller = p;
        break;
      }
    }

    if (seller != null) {
      String name = ItemStackExtensionsKt.getDisplayName(listing.getItemStack());
      MessageUtils.sendMessage(seller, "&2[Market] &aYour listing for&f " + name
          + " &r&awas purchased! Visit a marketplace to collect your &eBits&a!");
      ListingMenu.getInstance().update(seller);
    } else {
      sendDiscordMessage(listing.getSellerUuid(),
          "**Greetings gamer!** Your market listing of **" + ChatColor.stripColor(
              ItemStackExtensionsKt.getDisplayName(listing.getItemStack())
                  + "** has been bought for **" + listing.getPrice()
                  + " Bits**! Login to collect your earnings at the market when you feel like it :O"));
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
        "&2[Market] &aYou listed &f" + name + " &r&afor &e" + plugin.getEconomy().format(price)
            + "&a!");
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
      listings.removeIf(l -> l.getFlagA() != state.getFilterA());
    }
    if (state.getFilterB() != FilterFlagB.ALL) {
      listings.removeIf(l -> l.getFlagB() != state.getFilterB());
    }

    return listings;
  }

  public void sendDiscordMessage(UUID uuid, String message) {
    String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
    if (StringUtils.isBlank(id)) {
      return;
    }
    User user = DiscordUtil.getJda().getUserById(id);
    if (user == null) {
      return;
    }
    DiscordUtil.privateMessage(user, message);
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
    listings.sort(typeComparator);
    sortCache.put(SortStyle.TYPE_ORDER, listings);

    for (Player p : menuMap.keySet()) {
      if (p.isOnline()) {
        menuMap.get(p).update(p);
      }
    }
  }
}
