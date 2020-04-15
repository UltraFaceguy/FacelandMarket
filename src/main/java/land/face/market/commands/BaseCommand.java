package land.face.market.commands;

import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import land.face.market.FacelandMarketPlugin;
import land.face.market.menu.main.MarketMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private FacelandMarketPlugin plugin;
  private Gson gson = new Gson();

  public BaseCommand(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "market open", permissions = "market.start", onlyPlayers = false)
  public void startCommand(CommandSender sender, @Arg(name = "player") Player player) {
    plugin.getMarketManager().updateMarket();
    MarketMenu.getInstance().open(player);
  }

  @Command(identifier = "market sell", permissions = "market.start", onlyPlayers = true)
  public void startCommand(Player sender, @Arg(name = "price") double price) {
    plugin.getMarketManager().listItem(sender, sender.getEquipment().getItemInMainHand(), price);
  }
}
