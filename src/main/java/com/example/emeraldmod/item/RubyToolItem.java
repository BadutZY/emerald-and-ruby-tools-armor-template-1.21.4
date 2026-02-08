package com.example.emeraldmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class RubyToolItem {

    public static class RubySwordItem extends SwordItem {
        public RubySwordItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class RubyPickaxeItem extends PickaxeItem {
        public RubyPickaxeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class RubyAxeItem extends AxeItem {
        public RubyAxeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class RubyShovelItem extends ShovelItem {
        public RubyShovelItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class RubyHoeItem extends HoeItem {
        public RubyHoeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class BasicRubySwordItem extends SwordItem {
        public BasicRubySwordItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class BasicRubyPickaxeItem extends PickaxeItem {
        public BasicRubyPickaxeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class BasicRubyAxeItem extends AxeItem {
        public BasicRubyAxeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class BasicRubyShovelItem extends ShovelItem {
        public BasicRubyShovelItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }

    public static class BasicRubyHoeItem extends HoeItem {
        public BasicRubyHoeItem(ToolMaterial material, Settings settings) {
            super(material, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            if (stack.getDamage() > 0) {
                stack.setDamage(0);
            }
        }
    }
}