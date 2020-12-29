package cn.lanink.playercorpse;

import cn.lanink.playercorpse.entity.EntityPlayerCorpse;
import cn.lanink.playercorpse.task.CloseEntityTask;
import cn.lanink.playercorpse.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author lt_name
 */
public class PlayerCorpse extends PluginBase implements Listener {

    public final Skin corpseSkin = new Skin();
    public ConcurrentHashMap<Player, respawnCountdownTask> tasks = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        BufferedImage skinData = null;
        try {
            skinData = ImageIO.read(this.getResource("skin.png"));
        } catch (IOException ignored) { }
        if (skinData == null) {
            getLogger().error("§c默认尸体皮肤加载失败！请检查插件完整性！");
        }
        this.corpseSkin.setSkinData(skinData);
        this.corpseSkin.setSkinId("defaultSkin");

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("Loading completed!");
    }

    @Override
    public void onDisable() {
        for (Task task : this.tasks.values()) {
            task.cancel();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Math.ceil(event.getFinalDamage()) + 1 >= player.getHealth() && !this.tasks.containsKey(player)) {
                event.setDamage(0);
                event.setCancelled(true);
                player.setGamemode(Player.SPECTATOR);
                respawnCountdownTask task = new respawnCountdownTask(this, player);
                this.getServer().getScheduler().scheduleRepeatingTask(this, task, 20);
                this.tasks.put(player, task);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.tasks.containsKey(player)) {
            this.tasks.get(player).cancel();
        }
    }

}
