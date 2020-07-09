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

import static land.face.market.FacelandMarketPlugin.INT_FORMAT;

import com.tealcube.minecraft.bukkit.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.market.menu.sell.SellMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PriceChangeButton extends MenuItem {

  private int changeAmount;
  private ItemStack icon;
  private String currPriceString;
  private List<String> instructions;

  public PriceChangeButton(Material material, int changeAmount) {
    super("", new ItemStack(material));
    this.changeAmount = changeAmount;
    icon = new ItemStack(material);
    ItemStackExtensionsKt.setDisplayName(icon, TextUtils.color("&f&lClick To Chance Price!"));
    currPriceString = TextUtils.color("&eCurrent Price: &f");
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add("&eLeft Click: &a+" + INT_FORMAT.format(changeAmount));
    lore.add("&eRight Click: &c-" + INT_FORMAT.format(changeAmount));
    instructions = TextUtils.color(lore);
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    List<String> lore = new ArrayList<>();
    lore.add(currPriceString + INT_FORMAT.format(SellMenu.getInstance().getSelectedPrice(player)) + " Bits");
    lore.addAll(instructions);
    ItemStack newIcon = icon.clone();
    ItemStackExtensionsKt.setLore(newIcon, lore);
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    int change;
    if (event.getClickType() == ClickType.LEFT || event.getClickType() == ClickType.SHIFT_LEFT) {
      change = changeAmount;
    } else if (event.getClickType() == ClickType.RIGHT || event.getClickType() == ClickType.SHIFT_RIGHT) {
      change = -changeAmount;
    } else {
      event.setWillUpdate(false);
      return;
    }
    int newPrice = change + SellMenu.getInstance().getSelectedPrice(event.getPlayer());
    newPrice = Math.min(10000000, newPrice);
    newPrice = Math.max(10, newPrice);
    SellMenu.getInstance().setSelectedPrice(event.getPlayer(), newPrice);
    event.setWillUpdate(true);
  }
}
