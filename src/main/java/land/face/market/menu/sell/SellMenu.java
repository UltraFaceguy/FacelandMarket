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
package land.face.market.menu.sell;

import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.menu.sell.icons.BackButton;
import land.face.market.menu.sell.icons.PriceChangeButton;
import land.face.market.menu.sell.icons.SellItem;
import land.face.market.menu.sell.icons.SellButton;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellMenu extends ItemMenu {

  private static SellMenu instance;

  private FacelandMarketPlugin plugin;
  private SellItem sellItem;

  private Map<Player, ItemStack> selectedItem = new WeakHashMap<>();
  private Map<Player, Integer> selectedPrice = new WeakHashMap<>();

  public SellMenu(FacelandMarketPlugin plugin) {
    super("Sell Item", Size.fit(36), plugin);
    this.plugin = plugin;
    sellItem = new SellItem();
    setItem(11, sellItem);
    setItem(20, new SellButton(plugin.getMarketManager()));
    setItem(35, new BackButton());

    setItem(14, new PriceChangeButton(Material.GOLD_NUGGET, 5));
    setItem(23, new PriceChangeButton(Material.GOLD_NUGGET,-5));
    setItem(15, new PriceChangeButton(Material.GOLD_INGOT,500));
    setItem(24, new PriceChangeButton(Material.GOLD_INGOT,-500));
    setItem(16, new PriceChangeButton(Material.GOLD_BLOCK,50000));
    setItem(25, new PriceChangeButton(Material.GOLD_BLOCK,-50000));
  }

  public ItemStack getSelectedItem(Player player) {
    return selectedItem.getOrDefault(player, null);
  }

  public ItemStack removeSelectedItem(Player player) {
    return selectedItem.remove(player);
  }

  public ItemStack setSelectedItem(Player player, ItemStack itemStack) {
    return selectedItem.put(player, itemStack.clone());
  }

  public void setSelectedPrice(Player player, int amount) {
    selectedPrice.put(player, amount);
  }

  public int getSelectedPrice(Player player) {
    return selectedPrice.getOrDefault(player, 5);
  }

  public static SellMenu getInstance() {
    return instance;
  }

  public static void setInstance(SellMenu menu) {
    instance = menu;
  }

}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
