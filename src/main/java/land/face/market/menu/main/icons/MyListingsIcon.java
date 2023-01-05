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
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.market.managers.MarketManager;
import land.face.market.menu.listings.ListingMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MyListingsIcon extends MenuItem {

  private final MarketManager marketManager;
  private final ItemStack icon;

  public MyListingsIcon(MarketManager marketManager) {
    super("", new ItemStack(Material.BOOKSHELF));
    this.marketManager = marketManager;
    icon = new ItemStack(Material.BOOKSHELF);
    ItemStackExtensionsKt.setDisplayName(icon, FaceColor.GREEN + "My Listings");
    List<String> lore = new ArrayList<>();
    lore.add(FaceColor.LIGHT_GRAY + "Click here to list items");
    lore.add(FaceColor.LIGHT_GRAY + "on the market or to view");
    lore.add(FaceColor.LIGHT_GRAY + "your listed items");
    icon.setLore(ListExtensionsKt.chatColorize(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack newIcon = icon.clone();
    newIcon.setAmount(Math.max(1, marketManager.getListingCount(player)));
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.getPlayer().closeInventory();
    ListingMenu.getInstance().getListingCache()
        .put(event.getPlayer(), marketManager.getListings(event.getPlayer()));
    ListingMenu.getInstance().open(event.getPlayer());
  }
}
