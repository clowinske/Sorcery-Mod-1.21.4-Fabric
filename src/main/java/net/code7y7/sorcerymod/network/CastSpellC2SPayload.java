package net.code7y7.sorcerymod.network;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.AbilityCodec;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellInputCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record CastSpellC2SPayload(String ability, String button, int crystalTier, ItemStack crystal, SpellInput input, SpellInput input1) implements CustomPayload {
public static final Id<CastSpellC2SPayload> ID = new Id<>(Packets.CAST_SPELL_C2S);
    public static final PacketCodec<RegistryByteBuf, CastSpellC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CastSpellC2SPayload::ability,
            PacketCodecs.STRING, CastSpellC2SPayload::button,
            PacketCodecs.INTEGER, CastSpellC2SPayload::crystalTier,
            ItemStack.PACKET_CODEC, CastSpellC2SPayload::crystal,
            SpellInputCodec.CODEC, CastSpellC2SPayload::input,
            SpellInputCodec.CODEC, CastSpellC2SPayload::input1,
            CastSpellC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
