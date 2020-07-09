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
package land.face.market.menu.main.icons;

import static land.face.market.FacelandMarketPlugin.INT_FORMAT;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.time.DurationFormatUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import land.face.market.data.Listing;
import land.face.market.data.PlayerMarketState;
import land.face.market.managers.MarketManager;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ListingIcon extends MenuItem {

  private MarketManager marketManager;
  private int listingSlot;

  private Set<UUID> selfBuyOverride = new HashSet<>();

  public ListingIcon(MarketManager marketManager, int listingSlot) {
    super("", new ItemStack(Material.AIR));
    this.marketManager = marketManager;
    this.listingSlot = listingSlot;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    PlayerMarketState state = marketManager.getPlayerState(player);
    List<Listing> resultListings = marketManager.getViewableListings(state);

    int slotPlacement = (state.getPage() - 1) * 36 + listingSlot;
    if (resultListings.size() <= slotPlacement) {
      return getIcon();
    }

    ItemStack icon;
    if (selfBuyOverride.contains(player.getUniqueId())) {
      selfBuyOverride.remove(player.getUniqueId());
      icon = new ItemStack(Material.BARRIER);
      ItemStackExtensionsKt.setDisplayName(icon, TextUtils.color("&eYou can't buy your own item!"));
      return icon;
    }

    Listing listing = resultListings.get(slotPlacement);

    if (marketManager.getListing(listing.getListingId()) == null) {
      icon = new ItemStack(Material.BARRIER);
      ItemStackExtensionsKt.setDisplayName(icon, "&eListing no longer exists!");
      return icon;
    }

    icon = listing.getItemStack().clone();
    List<String> displayLore = new ArrayList<>(ItemStackExtensionsKt.getLore(icon));
    int msRemaining = (int) (listing.getListingTime() - System.currentTimeMillis());
    String format = DurationFormatUtils.formatDuration(msRemaining, "d'D' H'H' m'M'");
    displayLore.add("");
    displayLore.add(ChatColor.GOLD + "Price: " +
        ChatColor.WHITE + INT_FORMAT.format(listing.getPrice()) + " Bits");
    displayLore.add(ChatColor.GOLD + "Seller: " + ChatColor.WHITE + listing.getSellerName());
    displayLore.add(ChatColor.GOLD + "Expiry: " + ChatColor.WHITE + format);
    displayLore.add(ChatColor.DARK_GRAY + " - " + listing.getCategory());
    displayLore.add(ChatColor.DARK_GRAY + " - " + listing.getFlagA());
    displayLore.add(ChatColor.DARK_GRAY + " - " + listing.getFlagB());
    icon.setLore(displayLore);
    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    PlayerMarketState state = marketManager.getPlayerState(event.getPlayer());
    List<Listing> resultListings = marketManager.getViewableListings(state);

    int slotPlacement =
        (marketManager.getPlayerState(event.getPlayer()).getPage() - 1) * 36 + listingSlot;
    if (resultListings.size() <= slotPlacement) {
      return;
    }
    Listing listing = resultListings.get(slotPlacement);
    if (listing.getSellerUuid().equals(event.getPlayer().getUniqueId())) {
      selfBuyOverride.add(event.getPlayer().getUniqueId());
      event.setWillUpdate(true);
      return;
    }
    if (marketManager.getListing(listing.getListingId()) == null) {
      event.setWillUpdate(true);
      return;
    }
    PurchaseConfirmMenu.getInstance().setSelectedListing(event.getPlayer(), listing);
    PurchaseConfirmMenu.getInstance().open(event.getPlayer());
  }
}
