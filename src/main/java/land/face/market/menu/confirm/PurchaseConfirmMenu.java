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
package land.face.market.menu.confirm;

import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import land.face.market.menu.confirm.icons.ListingSample;
import land.face.market.menu.confirm.icons.NoButton;
import land.face.market.menu.confirm.icons.YesButton;
import land.face.market.menu.main.MarketMenu;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.entity.Player;

public class PurchaseConfirmMenu extends ItemMenu {

  private static PurchaseConfirmMenu instance;

  private Map<Player, Listing> selectedListing = new WeakHashMap<>();

  public PurchaseConfirmMenu(FacelandMarketPlugin plugin) {
    super("Buy Item", Size.fit(36), plugin);
    setItem(20, new YesButton(plugin.getMarketManager()));
    setItem(13, new ListingSample());
    setItem(24, new NoButton());
    setParent(MarketMenu.getInstance());
  }

  public void setSelectedListing(Player player, Listing listing) {
    selectedListing.put(player, listing);
  }

  public Listing getSelectedListing(Player player) {
    return selectedListing.get(player);
  }

  public static PurchaseConfirmMenu getInstance() {
    return instance;
  }

  public static void setInstance(PurchaseConfirmMenu menu) {
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
