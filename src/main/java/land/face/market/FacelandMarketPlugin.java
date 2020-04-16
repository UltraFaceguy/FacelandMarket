package land.face.market;

import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.SmartYamlConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import land.face.market.commands.BaseCommand;
import land.face.market.data.Listing;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import land.face.market.listeners.MenuListener;
import land.face.market.listeners.WorldSaveListener;
import land.face.market.managers.CategoryAndFilterManager;
import land.face.market.managers.ListingManager;
import land.face.market.managers.MarketManager;
import land.face.market.menu.confirm.PurchaseConfirmMenu;
import land.face.market.menu.listings.ListingMenu;
import land.face.market.menu.main.MarketMenu;
import land.face.market.menu.sell.SellMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

public class FacelandMarketPlugin extends JavaPlugin {

  private static FacelandMarketPlugin instance;
  public static final DecimalFormat INT_FORMAT = new DecimalFormat("#");
  public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

  private MarketManager marketManager;
  private CategoryAndFilterManager categoryManager;
  private ListingManager listingManager;

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;

  private SmartYamlConfiguration listingData;

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
    listingManager = new ListingManager(this);

    Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
    Bukkit.getPluginManager().registerEvents(new WorldSaveListener(this), this);

    Bukkit.getScheduler().runTaskTimer(this,
        () -> marketManager.expireOldListings(),
        60L * 5, // Start save after 60s
        1120 * 20L // Run every 2 minutes
    );

    MarketMenu.setInstance(new MarketMenu(this));
    PurchaseConfirmMenu.setInstance(new PurchaseConfirmMenu(this));
    SellMenu.setInstance(new SellMenu(this));
    ListingMenu.setInstance(new ListingMenu(this));

    commandHandler = new CommandHandler(this);
    commandHandler.registerCommands(new BaseCommand(this));

    loadListings();

    Bukkit.getServer().getLogger().info("FacelandMarket Enabled!");
  }

  public void onDisable() {
    saveListings(false);
    HandlerList.unregisterAll(this);
    Bukkit.getServer().getScheduler().cancelTasks(this);
    Bukkit.getServer().getLogger().info("FacelandMarket Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public VersionedSmartYamlConfiguration getConfiguration() {
    return configYAML;
  }

  public ListingManager getListingManager() {
    return listingManager;
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

  private void loadListings() {
    listingData = new SmartYamlConfiguration(new File(getDataFolder(), "listings.yml"));
    if (!listingData.getFile().exists()) {
      try {
        listingData.getFile().createNewFile();
        listingData.createSection("listings");
      } catch (IOException ignored) {

      }
    }
    listingData.load();
    List<Listing> loadedListings = new ArrayList<>();
    ConfigurationSection listings = listingData.getConfigurationSection("listings");
    for (String listingId : listings.getKeys(false)) {
      ConfigurationSection listingSection = listings.getConfigurationSection(listingId);
      Listing listing = new Listing();
      listing.setListingId(UUID.fromString(listingId));
      listing.setSellerName(listingSection.getString("seller-name"));
      listing.setSellerUuid(UUID.fromString(listingSection.getString("seller-uuid")));
      listing.setSold(listingSection.getBoolean("sold"));
      listing.setExpired(listingSection.getBoolean("expired"));
      listing.setCategory(Category.valueOf(listingSection.getString("category")));
      listing.setFlagA(FilterFlagA.valueOf(listingSection.getString("flag-a")));
      listing.setFlagB(FilterFlagB.valueOf(listingSection.getString("flag-b")));
      listing.setPrice(listingSection.getInt("price"));
      listing.setListingTime(listingSection.getLong("time"));
      listing.setItemStack(listingSection.getItemStack("item"));
      loadedListings.add(listing);
    }
    marketManager.loadListings(loadedListings);
  }

  public void saveListings(boolean async) {
    final ConfigurationSection listings = listingData.getConfigurationSection("listings");
    for (String listingId : listings.getKeys(false)) {
      listings.set(listingId, null);
    }
    for (Listing l : marketManager.getListings()) {
      ConfigurationSection listingsSection = listings.createSection(l.getListingId().toString());
      listingsSection.set("seller-uuid", l.getSellerUuid().toString());
      listingsSection.set("seller-name", l.getSellerName());
      listingsSection.set("sold", l.isSold());
      listingsSection.set("expired", l.isExpired());
      listingsSection.set("category", l.getCategory().toString());
      listingsSection.set("flag-a", l.getFlagA().toString());
      listingsSection.set("flag-b", l.getFlagB().toString());
      listingsSection.set("price", l.getPrice());
      listingsSection.set("time", l.getListingTime());
      listingsSection.set("item", l.getItemStack());
    }
    if (async) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::saveYml, 20L);
    } else {
      saveYml();
    }
  }

  private synchronized void saveYml() {
    long time = System.currentTimeMillis();
    System.out.println("Saving market data...");
    listingData.save();
    System.out.println("Saved async in " + (System.currentTimeMillis() - time) + "ms");
  }
}