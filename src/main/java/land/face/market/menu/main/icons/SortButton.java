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

import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import land.face.market.data.PlayerMarketState;
import land.face.market.data.PlayerMarketState.SortStyle;
import land.face.market.managers.CategoryAndFilterManager;
import land.face.market.managers.MarketManager;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SortButton extends MenuItem {

  private final MarketManager marketManager;
  private final CategoryAndFilterManager categoryManager;
  private final ItemStack icon;

  public SortButton(MarketManager marketManager, CategoryAndFilterManager categoryManager) {
    super("", new ItemStack(Material.CARTOGRAPHY_TABLE));
    this.marketManager = marketManager;
    this.categoryManager = categoryManager;
    icon = new ItemStack(Material.CARTOGRAPHY_TABLE);
    ItemStackExtensionsKt.setDisplayName(icon, ChatColor.WHITE + "Sort Order");
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    List<String> lore = new ArrayList<>();
    PlayerMarketState state = marketManager.getPlayerState(player);
    for (SortStyle s : PlayerMarketState.SORT_STYLES) {
      if (state.getSortStyle() == s) {
        lore.add("&f● " + categoryManager.getSortName(s));
      } else {
        lore.add("&7" + categoryManager.getSortName(s));
      }
    }
    ItemStack newIcon = icon.clone();
    newIcon.setLore(ListExtensionsKt.chatColorize(lore));
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    if (event.getClickType() == ClickType.DOUBLE_CLICK) {
      event.setWillUpdate(false);
      return;
    }
    PlayerMarketState state = marketManager.getPlayerState(event.getPlayer());

    Iterator<SortStyle> iterator = Arrays.asList(PlayerMarketState.SORT_STYLES).iterator();
    while (iterator.hasNext()) {
      SortStyle next = iterator.next();
      if (next == state.getSortStyle()) {
        if (iterator.hasNext()) {
          state.setSortStyle(iterator.next());
        } else {
          state.setSortStyle(SortStyle.TIME_DESCENDING);
        }
      }
    }
    event.setWillUpdate(true);
  }
}
