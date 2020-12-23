package cn.lanink.playercorpse;

import cn.lanink.playercorpse.entity.EntityPlayerCorpse;
import cn.lanink.playercorpse.task.CloseEntityTask;
import cn.lanink.playercorpse.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author lt_name
 */
public class PlayerCorpse extends PluginBase implements Listener {

    private final Skin corpseSkin = new Skin();
    public HashSet<EntityPlayerCorpse> entityPlayerCorpses = new HashSet<>();

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
        if (!this.entityPlayerCorpses.isEmpty()) {
            for (EntityPlayerCorpse entity : this.entityPlayerCorpses) {
                entity.close();
            }
            this.entityPlayerCorpses.clear();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        CompoundTag nbt = EntityPlayerCorpse.getDefaultNBT(player);
        Skin skin = player.getSkin();
        switch(skin.getSkinData().data.length) {
            case 8192:
            case 16384:
            case 32768:
            case 65536:
                break;
            default:
                skin = this.corpseSkin;
                break;
        }
        skin.setTrusted(true);
        nbt.putCompound("Skin", new CompoundTag()
                .putByteArray("Data", skin.getSkinData().data)
                .putString("ModelId", skin.getSkinId()));
        nbt.putFloat("Scale", -1.0F);
        nbt.putString("playerName", player.getName());
        EntityPlayerCorpse entity = new EntityPlayerCorpse(player.getChunk(), nbt);
        entity.setSkin(skin);
        entity.setPosition(new Vector3(player.getFloorX(), Tools.getFloorY(player), player.getFloorZ()));
        entity.setGliding(true);
        entity.setRotation(player.getYaw(), 0);
        entity.spawnToAll();
        entity.updateMovement();
        this.entityPlayerCorpses.add(entity);
        this.getServer().getScheduler().scheduleDelayedTask(this,
                new CloseEntityTask(this, entity),
                20 * this.getConfig().getInt("corpseExistenceTime", 300));
    }

}
