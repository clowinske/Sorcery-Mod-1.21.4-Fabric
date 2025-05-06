package net.code7y7.sorcerymod.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BlinkC2SPayload(ItemStack crystalStack) implements CustomPayload {
    public static final Id<BlinkC2SPayload> ID = new Id<>(Packets.BLINK_C2S);
    public static final PacketCodec<RegistryByteBuf, BlinkC2SPayload> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, BlinkC2SPayload::crystalStack,
            BlinkC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
