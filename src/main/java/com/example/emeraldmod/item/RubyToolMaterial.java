package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class RubyToolMaterial {

    public static final ToolMaterial RUBY = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 10000;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 12.0f;
        }

        @Override
        public float getAttackDamage() {
            return 6.0f;
        }

        @Override
        public TagKey<Block> getInverseTag() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.RUBY);
        }
    };

    public static final ToolMaterial BASIC_RUBY = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 10000;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 9.0f;
        }

        @Override
        public float getAttackDamage() {
            return 4.0f;
        }

        @Override
        public TagKey<Block> getInverseTag() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }

        @Override
        public int getEnchantability() {
            return 10;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.RUBY);
        }
    };

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Ruby Tool Material");
        EmeraldMod.LOGGER.info("  - Durability: UNBREAKABLE (via item class override)");
        EmeraldMod.LOGGER.info("  - Mining Speed: 12.0 (Superior to all tiers)");
        EmeraldMod.LOGGER.info("  - Attack Damage: 6.0 (Superior to all tiers)");
        EmeraldMod.LOGGER.info("  - Enchantability: 15 (Superior enchanting)");
    }
}