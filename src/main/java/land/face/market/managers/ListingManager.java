package land.face.market.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.PermissionData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ListingManager {

  private final Map<String, PermissionData> permissionSlots = new HashMap<>();

  public ListingManager(FacelandMarketPlugin plugin) {
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
      if (player.hasPermission(perm)) {
        continue;
      }
      amount -= permissionSlots.get(perm).getAmount();
    }
    return Math.max(amount, 0);
  }
}
