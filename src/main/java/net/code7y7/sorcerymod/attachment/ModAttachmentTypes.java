package net.code7y7.sorcerymod.attachment;

import com.mojang.serialization.Codec;
import net.code7y7.sorcerymod.SorceryMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;

public class ModAttachmentTypes {
    public static final AttachmentType<String> POUCH_ID = AttachmentRegistry.create(
                    SorceryMod.createIdentifier("pouch_id"),
                    builder->builder // we are using a builder chain here to configure the attachment data type
                            .persistent(Codec.STRING) // how to save and load the data when the object it is attached to is saved or loaded
            .syncWith(
                    PacketCodecs.STRING,  // how to turn the data into a packet to send to players
                    AttachmentSyncPredicate.all() // who to send the data to
            ));

    public static void register() {
        // This empty method can be called from the mod initializer to ensure our component type is registered at mod initialization time
    }
}
