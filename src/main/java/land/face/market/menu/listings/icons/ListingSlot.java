/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.market.menu.listings.icons;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.time.DurationFormatUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.data.Listing;
import land.face.market.managers.MarketManager;
import land.face.market.menu.listings.ListingMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ListingSlot extends MenuItem {

  private MarketManager marketManager;
  private int listingId;

  private Map<Player, ListingState> listingState = new WeakHashMap<>();
  private Map<Player, Listing> listingMap = new WeakHashMap<>();

  private ItemStack noPerms;

  public ListingSlot(MarketManager marketManager, int listingId) {
    super("", new ItemStack(Material.AIR));
    this.listingId = listingId;
    this.marketManager = marketManager;
    noPerms = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
    ItemStackExtensionsKt.setDisplayName(noPerms, TextUtils.color("&7&l[ Locked ]"));
    List<String> lore = new ArrayList<>();
    lore.add("&7You can purchase extra market");
    lore.add("&7slots for &dFaceGems &7or &eBits");
    lore.add("&7by typing &b/buy&7! Also, players");
    lore.add("&7who are &eContributors &7get four");
    lore.add("&7more slots!");
    ItemStackExtensionsKt.setLore(noPerms, TextUtils.color(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    List<Listing> listings = ListingMenu.getInstance().getListingCache().get(player);
    if (listingId > ListingMenu.getInstance().getSlots(player)) {
      listingMap.put(player, null);
      listingState.put(player, ListingState.LOCKED);
      return noPerms;
    }
    if (listings.size() < listingId) {
      listingMap.put(player, null);
      listingState.put(player, ListingState.EMPTY);
      return getIcon();
    }
    Listing listing = listings.get(listingId - 1);
    ItemStack newIcon = listing.getItemStack().clone();
    if (listing.isSold()) {
      List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(listing.getItemStack()));
      lore.add("");
      lore.add(TextUtils.color("&e&lItem Sold! Click To Collect!"));
      ItemStackExtensionsKt.setLore(newIcon, lore);
      listingMap.put(player, listing);
      listingState.put(player, ListingState.SOLD);
      return newIcon;
    }
    if (listing.isExpired()) {
      List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(listing.getItemStack()));
      lore.add("");
      lore.add(TextUtils.color("&c&lExpired! Click To Reclaim"));
      ItemStackExtensionsKt.setLore(newIcon, lore);
      listingMap.put(player, listing);
      listingState.put(player, ListingState.EXPIRED);
      return newIcon;
    }
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(listing.getItemStack()));
    lore.add("");
    int msRemaining = (int) (listing.getListingTime() - System.currentTimeMillis());
    String format = DurationFormatUtils.formatDuration(msRemaining, "d'D' H'H' m'M'");
    lore.add(TextUtils.color("&6Listing Price: &e" + listing.getPrice() + " Bits"));
    lore.add(TextUtils.color("&6Remaining Time: &b" + format));
    lore.add(TextUtils.color("&c&lClick To Cancel Sale"));
    ItemStackExtensionsKt.setLore(newIcon, lore);
    listingMap.put(player, listing);
    listingState.put(player, ListingState.NORMAL);
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    switch (listingState.get(event.getPlayer())) {
      case SOLD:
        marketManager.collectEarnings(event.getPlayer(), listingMap.get(event.getPlayer()));
        event.setWillUpdate(true);
        return;
      case EMPTY:
      case LOCKED:
        event.setWillUpdate(false);
        return;
      case NORMAL:
      case EXPIRED:
        marketManager.reclaimItem(event.getPlayer(), listingMap.get(event.getPlayer()));
        event.setWillUpdate(true);
        return;
    }
    event.setWillUpdate(true);
  }

  public enum ListingState {
    EMPTY,
    LOCKED,
    EXPIRED,
    SOLD,
    NORMAL
  }
}
