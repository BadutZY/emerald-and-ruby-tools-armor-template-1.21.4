package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class ModToolMaterial {

    public static final ToolMaterial EMERALD = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 2151;
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
            return Ingredient.ofItems(Items.EMERALD);
        }
    };

    public static final ToolMaterial BASIC_EMERALD = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 2101;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 8.0f;
        }

        @Override
        public float getAttackDamage() {
            return 3.0f;
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
            return Ingredient.ofItems(Items.EMERALD);
        }
    };

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Emerald Tool Material");
        EmeraldMod.LOGGER.info("  - Durability: 2151 (Superior to Netherite)");
        EmeraldMod.LOGGER.info("  - Mining Speed: 9.0 (Netherite tier)");
        EmeraldMod.LOGGER.info("  - Attack Damage: 4.0 (Netherite tier)");
        EmeraldMod.LOGGER.info("  - Enchantability: 10 (Diamond tier)");
    }
}