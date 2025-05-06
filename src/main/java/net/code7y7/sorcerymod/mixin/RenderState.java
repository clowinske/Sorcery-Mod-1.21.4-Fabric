package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.util.RenderStateAccess;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class RenderState implements RenderStateAccess {
    @Unique
    private int leftHandCharge;
    @Unique
    private int rightHandCharge;
    @Unique
    private int leftHandSpell;
    @Unique
    private int rightHandSpell;
    @Unique
    private int corruption;

    @Override
    public int sorcerymod$getLeftHandCharge() {
        return leftHandCharge;
    }
    @Override
    public void sorcerymod$setLeftHandCharge(int value) {
        this.leftHandCharge = value;
    }
    @Override
    public int sorcerymod$getRightHandCharge() {
        return rightHandCharge;
    }
    @Override
    public void sorcerymod$setRightHandCharge(int value) {
        this.rightHandCharge = value;
    }
    @Override
    public int sorcerymod$getCorruption() {
        return corruption;
    }
    @Override
    public void sorcerymod$setCorruption(int value) {
        this.corruption = value;
    }
    @Override
    public int sorcerymod$getLeftHandSpell() {
        return leftHandSpell;
    }
    @Override
    public void sorcerymod$setLeftHandSpell(int value) {
        this.leftHandSpell = value;
    }
    @Override
    public int sorcerymod$getRightHandSpell() {
        return rightHandSpell;
    }
    @Override
    public void sorcerymod$setRightHandSpell(int value) {
        this.rightHandSpell = value;
    }
}
