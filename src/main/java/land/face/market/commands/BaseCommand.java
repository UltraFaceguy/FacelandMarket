package land.face.market.commands;

import land.face.market.FacelandMarketPlugin;
import land.face.market.menu.main.MarketMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private FacelandMarketPlugin plugin;

  public BaseCommand(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "market reload", permissions = "market.reload", onlyPlayers = false)
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Command(identifier = "market save", permissions = "market.save", onlyPlayers = false)
  public void saveCommand(CommandSender sender) {
    plugin.saveListings(false);
  }

  @Command(identifier = "market relist", permissions = "market.relist", onlyPlayers = false)
  public void relistCommand(CommandSender sender) {
    plugin.getMarketManager().reListItems();
  }

  @Command(identifier = "market open", permissions = "market.open", onlyPlayers = false)
  public void startCommand(CommandSender sender, @Arg(name = "player") Player player) {
    plugin.getMarketManager().updateMarket();
    MarketMenu.getInstance().open(player);
  }

  @Command(identifier = "market sell", permissions = "market.sell", onlyPlayers = true)
  public void startCommand(Player sender, @Arg(name = "price") int price) {
    plugin.getMarketManager().listItem(sender, sender.getEquipment().getItemInMainHand(), price);
  }
}
