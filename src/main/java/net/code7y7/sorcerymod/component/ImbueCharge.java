package net.code7y7.sorcerymod.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

public class ImbueCharge {
    public static final Codec<ImbueCharge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("type").forGetter(ImbueCharge::getType),
            Codec.INT.fieldOf("value").forGetter(ImbueCharge::getValue)
    ).apply(instance, ImbueCharge::new));

    private final int type;
    private final int value;

    public ImbueCharge(int type, int value){
        this.type = type;
        this.value = value;
    }



    public int getValue() {
        return value;
    }
    public int getType() {
        return type;
    }
}

