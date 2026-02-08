package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(EmeraldMod.MOD_ID, name), item);
    }

    // ============================================
    // RUBY ITEMS (NEW!)
    // ============================================
    public static final Item RUBY = registerItem("ruby",
            new Item(new Item.Settings().fireproof()));

    public static final Item RAW_RUBY = registerItem("raw_ruby",
            new Item(new Item.Settings().fireproof()));

    public static final Item RUBY_INGOT = registerItem("ruby_ingot",
            new Item(new Item.Settings().fireproof()));

    public static final Item RUBY_SCRAP = registerItem("ruby_scrap",
            new Item(new Item.Settings().fireproof()));

    public static final Item RUBY_NUGGET = registerItem("ruby_nugget",
            new Item(new Item.Settings().fireproof()));

    public static final Item EMERALD_CLUSTER = registerItem("emerald_cluster",
            new Item(new Item.Settings().fireproof()));

    public static final Item EMERALD_NUGGET = registerItem("emerald_nugget",
            new Item(new Item.Settings().fireproof()));

    // ============================================
    // RUBY TOOLS (UNBREAKABLE!)
    // ============================================
    public static final SwordItem RUBY_SWORD = registerItem("ruby_sword",
            new RubyToolItem.RubySwordItem(RubyToolMaterial.RUBY,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(RubyToolMaterial.RUBY, 6, -1.5F))
                            .fireproof().maxCount(1)
            ));

    public static final PickaxeItem RUBY_PICKAXE = registerItem("ruby_pickaxe",
            new RubyToolItem.RubyPickaxeItem(RubyToolMaterial.RUBY,
                    new Item.Settings()
                            .attributeModifiers(PickaxeItem.createAttributeModifiers(RubyToolMaterial.RUBY, 4.0F, -2.8F))
                            .fireproof().maxCount(1)
            ));

    public static final AxeItem RUBY_AXE = registerItem("ruby_axe",
            new RubyToolItem.RubyAxeItem(RubyToolMaterial.RUBY,
                    new Item.Settings()
                            .attributeModifiers(AxeItem.createAttributeModifiers(RubyToolMaterial.RUBY, 8.0F, -3.0F))
                            .fireproof().maxCount(1)
            ));

    public static final ShovelItem RUBY_SHOVEL = registerItem("ruby_shovel",
            new RubyToolItem.RubyShovelItem(RubyToolMaterial.RUBY,
                    new Item.Settings()
                            .attributeModifiers(ShovelItem.createAttributeModifiers(RubyToolMaterial.RUBY, 4.5F, -3.0F))
                            .fireproof().maxCount(1)
            ));

    public static final HoeItem RUBY_HOE = registerItem("ruby_hoe",
            new RubyToolItem.RubyHoeItem(RubyToolMaterial.RUBY,
                    new Item.Settings()
                            .attributeModifiers(HoeItem.createAttributeModifiers(RubyToolMaterial.RUBY, -1.0F, 3.0F))
                            .fireproof().maxCount(1)
            ));

    // ============================================
    // RUBY ARMOR (UNBREAKABLE!)
    // ============================================
    public static final RubyArmorItem RUBY_HELMET = registerItem("ruby_helmet",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem RUBY_CHESTPLATE = registerItem("ruby_chestplate",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem RUBY_LEGGINGS = registerItem("ruby_leggings",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem RUBY_BOOTS = registerItem("ruby_boots",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // BASIC RUBY TOOLS (UNBREAKABLE!)
    // ============================================
    public static final SwordItem BASIC_RUBY_SWORD = registerItem("basic_ruby_sword",
            new RubyToolItem.BasicRubySwordItem(RubyToolMaterial.BASIC_RUBY,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(RubyToolMaterial.BASIC_RUBY, 5, -1.5F))
                            .fireproof().maxCount(1)
            ));

    public static final PickaxeItem BASIC_RUBY_PICKAXE = registerItem("basic_ruby_pickaxe",
            new RubyToolItem.BasicRubyPickaxeItem(RubyToolMaterial.BASIC_RUBY,
                    new Item.Settings()
                            .attributeModifiers(PickaxeItem.createAttributeModifiers(RubyToolMaterial.BASIC_RUBY, 3.0F, -2.8F))
                            .fireproof().maxCount(1)
            ));

    public static final AxeItem BASIC_RUBY_AXE = registerItem("basic_ruby_axe",
            new RubyToolItem.BasicRubyAxeItem(RubyToolMaterial.BASIC_RUBY,
                    new Item.Settings()
                            .attributeModifiers(AxeItem.createAttributeModifiers(RubyToolMaterial.BASIC_RUBY, 7.0F, -3.0F))
                            .fireproof().maxCount(1)
            ));

    public static final ShovelItem BASIC_RUBY_SHOVEL = registerItem("basic_ruby_shovel",
            new RubyToolItem.BasicRubyShovelItem(RubyToolMaterial.BASIC_RUBY,
                    new Item.Settings()
                            .attributeModifiers(ShovelItem.createAttributeModifiers(RubyToolMaterial.BASIC_RUBY, 3.5F, -3.0F))
                            .fireproof().maxCount(1)
            ));

    public static final HoeItem BASIC_RUBY_HOE = registerItem("basic_ruby_hoe",
            new RubyToolItem.BasicRubyHoeItem(RubyToolMaterial.BASIC_RUBY,
                    new Item.Settings()
                            .attributeModifiers(HoeItem.createAttributeModifiers(RubyToolMaterial.BASIC_RUBY, -1.0F, 2.0F))
                            .fireproof().maxCount(1)
            ));

    // ============================================
    // BASIC RUBY ARMOR (UNBREAKABLE!)
    // ============================================
    public static final RubyArmorItem BASIC_RUBY_HELMET = registerItem("basic_ruby_helmet",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem BASIC_RUBY_CHESTPLATE = registerItem("basic_ruby_chestplate",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem BASIC_RUBY_LEGGINGS = registerItem("basic_ruby_leggings",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final RubyArmorItem BASIC_RUBY_BOOTS = registerItem("basic_ruby_boots",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // RUBY HORSE ARMOR (UNBREAKABLE!)
    // ============================================
    public static final AnimalArmorItem RUBY_HORSE_ARMOR = registerItem("ruby_horse_armor",
            new AnimalArmorItem(
                    RubyArmorMaterial.RUBY_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    false,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // BASIC EMERALD TOOLS (DENGAN DURABILITY)
    // ============================================
    public static final SwordItem BASIC_EMERALD_SWORD = registerItem("basic_emerald_sword",
            new SwordItem(ModToolMaterial.BASIC_EMERALD,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterial.BASIC_EMERALD, 4, -0.5F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.BASIC_EMERALD.getDurability())
            ));

    public static final PickaxeItem BASIC_EMERALD_PICKAXE = registerItem("basic_emerald_pickaxe",
            new PickaxeItem(ModToolMaterial.BASIC_EMERALD,
                    new Item.Settings()
                            .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterial.BASIC_EMERALD, 2.0F, -2.8F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.BASIC_EMERALD.getDurability())
            ));

    public static final AxeItem BASIC_EMERALD_AXE = registerItem("basic_emerald_axe",
            new AxeItem(ModToolMaterial.BASIC_EMERALD,
                    new Item.Settings()
                            .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterial.BASIC_EMERALD, 6.0F, -3.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.BASIC_EMERALD.getDurability())
            ));

    public static final ShovelItem BASIC_EMERALD_SHOVEL = registerItem("basic_emerald_shovel",
            new ShovelItem(ModToolMaterial.BASIC_EMERALD,
                    new Item.Settings()
                            .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterial.BASIC_EMERALD, 2.5F, -3.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.BASIC_EMERALD.getDurability())
            ));

    public static final HoeItem BASIC_EMERALD_HOE = registerItem("basic_emerald_hoe",
            new HoeItem(ModToolMaterial.BASIC_EMERALD,
                    new Item.Settings()
                            .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterial.BASIC_EMERALD, -2.0F, 0.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.BASIC_EMERALD.getDurability())
            ));

    // ============================================
    // BASIC EMERALD ARMOR (DENGAN DURABILITY)
    // ============================================
    public static final EmeraldArmorItem BASIC_EMERALD_HELMET = registerItem("basic_emerald_helmet",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem BASIC_EMERALD_CHESTPLATE = registerItem("basic_emerald_chestplate",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem BASIC_EMERALD_LEGGINGS = registerItem("basic_emerald_leggings",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem BASIC_EMERALD_BOOTS = registerItem("basic_emerald_boots",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // EMERALD TOOLS (DENGAN DURABILITY)
    // ============================================
    public static final SwordItem EMERALD_SWORD = registerItem("emerald_sword",
            new SwordItem(ModToolMaterial.EMERALD,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterial.EMERALD, 5, -1.5F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.EMERALD.getDurability())
            ));

    public static final PickaxeItem EMERALD_PICKAXE = registerItem("emerald_pickaxe",
            new PickaxeItem(ModToolMaterial.EMERALD,
                    new Item.Settings()
                            .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterial.EMERALD, 3.0F, -2.8F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.EMERALD.getDurability())
            ));

    public static final AxeItem EMERALD_AXE = registerItem("emerald_axe",
            new AxeItem(ModToolMaterial.EMERALD,
                    new Item.Settings()
                            .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterial.EMERALD, 7.0F, -3.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.EMERALD.getDurability())
            ));

    public static final ShovelItem EMERALD_SHOVEL = registerItem("emerald_shovel",
            new ShovelItem(ModToolMaterial.EMERALD,
                    new Item.Settings()
                            .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterial.EMERALD, 3.5F, -3.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.EMERALD.getDurability())
            ));

    public static final HoeItem EMERALD_HOE = registerItem("emerald_hoe",
            new HoeItem(ModToolMaterial.EMERALD,
                    new Item.Settings()
                            .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterial.EMERALD, -1.0F, 2.0F))
                            .fireproof().maxCount(1)
                            .maxDamage(ModToolMaterial.EMERALD.getDurability())
            ));

    // ============================================
    // EMERALD ARMOR (DENGAN DURABILITY)
    // ============================================
    public static final EmeraldArmorItem EMERALD_HELMET = registerItem("emerald_helmet",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem EMERALD_CHESTPLATE = registerItem("emerald_chestplate",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem EMERALD_LEGGINGS = registerItem("emerald_leggings",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    public static final EmeraldArmorItem EMERALD_BOOTS = registerItem("emerald_boots",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // EMERALD HORSE ARMOR (DENGAN DURABILITY)
    // ============================================
    public static final AnimalArmorItem EMERALD_HORSE_ARMOR = registerItem("emerald_horse_armor",
            new AnimalArmorItem(
                    ModArmorMaterial.EMERALD_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    false,
                    new Item.Settings()
                            .fireproof()
                            .maxCount(1)
                            .maxDamage(640)
            ));

    // ============================================
    // NETHERITE HORSE ARMOR
    // ============================================
    public static final AnimalArmorItem NETHERITE_HORSE_ARMOR = registerItem("netherite_horse_armor",
            new AnimalArmorItem(
                    NetheriteArmorMaterial.NETHERITE_HORSE_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    false,
                    new Item.Settings().fireproof().maxCount(1)
            ));

    // ============================================
    // TOTEMS OF UNDYING (NEW!)
    // ============================================
    public static final Item EMERALD_TOTEM = registerItem("emerald_totem",
            new EmeraldTotemItem(new Item.Settings()
                    .maxCount(16)
                    .fireproof()));

    public static final Item RUBY_TOTEM = registerItem("ruby_totem",
            new RubyTotemItem(new Item.Settings()
                    .maxCount(16)
                    .fireproof()));

    public static final Item EMERALD_APPLE = registerItem("emerald_apple",
            new EmeraldAppleItem(new Item.Settings()
                    .fireproof()));

    public static final Item ENCHANTED_EMERALD_APPLE = registerItem("enchanted_emerald_apple",
            new EnchantedEmeraldAppleItem(new Item.Settings()
                    .fireproof()));

    public static final Item RUBY_APPLE = registerItem("ruby_apple",
            new RubyAppleItem(new Item.Settings()
                    .fireproof()));

    public static final Item ENCHANTED_RUBY_APPLE = registerItem("enchanted_ruby_apple",
            new EnchantedRubyAppleItem(new Item.Settings()
                    .fireproof()));

    // ============================================
    // CARROTS (NEW!)
    // ============================================
    public static final Item EMERALD_CARROT = registerItem("emerald_carrot",
            new Item(new Item.Settings()
                    .food(new FoodComponent.Builder()
                            .nutrition(14)
                            .saturationModifier(0.58f)
                            .build())
                    .fireproof()));

    public static final Item RUBY_CARROT = registerItem("ruby_carrot",
            new Item(new Item.Settings()
                    .food(new FoodComponent.Builder()
                            .nutrition(20)
                            .saturationModifier(0.5f)
                            .build())
                    .fireproof()));

    // ============================================
    // SMITHING TEMPLATES
    // ============================================
    public static final Item EMERALD_UPGRADE_SMITHING_TEMPLATE = registerItem(
            "emerald_upgrade_smithing_template",
            new Item(new Item.Settings().maxCount(64)));

    public static final Item RUBY_UPGRADE_SMITHING_TEMPLATE = registerItem(
            "ruby_upgrade_smithing_template",
            new Item(new Item.Settings().maxCount(64)));

    public static void registerModItems() {
        EmeraldMod.LOGGER.info("Registering Mod Items for " + EmeraldMod.MOD_ID);

        RubyToolMaterial.initialize();
        RubyArmorMaterial.initialize();
        ModToolMaterial.initialize();
        ModArmorMaterial.initialize();
        NetheriteArmorMaterial.initialize();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(NETHERITE_HORSE_ARMOR);
        });

        EmeraldMod.LOGGER.info("✓ Successfully registered all Ruby items (UNBREAKABLE)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Scrap (Nether Drop)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Emerald Ingot (Nether Drop)");
        EmeraldMod.LOGGER.info("✓ Successfully registered all Emerald items (WITH DURABILITY)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Netherite Horse Armor");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Upgrade Smithing Template");
        EmeraldMod.LOGGER.info("✓ Successfully registered Emerald Totem (3 uses)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Totem (5 uses)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Emerald Carrot (14 hunger, 14.5 saturation)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Carrot (20 hunger, 20 saturation)");
    }
}