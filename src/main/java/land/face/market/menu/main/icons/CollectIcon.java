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
import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
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

  private final MarketManager marketManager;
  private final ItemStack stack;

  public CollectIcon(MarketManager marketManager) {
    super("", new ItemStack(Material.PAPER));
    this.marketManager = marketManager;
    stack = new ItemStack(Material.PAPER);
    ItemStackExtensionsKt.setDisplayName(stack, FaceColor.ORANGE + "Collect Earnings");
    List<String> lore = new ArrayList<>();
    ItemStackExtensionsKt.setCustomModelData(stack, 71);
    lore.add("|lgray|When you sell an item,");
    lore.add("|lgray|you can click this icon");
    lore.add("|lgray|to collect your Bits!");
    stack.setLore(PaletteUtil.color(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack icon = stack.clone();
    if (marketManager.hasEarnings(player)) {
      icon.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
      icon.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      ItemStackExtensionsKt.setCustomModelData(icon, 70);
      List<String> lore = new ArrayList<>(icon.getLore() == null ? new ArrayList<>() : icon.getLore());
      lore.add(StringExtensionsKt.chatColorize("&a&lCLICK TO COLLECT!"));
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
