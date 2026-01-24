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
import net.minecraft.world.World;

import java.util.List;

/**
 * Ruby Apple - Better than Emerald Apple, not as good as Enchanted Golden Apple
 */
public class RubyAppleItem extends Item {

    private static final FoodComponent RUBY_APPLE_FOOD = new FoodComponent.Builder()
            .nutrition(6)
            .saturationModifier(1.6f)
            .alwaysEdible()
            .build();

    public RubyAppleItem(Settings settings) {
        super(settings.food(RUBY_APPLE_FOOD));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            // Ruby Apple Effects (Better than Emerald Apple, not quite Enchanted Golden)

            // Regeneration IV for 10 seconds (Emerald: Regen III for 8s)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 200, 3
            ));

            // Absorption III for 2.5 minutes (Emerald: Absorption II for 2min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 3000, 2
            ));

            // Fire Resistance for 6 minutes (Emerald: 5min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 7200, 0
            ));

            // Resistance I for 1 minute (Emerald: 30s)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 1200, 0
            ));

            // Health Boost I for 1 minute (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HEALTH_BOOST, 1200, 0
            ));

            // Strength I for 1 minute (NEW)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 1200, 0
            ));

            // Play eating sound
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENTITY_PLAYER_BURP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("When consumed:")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("  • Regeneration IV (10s)")
                .formatted(Formatting.GREEN));
        tooltip.add(Text.literal("  • Absorption III (2.5m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Fire Resistance (6m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Resistance (1m)")
                .formatted(Formatting.AQUA));
        tooltip.add(Text.literal("  • Health Boost (1m)")
                .formatted(Formatting.RED));
        tooltip.add(Text.literal("  • Strength (1m)")
                .formatted(Formatting.LIGHT_PURPLE));
    }
}