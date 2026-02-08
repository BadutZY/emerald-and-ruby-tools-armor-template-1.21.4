package com.example.emeraldmod.network;

import com.example.emeraldmod.EmeraldMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Packet untuk sync effect state dari server ke client ketika player join
 * Ini memastikan client tahu state yang tersimpan di server
 */
public record EffectStateSyncPacket(boolean toolsEnabled, boolean armorEnabled) implements CustomPayload {

    public static final CustomPayload.Id<EffectStateSyncPacket> ID =
            new CustomPayload.Id<>(Identifier.of(EmeraldMod.MOD_ID, "effect_state_sync"));

    public static final PacketCodec<RegistryByteBuf, EffectStateSyncPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            EffectStateSyncPacket::toolsEnabled,
            PacketCodecs.BOOL,
            EffectStateSyncPacket::armorEnabled,
            EffectStateSyncPacket::new
    );

    public static void register() {
        // Register untuk server -> client
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}