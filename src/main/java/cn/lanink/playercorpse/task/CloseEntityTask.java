package cn.lanink.playercorpse.task;

import cn.lanink.playercorpse.PlayerCorpse;
import cn.lanink.playercorpse.entity.EntityPlayerCorpse;
import cn.nukkit.scheduler.PluginTask;

/**
 * @author lt_name
 */
public class CloseEntityTask extends PluginTask<PlayerCorpse> {

    private final EntityPlayerCorpse playerCorpse;

    public CloseEntityTask(PlayerCorpse owner, EntityPlayerCorpse playerCorpse) {
        super(owner);
        this.playerCorpse = playerCorpse;
    }

    @Override
    public void onRun(int i) {
        if (this.playerCorpse != null && !this.playerCorpse.isClosed()) {
            this.playerCorpse.close();
        }
        this.owner.entityPlayerCorpses.remove(this.playerCorpse);
        this.cancel();
    }

}
