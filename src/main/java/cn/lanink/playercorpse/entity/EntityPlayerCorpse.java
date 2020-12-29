package cn.lanink.playercorpse.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author lt_name
 */
public class EntityPlayerCorpse extends EntityHuman {

    private Player player;

    public EntityPlayerCorpse(FullChunk chunk, CompoundTag nbt, Player player) {
        super(chunk, nbt);
        this.player = player;
        this.setNameTagVisible(false);
        this.setNameTagAlwaysVisible(false);
    }

    public Player getPlayer() {
        return this.player;
    }


}
