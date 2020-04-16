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

import com.tealcube.minecraft.bukkit.TextUtils;
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
import land.face.market.managers.CategoryAndFilterManager;
import land.face.market.managers.MarketManager;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FilterButtonA extends MenuItem {

  private MarketManager marketManager;
  private CategoryAndFilterManager categoryManager;
  private Map<Category, List<FilterFlagA>> filterOrder = new HashMap<>();

  public FilterButtonA(MarketManager marketManager, CategoryAndFilterManager categoryManager) {
    super("", new ItemStack(Material.HOPPER));
    this.marketManager = marketManager;
    this.categoryManager = categoryManager;
    for (Category c : MarketManager.CATEGORIES) {
      List<FilterFlagA> flags = new ArrayList<>();
      for (FilterFlagA f : MarketManager.FILTER_AS) {
        CategoryContainer container = categoryManager.getCategoryData().get(c);
        if (container.getFilterNamesA().containsKey(f)) {
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
      return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }
    for (FilterFlagA f : filterOrder.get(state.getSelectedCategory())) {
      if (state.getFilterA() == f) {
        lore.add("&f● " + categoryManager.getCategoryData().get(state.getSelectedCategory())
            .getFilterNamesA().get(f));
      } else {
        lore.add("&7" + categoryManager.getCategoryData().get(state.getSelectedCategory())
            .getFilterNamesA().get(f));
      }
    }
    ItemStack icon = getIcon().clone();
    ItemStackExtensionsKt.setDisplayName(icon, ChatColor.WHITE + "Filter");
    ItemStackExtensionsKt.setLore(icon, TextUtils.color(lore));

    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    PlayerMarketState state = marketManager.getPlayerState(event.getPlayer());

    Iterator iterator = filterOrder.get(state.getSelectedCategory()).iterator();
    while (iterator.hasNext()) {
      FilterFlagA next = (FilterFlagA) iterator.next();
      if (next == state.getFilterA()) {
        if (iterator.hasNext()) {
          state.setFilterA((FilterFlagA) iterator.next());
        } else {
          state.setFilterA(filterOrder.get(state.getSelectedCategory()).get(0));
        }
      }
    }
    event.setWillUpdate(true);
  }
}
