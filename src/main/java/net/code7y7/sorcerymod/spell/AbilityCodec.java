package net.code7y7.sorcerymod.spell;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Map;

public class AbilityCodec {
    public static final PacketCodec<RegistryByteBuf, Ability> PACKET_CODEC = new PacketCodec<RegistryByteBuf, Ability>() {
        public Ability decode(RegistryByteBuf registryByteBuf) {
            String abilityId = registryByteBuf.readString(); // Read the ability identifier
            return AbilityRegistry.get(abilityId).orElseThrow(() ->
                    new DecoderException("Unknown Ability ID: " + abilityId)
            );
        }

        public void encode(RegistryByteBuf registryByteBuf, Ability ability) {
            for (Map.Entry<String, Ability> entry : AbilityRegistry.ABILITIES.entrySet()) {
                if (entry.getValue().equals(ability)) {
                    registryByteBuf.writeString(entry.getKey());
                    return;
                }
            }
            throw new EncoderException("Unregistered Ability: " + ability);
        }
    };

}
