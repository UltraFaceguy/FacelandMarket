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

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.market.data.Listing;
import land.face.market.managers.MarketManager;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.nunnerycode.mint.MintPlugin;

public class YesButton extends MenuItem {

  private MarketManager marketManager;

  public YesButton(MarketManager marketManager) {
    super("", new ItemStack(Material.HOPPER));
    this.marketManager = marketManager;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.GREEN_CONCRETE);
    Listing listing = PurchaseConfirmMenu.getInstance().getSelectedListing(player);
    ItemStackExtensionsKt.setDisplayName(stack,
        TextUtils.color("&e&lClick To Purchase For &f&l" + listing.getPrice() + " Bits"));
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    Listing listing = PurchaseConfirmMenu.getInstance().getSelectedListing(event.getPlayer());
    if (listing == null || !marketManager.getListings().contains(listing)) {
      MessageUtils.sendMessage(event.getPlayer(),
          TextUtils.color("&eSorry! This item no longer exists in the market."));
      return;
    }
    if (listing.getPrice() > MintPlugin.getInstance().getEconomy().getBalance(event.getPlayer())) {
      MessageUtils.sendMessage(event.getPlayer(),
          TextUtils.color("&cYou don't have enough Bits to buy this item!"));
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 0.5f);
      return;
    }
    boolean freeSpace = false;
    for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
      if (stack == null || stack.getType() == Material.AIR) {
        freeSpace = true;
        break;
      }
    }
    if (!freeSpace) {
      MessageUtils.sendMessage(event.getPlayer(),
          TextUtils.color("&eYou don't have enough inventory space to buy this item!"));
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 0.5f);
      return;
    }
    String itemName = ItemStackExtensionsKt.getDisplayName(listing.getItemStack());
    marketManager.buyItem(event.getPlayer(), listing);
    MessageUtils.sendMessage(event.getPlayer(), TextUtils
        .color("&2 - You purchased &f" + itemName + "&r&2 for &e" + listing.getPrice() + " Bits&2!"));
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.5f);
    event.setWillGoBack(true);
  }
}
