package net.code7y7.sorcerymod.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.util.crystal.CrystalData;

public class RadiantOptions extends CrystalOptions {
    public RadiantOptions() {
        super(CrystalData.RADIANT);
    }
}
