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
package land.face.market.menu.listings;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import land.face.market.menu.BlankIcon;
import land.face.market.menu.listings.icons.BackButton;
import land.face.market.menu.listings.icons.ListingSlot;
import land.face.market.menu.listings.icons.NewListingButton;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.entity.Player;

public class ListingMenu extends ItemMenu {

  private static ListingMenu instance;

  private FacelandMarketPlugin plugin;

  private Map<Player, List<Listing>> listingCache = new WeakHashMap<>();
  private Map<Player, Integer> slots = new WeakHashMap<>();

  public ListingMenu(FacelandMarketPlugin plugin) {
    super("Listings", Size.fit(35), plugin);
    this.plugin = plugin;

    setItem(10, new ListingSlot(plugin, 1));
    setItem(11, new ListingSlot(plugin, 2));
    setItem(12, new ListingSlot(plugin, 3));
    setItem(13, new ListingSlot(plugin, 4));
    setItem(14, new ListingSlot(plugin, 5));
    setItem(15, new ListingSlot(plugin, 6));
    setItem(16, new ListingSlot(plugin, 7));
    setItem(19, new ListingSlot(plugin, 8));
    setItem(20, new ListingSlot(plugin, 9));
    setItem(21, new ListingSlot(plugin, 10));
    setItem(22, new ListingSlot(plugin, 11));
    setItem(23, new ListingSlot(plugin, 12));
    setItem(24, new ListingSlot(plugin, 13));
    setItem(25, new ListingSlot(plugin, 14));

    setItem(30, new BackButton());
    setItem(32, new NewListingButton());

    fillEmptySlots(new BlankIcon());
  }

  @Override
  public void open(Player player) {
    listingCache.put(player, plugin.getMarketManager().getListings(player));
    slots.put(player, plugin.getListingManager().getMaxListings(player));
    super.open(player);
  }

  @Override
  public void update(Player player) {
    listingCache.put(player, plugin.getMarketManager().getListings(player));
    slots.put(player, plugin.getListingManager().getMaxListings(player));
    super.update(player);
  }

  public int getSlots(Player player) {
    return slots.getOrDefault(player, 0);
  }

  public Map<Player, List<Listing>> getListingCache() {
    return listingCache;
  }

  public static ListingMenu getInstance() {
    return instance;
  }

  public static void setInstance(ListingMenu menu) {
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
