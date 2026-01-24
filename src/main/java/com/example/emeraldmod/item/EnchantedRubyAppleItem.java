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
 * Enchanted Ruby Apple - THE ULTIMATE APPLE!
 * Better than Enchanted Emerald Apple
 * Has enchantment glint effect
 */
public class EnchantedRubyAppleItem extends Item {

    private static final FoodComponent ENCHANTED_RUBY_APPLE_FOOD = new FoodComponent.Builder()
            .nutrition(8)  // Even more nutrition
            .saturationModifier(2.0f)  // Maximum saturation
            .alwaysEdible()
            .build();

    public EnchantedRubyAppleItem(Settings settings) {
        super(settings.food(ENCHANTED_RUBY_APPLE_FOOD).rarity(Rarity.EPIC));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            // Enchanted Ruby Apple Effects (THE BEST!)

            // Regeneration VI for 45 seconds (Enchanted Emerald: Regen V for 30s)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 900, 5
            ));

            // Absorption V for 5 minutes (Enchanted Emerald: Absorption IV for 3min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 6000, 4
            ));

            // Fire Resistance for 10 minutes (Enchanted Emerald: 8min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 12000, 0
            ));

            // Resistance III for 10 minutes (Enchanted Emerald: Resistance II for 8min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 12000, 2
            ));

            // Health Boost III for 5 minutes (Enchanted Emerald: Health Boost II for 3min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HEALTH_BOOST, 6000, 2
            ));

            // Strength III for 3 minutes (Enchanted Emerald: Strength II for 2min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 3600, 2
            ));

            // Speed III for 3 minutes (Enchanted Emerald: Speed II for 2min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 3600, 2
            ));

            // Haste II for 3 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HASTE, 3600, 1
            ));

            // Jump Boost II for 3 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST, 3600, 1
            ));

            // Night Vision for 5 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 6000, 0
            ));

            // Water Breathing for 5 minutes (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WATER_BREATHING, 6000, 0
            ));

            // Play epic sound
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundCategory.PLAYERS,
                    1.0F,
                    0.8F
            );
        }

        return result;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Always has enchantment glint
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("When consumed:")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("  • Regeneration VI (45s)")
                .formatted(Formatting.GREEN));
        tooltip.add(Text.literal("  • Absorption V (5m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Fire Resistance (10m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Resistance III (10m)")
                .formatted(Formatting.AQUA));
        tooltip.add(Text.literal("  • Health Boost III (5m)")
                .formatted(Formatting.RED));
        tooltip.add(Text.literal("  • Strength III (3m)")
                .formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("  • Speed III (3m)")
                .formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("  • Haste II (3m)")
                .formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("  • Jump Boost II (3m)")
                .formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("  • Night Vision (5m)")
                .formatted(Formatting.BLUE));
        tooltip.add(Text.literal("  • Water Breathing (5m)")
                .formatted(Formatting.BLUE));
    }
}