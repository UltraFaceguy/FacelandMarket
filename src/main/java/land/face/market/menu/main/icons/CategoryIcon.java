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

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CategoryIcon extends MenuItem {

  private MarketManager marketManager;
  private Category category;
  private ItemStack selected;
  private ItemStack unselected;

  public CategoryIcon(MarketManager marketManager, CategoryAndFilterManager categoryManager,
      Category category) {
    super("", new ItemStack(Material.BOOK));
    this.marketManager = marketManager;
    this.category = category;
    String name = categoryManager.getCategoryData().get(category).getName();
    selected = new ItemStack(Material.WRITABLE_BOOK);
    ItemStackExtensionsKt.setDisplayName(selected, ChatColor.AQUA + name);
    selected.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    selected.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    unselected = new ItemStack(Material.BOOK);
    ItemStackExtensionsKt.setDisplayName(unselected, ChatColor.GRAY + name);
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    PlayerMarketState state = marketManager.getPlayerState(player);
    return state.getSelectedCategory() == category ? selected : unselected;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    PlayerMarketState state = marketManager.getPlayerState(event.getPlayer());
    if (state.getSelectedCategory() != category) {
      state.setFilterA(FilterFlagA.ALL);
      state.setFilterB(FilterFlagB.ALL);
      state.setPage(1);
      state.setSelectedCategory(category);
      event.setWillUpdate(true);
    }
  }
}
