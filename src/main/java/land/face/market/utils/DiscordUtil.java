/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.market.utils;

import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordImageEvent;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordToolTip;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.Listing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class DiscordUtil {

  private static TextChannel thechan;

  public static void sendToDiscord(Player player, ItemStack item, String format) {

    format = format.replace("%player%", player.getName());
    String[] splitFormat = format.split("%item%", 2);

    ICPlayer icPlayer = InteractiveChatAPI.getICPlayer(player);

    ItemStack finalItem = item.clone();
    TextComponent textComponent;
    if (splitFormat.length == 2) {
      Component itemComponent = Component.text("");
      try {
        itemComponent = InteractiveChatAPI.createItemDisplayComponent(player, finalItem);
      } catch (Exception ignored) {

      }
      textComponent = Component.text(TextUtils.color(splitFormat[0]))
          .append(itemComponent)
          .append(Component.text(TextUtils.color(splitFormat[1])));
    } else {
      textComponent = Component.text(format);
    }
    String finalFormat = ChatColor.stripColor(StringExtensionsKt.chatColorize(format));
    Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
      try {
        List<DiscordMessageContent> contents = new ArrayList<>();
        BufferedImage image = ImageGeneration.getItemStackImage(finalItem,
            InteractiveChatAPI.getICPlayer(player));
        ByteArrayOutputStream itemOs = new ByteArrayOutputStream();
        ImageIO.write(image, "png", itemOs);

        String name = ChatColor.stripColor(ItemStackExtensionsKt.getDisplayName(finalItem));
        Color color = DiscordItemStackUtils.getDiscordColor(finalItem);
        DiscordMessageContent content = new DiscordMessageContent(name, "attachment://Item.png",
            color);
        content.addAttachment("Item.png", itemOs.toByteArray());
        contents.add(content);

        DiscordToolTip discordToolTip = DiscordItemStackUtils.getToolTip(finalItem, icPlayer);
        if (!discordToolTip.isBaseItem()
            || InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImageOnBaseItem) {
          BufferedImage tooltip = ImageGeneration.getToolTipImage(discordToolTip.getComponents());
          ByteArrayOutputStream tooltipOs = new ByteArrayOutputStream();
          ImageIO.write(tooltip, "png", tooltipOs);
          content.addAttachment("ToolTip.png", tooltipOs.toByteArray());
          content.addImageUrl("attachment://ToolTip.png");
        }

        sendToDiscord(finalFormat.replace("%player%",
            player.getName()).replace("%item%", name), contents);

      } catch (Exception e) {
        Bukkit.getLogger().warning("[Market] Failed to send item to discord");
      }
    });
    InteractiveChatAPI.sendMessage(player, textComponent);
  }

  private static void sendToDiscord(String originalText, List<DiscordMessageContent> contents) {
    Bukkit.getScheduler().runTaskAsynchronously(FacelandMarketPlugin.getInstance(), () -> {

      String text = originalText;

      if (thechan == null) {
        thechan = github.scarsz.discordsrv.util.DiscordUtil.getTextChannelById("1078057361714987118");
        if (thechan == null) {
          Bukkit.getLogger().warning("[Market] Could not load discord channel!!");
          return;
        }
      }

      DiscordImageEvent discordImageEvent = new DiscordImageEvent(thechan, text, text, contents,
          false, true);
      TextChannel textChannel = discordImageEvent.getChannel();
      if (discordImageEvent.isCancelled()) {
        String restore = discordImageEvent.getOriginalMessage();
        textChannel.sendMessage(restore).queue();
      } else {
        text = discordImageEvent.getNewMessage();
        textChannel.sendMessage(text).queue();
        for (DiscordMessageContent content : discordImageEvent.getDiscordMessageContents()) {
          content.toJDAMessageRestAction(textChannel).queue();
        }
      }
    });
  }
}
