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

import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.time.DurationFormatUtils;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import land.face.market.data.PlayerMarketState;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ListingIcon extends MenuItem {

  private final FacelandMarketPlugin plugin;
  private final int listingSlot;
  private final Set<UUID> selfBuyOverride = new HashSet<>();

  public static boolean DEBUG_FLAGS = false;

  public ListingIcon(FacelandMarketPlugin plugin, int listingSlot) {
    super("", new ItemStack(Material.AIR));
    this.plugin = plugin;
    this.listingSlot = listingSlot;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    PlayerMarketState state = plugin.getMarketManager().getPlayerState(player);
    List<Listing> resultListings = plugin.getMarketManager().getViewableListings(state);

    int slotPlacement = (state.getPage() - 1) * 36 + listingSlot;
    if (resultListings.size() <= slotPlacement) {
      return getIcon();
    }

    ItemStack icon;
    if (selfBuyOverride.contains(player.getUniqueId())) {
      selfBuyOverride.remove(player.getUniqueId());
      icon = new ItemStack(Material.BARRIER);
      ItemStackExtensionsKt.setDisplayName(icon, StringExtensionsKt.chatColorize("&eYou can't buy your own item!"));
      return icon;
    }

    Listing listing = resultListings.get(slotPlacement);

    if (plugin.getMarketManager().getListing(listing.getListingId()) == null) {
      icon = new ItemStack(Material.BARRIER);
      ItemStackExtensionsKt.setDisplayName(icon, "&eListing no longer exists!");
      return icon;
    }

    icon = listing.getItemStack().clone();
    List<String> displayLore = new ArrayList<>(icon.getLore() == null ? new ArrayList<>() : icon.getLore());
    int msRemaining = (int) (listing.getListingTime() - System.currentTimeMillis());
    String format;
    if (msRemaining < 60000) {
      format = FaceColor.RED + FaceColor.BOLD.s() + "VERY SOON!!";
    } else {
      format = DurationFormatUtils.formatDuration(msRemaining, "d'D' H'H' m'M'");
    }
    displayLore.add("");
    displayLore.add(FaceColor.ORANGE + "Price: " + FaceColor.YELLOW + plugin.getEconomy()
        .format(listing.getPrice()));
    displayLore.add(FaceColor.ORANGE + "Seller: " + FaceColor.WHITE + listing.getSellerName());
    displayLore.add(FaceColor.ORANGE + "Expiry: " + FaceColor.WHITE + format);
    if (DEBUG_FLAGS) {
      displayLore.add(FaceColor.DARK_GRAY + " - " + listing.getCategory());
      displayLore.add(FaceColor.DARK_GRAY + " - " + listing.getFlagA());
      displayLore.add(FaceColor.DARK_GRAY + " - " + listing.getFlagB());
    }
    icon.setLore(displayLore);
    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    PlayerMarketState state = plugin.getMarketManager().getPlayerState(event.getPlayer());
    List<Listing> resultListings = plugin.getMarketManager().getViewableListings(state);

    int slotPlacement = (plugin.getMarketManager().getPlayerState(event.getPlayer()).getPage() - 1)
        * 36 + listingSlot;
    if (resultListings.size() <= slotPlacement) {
      return;
    }
    Listing listing = resultListings.get(slotPlacement);
    if (listing.getSellerUuid().equals(event.getPlayer().getUniqueId())) {
      selfBuyOverride.add(event.getPlayer().getUniqueId());
      event.setWillUpdate(true);
      return;
    }
    if (plugin.getMarketManager().getListing(listing.getListingId()) == null) {
      event.setWillUpdate(true);
      return;
    }
    PurchaseConfirmMenu.getInstance().setSelectedListing(event.getPlayer(), listing);
    PurchaseConfirmMenu.getInstance().open(event.getPlayer());
  }
}
