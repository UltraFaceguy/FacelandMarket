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
package land.face.market.menu.confirm.icons;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class YesButton extends MenuItem {

  private final FacelandMarketPlugin plugin;

  public YesButton(FacelandMarketPlugin plugin) {
    super("", new ItemStack(Material.HOPPER));
    this.plugin = plugin;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.GREEN_CONCRETE);
    Listing listing = PurchaseConfirmMenu.getInstance().getSelectedListing(player);
    ItemStackExtensionsKt.setDisplayName(stack, StringExtensionsKt.chatColorize(
        "&a&lClick To Purchase For &e&l" + plugin.getEconomy().format(listing.getPrice())));
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    Listing listing = PurchaseConfirmMenu.getInstance().getSelectedListing(event.getPlayer());
    if (listing == null || plugin.getMarketManager().getListing(listing.getListingId()) == null) {
      MessageUtils.sendMessage(event.getPlayer(),
          StringExtensionsKt.chatColorize("&eSorry! This item no longer exists in the market."));
      return;
    }
    if (listing.getPrice() > plugin.getEconomy().getBalance(event.getPlayer())) {
      MessageUtils.sendMessage(event.getPlayer(),
          StringExtensionsKt.chatColorize("&cYou don't have enough Bits to buy this item!"));
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 0.5f);
      return;
    }
    String itemName = ItemStackExtensionsKt.getDisplayName(listing.getItemStack());
    boolean success = plugin.getMarketManager().buyItem(event.getPlayer(), listing);
    if (!success) {
      event.setWillUpdate(false);
      return;
    }
    MessageUtils.sendMessage(event.getPlayer(), StringExtensionsKt.chatColorize(
        "&2[Market] &aYou purchased &f" + itemName + "&r&a for &e" + plugin.getEconomy().format(listing.getPrice()) + "&a!"));
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.5f);
    event.setWillGoBack(true);
  }
}
