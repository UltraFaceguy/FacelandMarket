package land.face.market.utils;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryUtil {

  public static boolean addItems(Player player, boolean failOnFull, @NotNull ItemStack... items) {
    if (!failOnFull) {
      addItems(player, items);
      return true;
    }
    int freeSlots = 0;
    for (ItemStack item : player.getInventory().getStorageContents()) {
      if (item == null || item.getType() == Material.AIR) {
        freeSlots++;
      }
    }
    if (freeSlots >= items.length) {
      addItems(player, items);
      return true;
    }
    MessageUtils.sendMessage(player, "&c[!] You don't have enough inventory space!");
    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
    return false;
  }

  public static void addItems(Player player, @NotNull ItemStack... items) {
    Map<Integer, ItemStack> remainder = player.getInventory().addItem(items);
    if (remainder.size() > 0) {
      MessageUtils.sendMessage(player,
          "&e[!] There wasn't enough space in your inventory, so some items were dropped on the ground. Don't worry, only you can pick them up :)");
      player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
      for (ItemStack stack : remainder.values()) {
        Item item = player.getWorld().dropItemNaturally(player.getLocation(), stack);
        item.setOwner(player.getUniqueId());
      }
    }
  }
}
