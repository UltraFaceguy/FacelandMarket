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

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.time.DurationFormatUtils;
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import land.face.market.menu.listings.ListingMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ListingSlot extends MenuItem {

  private final FacelandMarketPlugin plugin;
  private final int listingId;

  private final Map<Player, ListingState> listingState = new WeakHashMap<>();
  private final Map<Player, Listing> listingMap = new WeakHashMap<>();

  private final ItemStack noPerms;
  private final ItemStack itemSold;
  private final ItemStack itemExpired;

  public ListingSlot(FacelandMarketPlugin plugin, int listingId) {
    super("", new ItemStack(Material.AIR));
    this.listingId = listingId;
    this.plugin = plugin;

    noPerms = new ItemStack(Material.PAPER);
    ItemStackExtensionsKt.setDisplayName(noPerms, StringExtensionsKt.chatColorize("&7&l[ Locked ]"));
    ItemStackExtensionsKt.setCustomModelData(noPerms, 998);
    List<String> lore = new ArrayList<>();
    lore.add("&7You can purchase extra market slots");
    lore.add("&7for &f▼&dFaceGems &7by typing &b/buy&7!");
    lore.add("&7Players who are &eContributors &7get four");
    lore.add("&7four more slots!");
    noPerms.setLore(ListExtensionsKt.chatColorize(lore));

    itemSold = new ItemStack(Material.GOLD_BLOCK);
    itemSold.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    itemSold.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    ItemStackExtensionsKt.setDisplayName(itemSold, StringExtensionsKt.chatColorize("&e&lITEM SOLD!"));

    itemExpired = new ItemStack(Material.BARRIER);
    itemExpired.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    itemExpired.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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

    if (listing.isSold()) {
      ItemStack newIcon = itemSold.clone();
      List<String> lore = new ArrayList<>();
      lore.add(StringExtensionsKt.chatColorize("&e&lClick To Collect " +
          plugin.getEconomy().format(listing.getPrice())));
      ItemStackExtensionsKt.setDisplayName(newIcon,
          ItemStackExtensionsKt.getDisplayName(listing.getItemStack()));
      newIcon.setLore(lore);
      listingMap.put(player, listing);
      listingState.put(player, ListingState.SOLD);
      return newIcon;
    }
    if (listing.isExpired()) {
      ItemStack newIcon = itemExpired.clone();
      List<String> lore = new ArrayList<>(listing.getItemStack().getLore() == null ?
          new ArrayList<>() : listing.getItemStack().getLore());
      lore.add("");
      lore.add(StringExtensionsKt.chatColorize("&c&lExpired! Click To Reclaim"));
      ItemStackExtensionsKt
          .setDisplayName(newIcon, ItemStackExtensionsKt.getDisplayName(listing.getItemStack()));
      newIcon.setLore(lore);
      listingMap.put(player, listing);
      listingState.put(player, ListingState.EXPIRED);
      return newIcon;
    }
    ItemStack newIcon = listing.getItemStack().clone();
    List<String> lore = new ArrayList<>(listing.getItemStack().getLore() == null ?
        new ArrayList<>() : listing.getItemStack().getLore());
    lore.add("");
    int msRemaining = (int) (listing.getListingTime() - System.currentTimeMillis());
    String format = DurationFormatUtils.formatDuration(msRemaining, "d'D' H'H' m'M'");
    lore.add(StringExtensionsKt.chatColorize(
        "&6Listing Price: &e" + plugin.getEconomy().format(listing.getPrice())));
    lore.add(StringExtensionsKt.chatColorize("&6Remaining Time: &b" + format));
    lore.add(StringExtensionsKt.chatColorize("&c&lClick To Cancel Sale"));
    newIcon.setLore(lore);
    listingMap.put(player, listing);
    listingState.put(player, ListingState.NORMAL);
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    switch (listingState.get(event.getPlayer())) {
      case SOLD -> {
        plugin.getMarketManager().collectEarnings(event.getPlayer(), listingMap.get(event.getPlayer()));
        event.setWillUpdate(true);
        return;
      }
      case EMPTY, LOCKED -> {
        event.setWillUpdate(false);
        return;
      }
      case NORMAL, EXPIRED -> {
        plugin.getMarketManager().reclaimItem(event.getPlayer(), listingMap.get(event.getPlayer()));
        event.setWillUpdate(true);
        return;
      }
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
