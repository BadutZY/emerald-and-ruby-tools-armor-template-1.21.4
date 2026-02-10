package com.example.emeraldmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.example.emeraldmod.item.ModItems;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * Ini intercept ARGUMENT ItemStack
     * sebelum animasi totem di-lock oleh vanilla
     */
    @ModifyVariable(
            method = "showFloatingItem",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ItemStack replaceTotemPopItem(ItemStack originalStack) {

        // Safety check
        if (!originalStack.isOf(Items.TOTEM_OF_UNDYING)) {
            return originalStack;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return originalStack;
        }

        ClientPlayerEntity player = client.player;

        // Cek item yang SEBENARNYA dipakai player
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        if (mainHand.isOf(ModItems.EMERALD_TOTEM)) {
            return mainHand.copy();
        }

        if (mainHand.isOf(ModItems.RUBY_TOTEM)) {
            return mainHand.copy();
        }

        if (offHand.isOf(ModItems.EMERALD_TOTEM)) {
            return offHand.copy();
        }

        if (offHand.isOf(ModItems.RUBY_TOTEM)) {
            return offHand.copy();
        }

        // Default: vanilla totem
        return originalStack;
    }
}
