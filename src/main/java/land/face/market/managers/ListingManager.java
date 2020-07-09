package land.face.market.managers;

import java.util.Map;
import java.util.TreeMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.PermissionData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ListingManager {

  private FacelandMarketPlugin plugin;
  private Map<String, PermissionData> permissionSlots = new TreeMap<>();

  public ListingManager(FacelandMarketPlugin plugin) {
    this.plugin = plugin;
    ConfigurationSection permSection = plugin.getConfiguration().getConfigurationSection("permissions");
    if (permSection != null) {
      for (String perm : permSection.getKeys(false)) {
        PermissionData data = new PermissionData();
        data.setAmount(permSection.getInt(perm + ".slots", 0));
        data.setLore(permSection.getStringList(perm + ".description"));
        data.setName(permSection.getString(perm + ".title", "Locked!"));
        permissionSlots.put(perm, data);
      }
    }
  }

  public int getMaxListings(Player player) {
    int amount = 14;
    for (String perm : permissionSlots.keySet()) {
      //System.out.println("checking perm " + perm + " - " + player.hasPermission(perm));
      if (!player.hasPermission(perm)) {
        amount -= permissionSlots.get(perm).getAmount();
      }
    }
    //System.out.println("checking perm " + amount);
    return Math.max(amount, 0);
  }
}
