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

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.menu.listings.ListingMenu;
import land.face.market.menu.sell.icons.BackButton;
import land.face.market.menu.sell.icons.PriceChangeButton;
import land.face.market.menu.sell.icons.SellButton;
import land.face.market.menu.sell.icons.SellItem;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellMenu extends ItemMenu {

  private static SellMenu instance;

  private FacelandMarketPlugin plugin;
  private SellItem sellItem;

  private Map<Player, ItemStack> selectedItem = new WeakHashMap<>();
  private Map<Player, Integer> selectedPrice = new WeakHashMap<>();
  private List<String> bannedStrings;

  /*
    00 01 02 03 04 05 06 07 08
    09 10 11 12 13 14 15 16 17
    18 19 20 21 22 23 24 25 26
    27 28 29 30 31 32 33 34 35
    36 37 38 39 40 41 42 43 44
    45 46 47 48 49 50 51 52 53
  */
  public SellMenu(FacelandMarketPlugin plugin) {
    super("Sell Item", Size.fit(36), plugin);
    this.plugin = plugin;
    bannedStrings = plugin.getSettings().getStringList("config.disallowed-names-and-lores");
    sellItem = new SellItem();
    setItem(13, sellItem);
    setItem(15, new SellButton(plugin));
    setItem(11, new BackButton());

    setItem(28, new PriceChangeButton(plugin, Material.GOLD_NUGGET, 1));
    setItem(29, new PriceChangeButton(plugin, Material.GOLD_INGOT, 10));
    setItem(30, new PriceChangeButton(plugin, Material.DIAMOND, 100));
    setItem(31, new PriceChangeButton(plugin, Material.EMERALD, 1000));
    setItem(32, new PriceChangeButton(plugin, Material.GOLD_BLOCK, 10000));
    setItem(33, new PriceChangeButton(plugin, Material.DIAMOND_BLOCK, 100000));
    setItem(34, new PriceChangeButton(plugin, Material.EMERALD_BLOCK, 1000000));
  }

  @Override
  public void open(Player player) {
    if (plugin.getMarketManager().getListingCount(player) >= ListingMenu.getInstance()
        .getSlots(player)) {
      MessageUtils.sendMessage(player,
          "&eYou do not have any market slots remaining! Cancel a listing or collect outstanding earnings to list another item.");
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
      ListingMenu.getInstance().open(player);
      return;
    }
    super.open(player);
  }

  public ItemStack getSelectedItem(Player player) {
    return selectedItem.getOrDefault(player, null);
  }

  public void removeSelectedItem(Player player) {
    selectedItem.remove(player);
  }

  public void setSelectedItem(Player player, ItemStack itemStack) {
    List<String> strings = new ArrayList<>();
    strings.add(ItemStackExtensionsKt.getDisplayName(itemStack));
    strings.addAll(itemStack.getLore() == null ? new ArrayList<>() : itemStack.getLore());
    for (String b : bannedStrings) {
      for (String s : strings) {
        if (StringUtils.isBlank(s)) {
          continue;
        }
        if (ChatColor.stripColor(s).contains(b)) {
          MessageUtils.sendMessage(player, "&eSorry! This item cannot be listed.");
          return;
        }
      }
    }
    selectedItem.put(player, itemStack.clone());
  }

  public void setSelectedPrice(Player player, int amount) {
    selectedPrice.put(player, amount);
  }

  public int getSelectedPrice(Player player) {
    return selectedPrice.getOrDefault(player, 10);
  }

  public static SellMenu getInstance() {
    return instance;
  }

  public static void setInstance(SellMenu menu) {
    instance = menu;
  }

}
