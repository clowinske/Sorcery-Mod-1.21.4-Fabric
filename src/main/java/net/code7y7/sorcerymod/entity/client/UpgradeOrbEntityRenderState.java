package net.code7y7.sorcerymod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class UpgradeOrbEntityRenderState extends EntityRenderState {
    public float yaw;
    public float pitch;
    public int crystalType = 0;
    public boolean isUnlocked;
    public float scale = 1.0f;
    public UpgradeOrbEntityRenderState(){
    }
}
