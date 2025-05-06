package net.code7y7.sorcerymod.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class FireballEntityRenderState extends EntityRenderState {
    public float yaw;
    public float pitch;
    public FireballEntityRenderState(){
    }
}
