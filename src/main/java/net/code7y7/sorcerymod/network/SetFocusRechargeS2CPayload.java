package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record SetFocusRechargeS2CPayload(int amount) implements CustomPayload {
public static final Id<SetFocusRechargeS2CPayload> ID = new Id<>(Packets.SET_FOCUS_RECHARGE_S2C);
    public static final PacketCodec<RegistryByteBuf, SetFocusRechargeS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SetFocusRechargeS2CPayload::amount,
            SetFocusRechargeS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
