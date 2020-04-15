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
package land.face.market.menu.main;

import land.face.market.FacelandMarketPlugin;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.menu.BlankIcon;
import land.face.market.menu.main.icons.CategoryIcon;
import land.face.market.menu.main.icons.CollectIcon;
import land.face.market.menu.main.icons.FilterButtonA;
import land.face.market.menu.main.icons.FilterButtonB;
import land.face.market.menu.main.icons.MyListingsIcon;
import land.face.market.menu.main.icons.ListingIcon;
import land.face.market.menu.main.icons.NextIcon;
import land.face.market.menu.main.icons.PreviousIcon;
import land.face.market.menu.main.icons.SortButton;
import ninja.amp.ampmenus.menus.ItemMenu;

public class MarketMenu extends ItemMenu {

  private static MarketMenu instance;

  public MarketMenu(FacelandMarketPlugin plugin) {
    super("Marketplace", Size.fit(56), plugin);
    setItem(0, new CollectIcon(plugin.getMarketManager()));
    setItem(1, new BlankIcon());
    setItem(2, new CategoryIcon(plugin.getMarketManager(), plugin.getCategoryManager(),
        Category.CATEGORY_1));
    setItem(3, new CategoryIcon(plugin.getMarketManager(), plugin.getCategoryManager(),
        Category.CATEGORY_2));
    setItem(4, new CategoryIcon(plugin.getMarketManager(), plugin.getCategoryManager(),
        Category.CATEGORY_3));
    setItem(5, new CategoryIcon(plugin.getMarketManager(), plugin.getCategoryManager(),
        Category.CATEGORY_4));
    setItem(6, new CategoryIcon(plugin.getMarketManager(), plugin.getCategoryManager(),
        Category.CATEGORY_5));
    setItem(7, new BlankIcon());
    setItem(8, new MyListingsIcon(plugin.getMarketManager()));

    int listingSlot = 0;
    for (int i = 9; i <= 44; i++) {
      setItem(i, new ListingIcon(plugin.getMarketManager(), listingSlot));
      listingSlot++;
    }

    setItem(45, new PreviousIcon(plugin.getMarketManager()));
    setItem(46, new BlankIcon());
    setItem(47, new FilterButtonA(plugin.getMarketManager(), plugin.getCategoryManager()));
    setItem(48, new FilterButtonB(plugin.getMarketManager(), plugin.getCategoryManager()));
    setItem(49, new BlankIcon());
    setItem(50, new BlankIcon());
    setItem(51, new SortButton(plugin.getMarketManager()));
    setItem(52, new BlankIcon());
    setItem(53, new NextIcon(plugin.getMarketManager()));
  }

  public static MarketMenu getInstance() {
    return instance;
  }

  public static void setInstance(MarketMenu menu) {
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
