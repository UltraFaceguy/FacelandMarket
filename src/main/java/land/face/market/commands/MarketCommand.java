package land.face.market.commands;

import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandCompletion;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import land.face.market.FacelandMarketPlugin;
import land.face.market.menu.main.MarketMenu;
import org.bukkit.command.CommandSender;

@CommandAlias("market")
public class MarketCommand extends BaseCommand {

  private final FacelandMarketPlugin plugin;

  public MarketCommand(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
  }

  @Subcommand("reload")
  @CommandPermission("market.reload")
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Subcommand("reload")
  @CommandPermission("market.save")
  public void saveCommand(CommandSender sender) {
    plugin.saveListings(false);
  }

  @Subcommand("relist")
  @CommandPermission("market.relist")
  public void relistCommand(CommandSender sender) {
    plugin.getMarketManager().reListItems();
  }

  @Subcommand("open")
  @CommandCompletion("@players")
  @CommandPermission("market.open")
  public void startCommand(CommandSender sender, OnlinePlayer player) {
    plugin.getMarketManager().updateMarket();
    plugin.getMarketManager().openMarket(player.getPlayer());
  }

  @Subcommand("sell")
  @CommandCompletion("@range:1-100")
  @CommandPermission("market.sell")
  public void startCommand(OnlinePlayer player, int price) {
    plugin.getMarketManager().listItem(player.getPlayer(),
        player.getPlayer().getEquipment().getItemInMainHand(), price);
  }
}
