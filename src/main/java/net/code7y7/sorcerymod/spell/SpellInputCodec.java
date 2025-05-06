package net.code7y7.sorcerymod.spell;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class SpellInputCodec {
    public static final PacketCodec<ByteBuf, SpellInput> CODEC = PacketCodecs.VAR_INT.xmap(
            ordinal -> SpellInput.values()[ordinal], // Convert integer to SpellInput
            SpellInput::ordinal // Convert SpellInput to integer
    );
}
