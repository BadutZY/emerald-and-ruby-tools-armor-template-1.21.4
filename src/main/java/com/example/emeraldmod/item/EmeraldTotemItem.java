package com.example.emeraldmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Emerald Totem of Undying - Can prevent death 3 times
 */
public class EmeraldTotemItem extends Item {
    private static final int MAX_USES = 3;
    private static final String NBT_USES_KEY = "RemainingUses";

    public EmeraldTotemItem(Settings settings) {
        super(settings);
    }

    /**
     * Initialize totem with max uses
     */
    public static ItemStack createTotem() {
        ItemStack stack = new ItemStack(ModItems.EMERALD_TOTEM);
        setRemainingUses(stack, MAX_USES);
        return stack;
    }

    /**
     * Get remaining uses from NBT
     */
    public static int getRemainingUses(ItemStack stack) {
        NbtCompound nbt = stack.getOrDefault(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.component.type.NbtComponent.DEFAULT).copyNbt();

        if (!nbt.contains(NBT_USES_KEY)) {
            setRemainingUses(stack, MAX_USES);
            return MAX_USES;
        }
        return nbt.getInt(NBT_USES_KEY);
    }

    /**
     * Set remaining uses in NBT
     */
    public static void setRemainingUses(ItemStack stack, int uses) {
        NbtCompound nbt = stack.getOrDefault(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.component.type.NbtComponent.DEFAULT).copyNbt();
        nbt.putInt(NBT_USES_KEY, uses);
        stack.set(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.component.type.NbtComponent.of(nbt));
    }

    /**
     * Use one charge from totem
     * @return true if totem still has uses, false if depleted
     */
    public static boolean useCharge(ItemStack stack, LivingEntity entity) {
        int remaining = getRemainingUses(stack);

        if (remaining <= 0) {
            return false;
        }

        remaining--;

        if (remaining > 0) {
            setRemainingUses(stack, remaining);
            return true;
        } else {
            // Totem depleted, remove one from stack
            stack.decrement(1);

            // ✅ FIX: Reset uses for remaining items in stack
            if (!stack.isEmpty()) {
                setRemainingUses(stack, MAX_USES);
            }

            return false;
        }
    }

    /**
     * Check if this stack is an Emerald Totem
     */
    public static boolean isEmeraldTotem(ItemStack stack) {
        return stack.getItem() instanceof EmeraldTotemItem;
    }

    /**
     * Add tooltip showing remaining uses
     */
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        int uses = getRemainingUses(stack);

        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("Emerald Totem of Undying")
                .formatted(Formatting.GREEN, Formatting.BOLD));
        tooltip.add(Text.literal("Uses Remaining: " + uses + "/" + MAX_USES)
                .formatted(Formatting.AQUA));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("Prevents death up to 3 times")
                .formatted(Formatting.GRAY, Formatting.ITALIC));
    }

    /**
     * Show durability bar
     */
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    /**
     * Calculate durability bar progress
     */
    @Override
    public int getItemBarStep(ItemStack stack) {
        int uses = getRemainingUses(stack);
        return Math.round(13.0f * uses / MAX_USES);
    }

    /**
     * Durability bar color (green)
     */
    @Override
    public int getItemBarColor(ItemStack stack) {
        int uses = getRemainingUses(stack);
        if (uses == 3) return 0x50C878; // Emerald green
        if (uses == 2) return 0xFFD700; // Gold
        return 0xFF6B6B; // Red
    }
}