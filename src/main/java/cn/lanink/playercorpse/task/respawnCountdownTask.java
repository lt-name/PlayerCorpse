package cn.lanink.playercorpse.task;

import cn.lanink.playercorpse.PlayerCorpse;
import cn.lanink.playercorpse.entity.EntityPlayerCorpse;
import cn.lanink.playercorpse.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.DummyBossBar;

/**
 * @author lt_name
 */
public class respawnCountdownTask extends PluginTask<PlayerCorpse> {

    private final Player player;
    private final EntityPlayerCorpse playerCorpse;
    private final int needTime;
    private int time;
    private final DummyBossBar bossBar;

    public respawnCountdownTask(PlayerCorpse owner, Player player) {
        super(owner);
        this.player = player;
        this.needTime = owner.getConfig().getInt("respawnTime", 30);
        this.time = this.needTime;

        this.bossBar = new DummyBossBar.Builder(player).build();
        this.bossBar.setText("§c等待复活中");
        player.createBossBar(this.bossBar);

        CompoundTag nbt = EntityPlayerCorpse.getDefaultNBT(player);
        Skin skin = player.getSkin();
        switch(skin.getSkinData().data.length) {
            case 8192:
            case 16384:
            case 32768:
            case 65536:
                break;
            default:
                skin = this.owner.corpseSkin;
                break;
        }
        skin.setTrusted(true);
        nbt.putCompound("Skin", new CompoundTag()
                .putByteArray("Data", skin.getSkinData().data)
                .putString("ModelId", skin.getSkinId()));
        nbt.putFloat("Scale", -1.0F);
        nbt.putString("playerName", player.getName());
        EntityPlayerCorpse entity = new EntityPlayerCorpse(player.getChunk(), nbt, player);
        entity.setSkin(skin);
        entity.setPosition(new Vector3(player.getFloorX(), Tools.getFloorY(player), player.getFloorZ()));
        entity.setGliding(true);
        entity.setRotation(player.getYaw(), 0);
        entity.spawnToAll();
        entity.updateMovement();

        this.playerCorpse = entity;
    }

    @Override
    public void onRun(int i) {
        this.time--;
        if (this.time <= 0) {
            this.cancel();
            return;
        }
        bossBar.setLength(100 - (this.time * 1F / this.needTime * 100));
    }

    @Override
    public void cancel() {
        this.owner.tasks.remove(this.player);
        this.playerCorpse.close();

        this.player.removeBossBar(this.bossBar.getBossBarId());
        this.player.teleport(this.owner.getServer().getDefaultLevel().getSafeSpawn());
        this.player.setHealth(this.player.getMaxHealth());
        this.player.setGamemode(Player.SURVIVAL);

        super.cancel();
    }

}
