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
package land.face.market.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import java.util.List;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public final class LoginListener implements Listener {

  private FacelandMarketPlugin plugin;

  public LoginListener(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLogin(PlayerLoginEvent event) {
    if (event.getResult() == Result.ALLOWED) {
      List<Listing> listings = plugin.getMarketManager().getListings(event.getPlayer());
      int unclaimed = 0;
      int expired = 0;
      for (Listing l : listings) {
        if (l.isExpired()) {
          expired++;
        } else if (l.isSold()) {
          unclaimed++;
        }
      }
      if (unclaimed == 0 && expired == 0) {
        return;
      }
      int finalUnclaimed = unclaimed;
      int finalExpired = expired;
      Bukkit.getScheduler().runTaskLater(plugin, () -> sendSpam(event.getPlayer(),
          finalUnclaimed, finalExpired), 280L);
    }
  }

  private void sendSpam(Player player, int unclaimed, int expired) {
    if (!player.isOnline()) {
      return;
    }
    if (unclaimed > 0) {
      MessageUtils.sendMessage(player, "&e&lYou have &f&l" + unclaimed + " &e&lmarket listings to collect!");
    }
    if (expired > 0) {
      MessageUtils.sendMessage(player, "&e&lYou have &c&l" + expired + " &e&lexpired market listings");
    }
    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1f);
  }

}
