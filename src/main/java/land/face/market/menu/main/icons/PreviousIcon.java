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
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.market.data.PlayerMarketState;
import land.face.market.managers.MarketManager;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PreviousIcon extends MenuItem {

  private final MarketManager marketManager;
  private final ItemStack icon;

  public PreviousIcon(MarketManager marketManager) {
    super("", new ItemStack(Material.PAPER));
    this.marketManager = marketManager;
    icon = new ItemStack(Material.PAPER);
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack newIcon = icon.clone();
    PlayerMarketState state = marketManager.getPlayerState(player);
    if (state.getPage() == 1) {
      ItemStackExtensionsKt.setDisplayName(newIcon, FaceColor.LIGHT_GRAY + "<< Previous Page");
      ItemStackExtensionsKt.setCustomModelData(newIcon, 73);
    } else {
      ItemStackExtensionsKt.setDisplayName(newIcon, FaceColor.YELLOW + "<< Previous Page");
      ItemStackExtensionsKt.setCustomModelData(newIcon, 72);
    }
    newIcon.setAmount(Math.max(1, state.getPage() - 1));
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    PlayerMarketState state = marketManager.getPlayerState(event.getPlayer());
    state.setPage(Math.max(1, state.getPage() - 1));
    event.setWillUpdate(true);
  }
}
