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
package land.face.market.menu.sell.icons;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.market.managers.MarketManager;
import land.face.market.menu.sell.SellMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellButton extends MenuItem {

  private MarketManager marketManager;

  public SellButton(MarketManager marketManager) {
    super("", new ItemStack(Material.HOPPER));
    this.marketManager = marketManager;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.GREEN_CONCRETE);
    ItemStackExtensionsKt.setDisplayName(stack, TextUtils.color("&2Click to sell for &e" +
        SellMenu.getInstance().getSelectedPrice(player) + " Bits"));
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    if (SellMenu.getInstance().getSelectedItem(event.getPlayer()) == null) {
      event.getPlayer()
          .playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
      MessageUtils.sendMessage(event.getPlayer(), "&eYou need to pick an item to sell!");
      return;
    }
    boolean success = marketManager.listItem(event.getPlayer(),
        SellMenu.getInstance().getSelectedItem(event.getPlayer()),
        SellMenu.getInstance().getSelectedPrice(event.getPlayer()));
    if (success) {
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
      SellMenu.getInstance().removeSelectedItem(event.getPlayer());
      SellMenu.getInstance().setSelectedPrice(event.getPlayer(), 5);
    }
    event.setWillUpdate(true);
  }
}
