package net.code7y7.sorcerymod.entity;

import net.code7y7.sorcerymod.spell.SpellPose;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.LivingEntity;

public class SpellCastingPlayerModel<T extends LivingEntity> extends PlayerEntityModel {
    private SpellPose currentPose = SpellPose.NONE;
    public SpellCastingPlayerModel(ModelPart modelPart, boolean thinArms) {
        super(modelPart, thinArms);
    }

    public void setSpellCastingPose(SpellPose pose){
        this.currentPose = pose;
        applyPose();
    }
    public void applyPose(){
        resetPose();
        switch (currentPose){
            case FIRESPRAY_OUTWARD:
                this.rightArm.pitch = (float) Math.toRadians(-90);
                this.leftArm.pitch = (float) Math.toRadians(-90);
                break;
        }
    }

    public void resetPose(){
        this.rightArm.pitch = 0;
        this.rightArm.yaw = 0;
        this.leftArm.pitch = 0;
        this.leftArm.yaw = 0;
    }

    @Override
    public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
        super.setAngles(playerEntityRenderState);
        if(currentPose != SpellPose.NONE){
            applyPose();
        }
    }
}
