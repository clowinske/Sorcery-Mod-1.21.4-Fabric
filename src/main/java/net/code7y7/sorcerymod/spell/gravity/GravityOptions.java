package net.code7y7.sorcerymod.spell.gravity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;

public class GravityOptions extends CrystalOptions {

    PushOptions pushOptions;
    PullOptions pullOptions;
    public GravityOptions(){
        super(CrystalData.GRAVITY);
        this.pushOptions = new PushOptions();
        spellOptions.put("push", pushOptions);
        spellOptions.put("pull", new PullOptions());
    }

    public PushOptions getPushOptions() {
        return pushOptions;
    }
}
