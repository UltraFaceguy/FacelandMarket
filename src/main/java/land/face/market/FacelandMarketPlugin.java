package land.face.market;

import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.market.commands.BaseCommand;
import land.face.market.listeners.SellMenuListener;
import land.face.market.managers.CategoryAndFilterManager;
import land.face.market.managers.MarketManager;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import land.face.market.menu.main.MarketMenu;
import land.face.market.menu.sell.SellMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

public class FacelandMarketPlugin extends JavaPlugin {

  private static FacelandMarketPlugin instance;
  public static final DecimalFormat INT_FORMAT = new DecimalFormat("#");
  public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

  private MarketManager marketManager;
  private CategoryAndFilterManager categoryManager;

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;

  private CommandHandler commandHandler;

  public static FacelandMarketPlugin getInstance() {
    return instance;
  }

  public void onEnable() {
    instance = this;

    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    settings = MasterConfiguration.loadFromFiles(configYAML);

    categoryManager = new CategoryAndFilterManager(this);
    marketManager = new MarketManager(this);

    Bukkit.getPluginManager().registerEvents(new SellMenuListener(), this);

    MarketMenu.setInstance(new MarketMenu(this));
    PurchaseConfirmMenu.setInstance(new PurchaseConfirmMenu(this));
    SellMenu.setInstance(new SellMenu(this));

    commandHandler = new CommandHandler(this);
    commandHandler.registerCommands(new BaseCommand(this));

    Bukkit.getServer().getLogger().info("StrifeArena Enabled!");
  }

  public void onDisable() {
    //arenaManager.saveArenas();
    HandlerList.unregisterAll(this);
    Bukkit.getServer().getScheduler().cancelTasks(this);
    Bukkit.getServer().getLogger().info("StrifeArena Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public MarketManager getMarketManager() {
    return marketManager;
  }

  public CategoryAndFilterManager getCategoryManager() {
    return categoryManager;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }
}