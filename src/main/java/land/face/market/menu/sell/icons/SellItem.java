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
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.market.menu.sell.SellMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellItem extends MenuItem {

  private ItemStack icon;

  public SellItem() {
    super("", new ItemStack(Material.AIR));
    icon = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    ItemStackExtensionsKt.setDisplayName(icon, TextUtils.color("&a&lClick An Item!"));
    List<String> lore = new ArrayList<>();
    lore.add("&7Click an item in your");
    lore.add("&7inventory to list it");
    lore.add("&7on the market!");
    ItemStackExtensionsKt.setLore(icon, TextUtils.color(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    if (SellMenu.getInstance().getSelectedItem(player) == null) {
      return icon;
    }
    return SellMenu.getInstance().getSelectedItem(player);
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
  }
}
