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
import java.util.List;
import land.face.market.managers.MarketManager;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CollectIcon extends MenuItem {

  private MarketManager marketManager;

  private ItemStack stack;

  public CollectIcon(MarketManager marketManager) {
    super("", new ItemStack(Material.GOLD_INGOT));
    this.marketManager = marketManager;
    stack = new ItemStack(Material.GOLD_INGOT);
    ItemStackExtensionsKt.setDisplayName(stack, TextUtils.color("&6Collect Earnings"));
    List<String> lore = new ArrayList<>();
    lore.add("&7When you sell an item,");
    lore.add("&7you can click this icon");
    lore.add("&7to collect your Bits!");
    ItemStackExtensionsKt.setLore(stack, TextUtils.color(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack icon = stack.clone();
    if (marketManager.hasEarnings(player)) {
      icon.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
      icon.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(icon));
      lore.add(TextUtils.color("&a&lCLICK TO COLLECT!"));
      icon.setLore(lore);
    }
    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    marketManager.collectEarnings(event.getPlayer());
    event.setWillUpdate(true);
  }
}
