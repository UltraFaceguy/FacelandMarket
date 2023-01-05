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
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import land.face.market.data.CategoryContainer;
import land.face.market.data.PlayerMarketState;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import land.face.market.managers.CategoryAndFilterManager;
import land.face.market.managers.MarketManager;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class FilterButtonB extends MenuItem {

  private final MarketManager marketManager;
  private final CategoryAndFilterManager categoryManager;
  private final Map<Category, List<FilterFlagB>> filterOrder = new HashMap<>();

  public FilterButtonB(MarketManager marketManager, CategoryAndFilterManager categoryManager) {
    super("", new ItemStack(Material.PAPER));
    this.marketManager = marketManager;
    this.categoryManager = categoryManager;
    for (Category c : MarketManager.CATEGORIES) {
      List<FilterFlagB> flags = new ArrayList<>();
      for (FilterFlagB f : MarketManager.FILTER_BS) {
        CategoryContainer container = categoryManager.getCategoryData().get(c);
        if (container.getFilterNamesB().containsKey(f)) {
          flags.add(f);
        }
      }
      filterOrder.put(c, flags);
    }
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    List<String> lore = new ArrayList<>();
    PlayerMarketState state = marketManager.getPlayerState(player);
    if (filterOrder.get(state.getSelectedCategory()).size() == 0) {
      return new ItemStack(Material.AIR);
    }
    for (FilterFlagB f : filterOrder.get(state.getSelectedCategory())) {
      if (state.getFilterB() == f) {
        lore.add(FaceColor.WHITE + "● " + categoryManager.getCategoryData().get(state.getSelectedCategory())
            .getFilterNamesB().get(f));
      } else {
        lore.add(FaceColor.LIGHT_GRAY + categoryManager.getCategoryData().get(state.getSelectedCategory()).getFilterNamesB().get(f));
      }
    }
    ItemStack icon = getIcon().clone();
    ItemStackExtensionsKt.setDisplayName(icon, FaceColor.YELLOW + "Filter");
    ItemStackExtensionsKt.setCustomModelData(icon, 76);
    icon.setLore(lore);

    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
    if (event.getClickType() == ClickType.DOUBLE_CLICK) {
      return;
    }
    event.setWillUpdate(true);

    PlayerMarketState marketState = marketManager.getPlayerState(event.getPlayer());
    List<FilterFlagB> order = filterOrder.get(marketState.getSelectedCategory());
    FilterFlagB currentFlag = marketState.getFilterB();

    if (event.getClickType().isLeftClick()) {
      if (order.indexOf(currentFlag) == order.size() - 1) {
        marketState.setFilterB(order.get(0));
      } else {
        marketState.setFilterB(order.get(order.indexOf(currentFlag) + 1));
      }
    } else if (event.getClickType().isRightClick()) {
      if (currentFlag.ordinal() == 0) {
        marketState.setFilterB(order.get(order.size() - 1));
      } else {
        marketState.setFilterB(order.get(order.indexOf(currentFlag) - 1));
      }
    }
  }
}
