package com.example.emeraldmod.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Enchanted Emerald Apple - Better than Enchanted Golden Apple
 * Has enchantment glint effect
 */
public class EnchantedEmeraldAppleItem extends Item {

    private static final FoodComponent ENCHANTED_EMERALD_APPLE_FOOD = new FoodComponent.Builder()
            .nutrition(6)
            .saturationModifier(1.6f)
            .alwaysEdible()
            .build();

    public EnchantedEmeraldAppleItem(Settings settings) {
        super(settings.food(ENCHANTED_EMERALD_APPLE_FOOD).rarity(Rarity.EPIC));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            // Enchanted Emerald Apple Effects (Better than Enchanted Golden Apple)

            // Regeneration V for 30 seconds (Enchanted Golden: Regen II for 20s)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 600, 4
            ));

            // Absorption IV for 3 minutes (Enchanted Golden: Absorption IV for 2min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 3600, 3
            ));

            // Fire Resistance for 8 minutes (Enchanted Golden: Fire Resistance for 5min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 9600, 0
            ));

            // Resistance II for 8 minutes (Enchanted Golden: Resistance I for 5min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 9600, 1
            ));

            // Health Boost II for 3 minutes (NEW - Better than Enchanted Golden Apple)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HEALTH_BOOST, 3600, 1
            ));

            // Strength II for 2 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 2400, 1
            ));

            // Speed II for 2 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 2400, 1
            ));

            // Play special sound
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        return result;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Always has enchantment glint like Enchanted Golden Apple
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("When consumed:")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("  • Regeneration V (30s)")
                .formatted(Formatting.GREEN));
        tooltip.add(Text.literal("  • Absorption IV (3m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Fire Resistance (8m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Resistance II (8m)")
                .formatted(Formatting.AQUA));
        tooltip.add(Text.literal("  • Health Boost II (3m)")
                .formatted(Formatting.RED));
        tooltip.add(Text.literal("  • Strength II (2m)")
                .formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("  • Speed II (2m)")
                .formatted(Formatting.LIGHT_PURPLE));
    }
}