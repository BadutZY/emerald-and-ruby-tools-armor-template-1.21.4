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
 * Emerald Apple - Better than Golden Apple, not as good as Enchanted Golden Apple
 */
public class EmeraldAppleItem extends Item {

    private static final FoodComponent EMERALD_APPLE_FOOD = new FoodComponent.Builder()
            .nutrition(6)  // Golden Apple = 4, Enchanted = 4
            .saturationModifier(1.6f)  // Golden Apple = 1.2f, Enchanted = 1.2f
            .alwaysEdible()
            .build();

    public EmeraldAppleItem(Settings settings) {
        super(settings.food(EMERALD_APPLE_FOOD));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            // Emerald Apple Effects (Better than Golden Apple)

            // Regeneration III for 8 seconds (Golden Apple: Regen II for 5s)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 160, 2
            ));

            // Absorption II for 2 minutes (Golden Apple: Absorption I for 2min)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 2400, 1
            ));

            // Fire Resistance for 5 minutes (NEW - Golden Apple doesn't have this)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 6000, 0
            ));

            // Resistance I for 30 seconds (NEW - Golden Apple doesn't have this)
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 600, 0
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
        tooltip.add(Text.literal("  • Regeneration III (8s)")
                .formatted(Formatting.GREEN));
        tooltip.add(Text.literal("  • Absorption II (2m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Fire Resistance (5m)")
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("  • Resistance (30s)")
                .formatted(Formatting.AQUA));
    }
}