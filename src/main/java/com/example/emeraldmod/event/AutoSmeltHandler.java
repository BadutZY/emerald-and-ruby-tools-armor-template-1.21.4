package com.example.emeraldmod.event;

import com.example.emeraldmod.block.ModBlocks;
import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.item.ModItems;
import com.example.emeraldmod.state.EffectStateManager;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class AutoSmeltHandler {

    private static final Random RANDOM = new Random();

    // Maximum ore blocks untuk vein mining (Ruby Pickaxe only)
    private static final int MAX_VEIN_SIZE = 64;

    // Radius pencarian ore untuk vein mining (Ruby Pickaxe only)
    private static final int SEARCH_RADIUS = 3;

    // Map ore block ke smelted item (untuk auto-smelt)
    private static final Map<Block, ItemStack> ORE_TO_INGOT = new HashMap<>();

    // Map ore block ke raw item (untuk Silk Touch)
    private static final Map<Block, ItemStack> ORE_TO_RAW = new HashMap<>();

    // Set semua ore blocks yang bisa auto-smelt
    private static final Set<Block> SMELTABLE_ORES = new HashSet<>();

    // Set ore blocks yang bisa vein mining tapi TIDAK auto-smelt
    private static final Set<Block> VEIN_MINING_ONLY_ORES = new HashSet<>();

    static {
        // ===== SMELTABLE ORES (dengan Silk Touch drop ore block) =====

        // Copper Ore
        ORE_TO_INGOT.put(Blocks.COPPER_ORE, new ItemStack(Items.COPPER_INGOT));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_COPPER_ORE, new ItemStack(Items.COPPER_INGOT));
        ORE_TO_RAW.put(Blocks.COPPER_ORE, new ItemStack(Blocks.COPPER_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_COPPER_ORE, new ItemStack(Blocks.DEEPSLATE_COPPER_ORE));

        // Iron Ore
        ORE_TO_INGOT.put(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_IRON_ORE, new ItemStack(Items.IRON_INGOT));
        ORE_TO_RAW.put(Blocks.IRON_ORE, new ItemStack(Blocks.IRON_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_IRON_ORE, new ItemStack(Blocks.DEEPSLATE_IRON_ORE));

        // Gold Ore
        ORE_TO_INGOT.put(Blocks.GOLD_ORE, new ItemStack(Items.GOLD_INGOT));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_GOLD_ORE, new ItemStack(Items.GOLD_INGOT));
        ORE_TO_RAW.put(Blocks.GOLD_ORE, new ItemStack(Blocks.GOLD_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_GOLD_ORE, new ItemStack(Blocks.DEEPSLATE_GOLD_ORE));

        // Ancient Debris -> Netherite Scrap
        ORE_TO_INGOT.put(Blocks.ANCIENT_DEBRIS, new ItemStack(Items.NETHERITE_SCRAP));
        ORE_TO_RAW.put(Blocks.ANCIENT_DEBRIS, new ItemStack(Blocks.ANCIENT_DEBRIS));

        // Coal Ore
        ORE_TO_INGOT.put(Blocks.COAL_ORE, new ItemStack(Items.COAL));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_COAL_ORE, new ItemStack(Items.COAL));
        ORE_TO_RAW.put(Blocks.COAL_ORE, new ItemStack(Blocks.COAL_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_COAL_ORE, new ItemStack(Blocks.DEEPSLATE_COAL_ORE));

        // Diamond Ore
        ORE_TO_INGOT.put(Blocks.DIAMOND_ORE, new ItemStack(Items.DIAMOND));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_DIAMOND_ORE, new ItemStack(Items.DIAMOND));
        ORE_TO_RAW.put(Blocks.DIAMOND_ORE, new ItemStack(Blocks.DIAMOND_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_DIAMOND_ORE, new ItemStack(Blocks.DEEPSLATE_DIAMOND_ORE));

        // Emerald Ore
        ORE_TO_INGOT.put(Blocks.EMERALD_ORE, new ItemStack(Items.EMERALD));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_EMERALD_ORE, new ItemStack(Items.EMERALD));
        ORE_TO_RAW.put(Blocks.EMERALD_ORE, new ItemStack(Blocks.EMERALD_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_EMERALD_ORE, new ItemStack(Blocks.DEEPSLATE_EMERALD_ORE));

        // Lapis Ore
        ORE_TO_INGOT.put(Blocks.LAPIS_ORE, new ItemStack(Items.LAPIS_LAZULI));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_LAPIS_ORE, new ItemStack(Items.LAPIS_LAZULI));
        ORE_TO_RAW.put(Blocks.LAPIS_ORE, new ItemStack(Blocks.LAPIS_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_LAPIS_ORE, new ItemStack(Blocks.DEEPSLATE_LAPIS_ORE));

        // Redstone Ore
        ORE_TO_INGOT.put(Blocks.REDSTONE_ORE, new ItemStack(Items.REDSTONE));
        ORE_TO_INGOT.put(Blocks.DEEPSLATE_REDSTONE_ORE, new ItemStack(Items.REDSTONE));
        ORE_TO_RAW.put(Blocks.REDSTONE_ORE, new ItemStack(Blocks.REDSTONE_ORE));
        ORE_TO_RAW.put(Blocks.DEEPSLATE_REDSTONE_ORE, new ItemStack(Blocks.DEEPSLATE_REDSTONE_ORE));

        // Nether Quartz Ore
        ORE_TO_INGOT.put(Blocks.NETHER_QUARTZ_ORE, new ItemStack(Items.QUARTZ));
        ORE_TO_RAW.put(Blocks.NETHER_QUARTZ_ORE, new ItemStack(Blocks.NETHER_QUARTZ_ORE));

        // Ruby Ores (Overworld - auto-smelt)
        ORE_TO_INGOT.put(ModBlocks.RUBY_ORE, new ItemStack(ModItems.RUBY_INGOT));
        ORE_TO_INGOT.put(ModBlocks.DEEPSLATE_RUBY_ORE, new ItemStack(ModItems.RUBY_INGOT));
        ORE_TO_RAW.put(ModBlocks.RUBY_ORE, new ItemStack(ModBlocks.RUBY_ORE));
        ORE_TO_RAW.put(ModBlocks.DEEPSLATE_RUBY_ORE, new ItemStack(ModBlocks.DEEPSLATE_RUBY_ORE));

        // Ruby Debris
        ORE_TO_INGOT.put(ModBlocks.RUBY_DEBRIS, new ItemStack(ModItems.RUBY_SCRAP));
        ORE_TO_RAW.put(ModBlocks.RUBY_DEBRIS, new ItemStack(ModBlocks.RUBY_DEBRIS));

        // Populate SMELTABLE_ORES set
        SMELTABLE_ORES.addAll(ORE_TO_INGOT.keySet());

        // ===== VEIN MINING ONLY ORES (NO auto-smelt) =====
        VEIN_MINING_ONLY_ORES.add(ModBlocks.NETHER_RUBY_ORE);
        VEIN_MINING_ONLY_ORES.add(Blocks.NETHER_GOLD_ORE);
        VEIN_MINING_ONLY_ORES.add(ModBlocks.NETHER_EMERALD_ORE);
    }

    public static void register() {
        // Gunakan BEFORE untuk cancel drop default
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            ItemStack tool = player.getMainHandStack();
            boolean isEmeraldPickaxe = tool.getItem() == ModItems.EMERALD_PICKAXE;
            boolean isRubyPickaxe = tool.getItem() == ModItems.RUBY_PICKAXE;

            // Cek apakah menggunakan Emerald atau Ruby Pickaxe
            if (isEmeraldPickaxe || isRubyPickaxe) {
                // Check tools effect enabled/disabled
                if (!world.isClient) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    EffectStateManager stateManager = EffectStateManager.getServerState(serverWorld.getServer());

                    if (!stateManager.isToolsEnabled(player.getUuid())) {
                        // Tools effect DISABLED, biarkan break normal
                        EmeraldMod.LOGGER.debug("Auto-smelt disabled for player {}", player.getName().getString());
                        return true;
                    }
                }

                Block block = state.getBlock();

                // Handle ores yang vein mining only (NO auto-smelt)
                if (VEIN_MINING_ONLY_ORES.contains(block)) {
                    if (isRubyPickaxe) {
                        // Ruby Pickaxe: Vein Mining tanpa auto-smelt
                        handleVeinMiningNoSmelt(world, player, pos, state, tool);
                        return false; // Cancel default drop
                    } else {
                        // Emerald Pickaxe: Break normal (biarkan loot table handle drop)
                        return true;
                    }
                }

                // Handle regular ores dengan auto-smelt
                if (SMELTABLE_ORES.contains(block)) {
                    if (isRubyPickaxe) {
                        // Ruby Pickaxe: Auto-smelt + Vein Mining (respects Silk Touch)
                        handleVeinMining(world, player, pos, state, tool);
                    } else {
                        // Emerald Pickaxe: Auto-smelt only single block (respects Silk Touch)
                        handleSingleBlockSmelt(world, player, pos, state, tool);
                    }
                    return false; // Cancel default drop
                }
            }
            return true; // Allow normal drop
        });

        EmeraldMod.LOGGER.info("✓ Registered Auto-Smelt Handler (Toggleable + Enchantment Support)");
        EmeraldMod.LOGGER.info("  - Emerald Pickaxe: Auto-smelt only (single block)");
        EmeraldMod.LOGGER.info("  - Ruby Pickaxe: Auto-smelt + Vein Mining");
        EmeraldMod.LOGGER.info("  - Full support for Fortune & Silk Touch enchantments");
        EmeraldMod.LOGGER.info("  - Supports all vanilla ores + custom Ruby ores");
    }

    /**
     * Handle single block auto-smelt (Emerald Pickaxe)
     * Supports Fortune & Silk Touch
     * ✅ FIXED: Items dan XP drop seperti vanilla, bukan langsung ke inventory
     */
    private static void handleSingleBlockSmelt(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;
        Block block = state.getBlock();

        // Get enchantment levels
        int fortuneLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.FORTUNE),
                tool
        );

        int silkTouchLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH),
                tool
        );

        ItemStack dropItem;
        int dropCount;
        int experience = 0;

        // Silk Touch: drop ore block
        if (silkTouchLevel > 0) {
            dropItem = ORE_TO_RAW.get(block).copy();
            dropCount = 1;
            experience = 0; // Silk Touch tidak drop XP
        } else {
            // Auto-smelt: drop smelted item dengan Fortune
            dropCount = calculateDropCount(block, fortuneLevel);
            dropItem = ORE_TO_INGOT.get(block).copy();
            experience = calculateExperience(block, dropCount);
        }

        dropItem.setCount(dropCount);

        // ✅ FIXED: Remove block dan biarkan vanilla handle break sound
        serverWorld.removeBlock(pos, false);

        // ✅ FIXED: Spawn item di posisi block dengan random offset seperti vanilla
        Vec3d dropPos = new Vec3d(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );

        ItemEntity itemEntity = new ItemEntity(
                serverWorld,
                dropPos.x,
                dropPos.y,
                dropPos.z,
                dropItem
        );

        // ✅ FIXED: Set velocity lebih dekat seperti vanilla (reduced spread)
        double velocityX = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05
        double velocityY = RANDOM.nextDouble() * 0.1 + 0.15;  // 0.15 to 0.25
        double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05

        itemEntity.setVelocity(velocityX, velocityY, velocityZ);
        serverWorld.spawnEntity(itemEntity);

        // ✅ FIXED: Spawn XP orbs seperti vanilla (tidak langsung ke player)
        if (experience > 0) {
            ExperienceOrbEntity.spawn(serverWorld, dropPos, experience);
        }

        // Damage tool (1 durability per block)
        tool.damage(1, player, net.minecraft.entity.EquipmentSlot.MAINHAND);

        EmeraldMod.LOGGER.debug("Single block {}: {} -> {} x{} + {} XP (Fortune {}, Silk Touch {})",
                silkTouchLevel > 0 ? "mined" : "auto-smelt",
                block.getName().getString(),
                dropItem.getItem().getName().getString(),
                dropCount,
                experience,
                fortuneLevel,
                silkTouchLevel);
    }

    /**
     * Handle vein mining dengan auto-smelt (Ruby Pickaxe untuk ore biasa)
     * Supports Fortune & Silk Touch
     * ✅ FIXED: Items dan XP drop seperti vanilla, sound hanya sekali
     */
    private static void handleVeinMining(World world, PlayerEntity player, BlockPos startPos, BlockState startState, ItemStack tool) {
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;
        Block targetOre = startState.getBlock();

        // Set untuk track blocks yang sudah di-process
        Set<BlockPos> processedBlocks = new HashSet<>();

        // Queue untuk BFS (Breadth-First Search)
        Queue<BlockPos> toProcess = new LinkedList<>();
        toProcess.add(startPos);
        processedBlocks.add(startPos);

        // Counter untuk total ores
        int totalOresMined = 0;

        // Map untuk track total drops
        Map<ItemStack, Integer> totalDrops = new HashMap<>();

        // Get enchantment levels
        int fortuneLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.FORTUNE),
                tool
        );

        int silkTouchLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH),
                tool
        );

        int totalExperience = 0;

        EmeraldMod.LOGGER.debug("Starting vein mining for {} at {} (Fortune {}, Silk Touch {})",
                targetOre.getName().getString(), startPos, fortuneLevel, silkTouchLevel);

        // BFS untuk menemukan semua ore yang connected
        while (!toProcess.isEmpty() && totalOresMined < MAX_VEIN_SIZE) {
            BlockPos currentPos = toProcess.poll();
            BlockState currentState = serverWorld.getBlockState(currentPos);
            Block currentBlock = currentState.getBlock();

            // Cek apakah block ini adalah ore yang sama dengan target
            if (currentBlock == targetOre && SMELTABLE_ORES.contains(currentBlock)) {
                // Process ore ini
                totalOresMined++;

                ItemStack dropItem;
                int dropCount;

                // Silk Touch: drop ore block
                if (silkTouchLevel > 0) {
                    dropItem = ORE_TO_RAW.get(currentBlock).copy();
                    dropCount = 1;
                } else {
                    // Auto-smelt: drop smelted item dengan Fortune
                    dropCount = calculateDropCount(currentBlock, fortuneLevel);
                    dropItem = ORE_TO_INGOT.get(currentBlock).copy();

                    // Add experience (only if not Silk Touch)
                    int experience = calculateExperience(currentBlock, dropCount);
                    totalExperience += experience;
                }

                // Tambahkan ke total drops
                addToTotalDrops(totalDrops, dropItem, dropCount);

                // ✅ FIXED: Remove block tanpa play sound (nanti play sekali saja)
                serverWorld.removeBlock(currentPos, false);

                // Cari ore di sekitar block ini
                for (BlockPos neighborPos : getNeighborPositions(currentPos)) {
                    if (!processedBlocks.contains(neighborPos) &&
                            isWithinSearchRadius(startPos, neighborPos)) {

                        BlockState neighborState = serverWorld.getBlockState(neighborPos);
                        Block neighborBlock = neighborState.getBlock();

                        // Hanya tambahkan jika block tetangga adalah ore yang SAMA
                        if (neighborBlock == targetOre) {
                            toProcess.add(neighborPos);
                            processedBlocks.add(neighborPos);
                        }
                    }
                }
            }
        }

        // ✅ FIXED: Play break sound SEKALI SAJA di posisi awal
        serverWorld.playSound(
                null,
                startPos,
                startState.getSoundGroup().getBreakSound(),
                SoundCategory.BLOCKS,
                (startState.getSoundGroup().getVolume() + 1.0F) / 2.0F,
                startState.getSoundGroup().getPitch() * 0.8F
        );

        // ✅ FIXED: Drop semua items di posisi awal dengan random spread
        Vec3d dropCenter = new Vec3d(
                startPos.getX() + 0.5,
                startPos.getY() + 0.5,
                startPos.getZ() + 0.5
        );

        if (!totalDrops.isEmpty()) {
            for (Map.Entry<ItemStack, Integer> entry : totalDrops.entrySet()) {
                ItemStack itemToDrop = entry.getKey().copy();
                int totalCount = entry.getValue();

                // Split into multiple stacks if needed
                int maxStackSize = itemToDrop.getMaxCount();
                while (totalCount > 0) {
                    int stackSize = Math.min(totalCount, maxStackSize);
                    ItemStack dropStack = itemToDrop.copy();
                    dropStack.setCount(stackSize);

                    // ✅ FIXED: Spawn dengan random offset lebih dekat (reduced spread)
                    double offsetX = (RANDOM.nextDouble() - 0.5) * 0.2; // ±0.1 block
                    double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.2; // ±0.1 block

                    ItemEntity itemEntity = new ItemEntity(
                            serverWorld,
                            dropCenter.x + offsetX,
                            dropCenter.y,
                            dropCenter.z + offsetZ,
                            dropStack
                    );

                    // ✅ FIXED: Velocity lebih dekat seperti vanilla
                    double velocityX = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05
                    double velocityY = RANDOM.nextDouble() * 0.1 + 0.15;  // 0.15 to 0.25
                    double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05

                    itemEntity.setVelocity(velocityX, velocityY, velocityZ);
                    serverWorld.spawnEntity(itemEntity);

                    totalCount -= stackSize;
                }
            }
        }

        // ✅ FIXED: Spawn XP orbs di posisi awal (tidak langsung ke player)
        if (totalExperience > 0) {
            ExperienceOrbEntity.spawn(serverWorld, dropCenter, totalExperience);
        }

        // Damage tool berdasarkan jumlah ore yang di-mine
        int durabilityDamage = Math.min(totalOresMined, tool.getMaxDamage() - tool.getDamage());
        if (durabilityDamage > 0) {
            tool.damage(durabilityDamage, player, net.minecraft.entity.EquipmentSlot.MAINHAND);
        }

        EmeraldMod.LOGGER.info("Vein mining completed: {} {} ores mined + {} XP (Fortune {}, Silk Touch {})",
                totalOresMined,
                targetOre.getName().getString(),
                totalExperience,
                fortuneLevel,
                silkTouchLevel);
    }

    /**
     * Handle vein mining TANPA auto-smelt (Ruby Pickaxe untuk Nether Ruby Ore & Nether Gold Ore)
     * Supports Fortune & Silk Touch
     * ✅ FIXED: Items dan XP drop seperti vanilla, sound hanya sekali
     */
    private static void handleVeinMiningNoSmelt(World world, PlayerEntity player, BlockPos startPos, BlockState startState, ItemStack tool) {
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;
        Block targetOre = startState.getBlock();

        // Set untuk track blocks yang sudah di-process
        Set<BlockPos> processedBlocks = new HashSet<>();

        // Queue untuk BFS (Breadth-First Search)
        Queue<BlockPos> toProcess = new LinkedList<>();
        toProcess.add(startPos);
        processedBlocks.add(startPos);

        // Counter untuk total ores
        int totalOresMined = 0;

        // Get enchantment levels
        int fortuneLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.FORTUNE),
                tool
        );

        int silkTouchLevel = EnchantmentHelper.getLevel(
                world.getRegistryManager().getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH),
                tool
        );

        EmeraldMod.LOGGER.debug("Starting vein mining (NO SMELT) for {} at {} (Fortune {}, Silk Touch {})",
                targetOre.getName().getString(), startPos, fortuneLevel, silkTouchLevel);

        // Total drops dan XP
        int totalDrops = 0;
        int totalExperience = 0;
        ItemStack dropItem = null;

        // BFS untuk menemukan semua ore yang connected
        while (!toProcess.isEmpty() && totalOresMined < MAX_VEIN_SIZE) {
            BlockPos currentPos = toProcess.poll();
            BlockState currentState = serverWorld.getBlockState(currentPos);
            Block currentBlock = currentState.getBlock();

            // Cek apakah block ini adalah ore yang sama dengan target
            if (currentBlock == targetOre && VEIN_MINING_ONLY_ORES.contains(currentBlock)) {
                // Process ore ini
                totalOresMined++;

                // Silk Touch: drop ore block langsung
                if (silkTouchLevel > 0) {
                    // ✅ FIXED: Drop lebih dekat seperti vanilla
                    Vec3d dropPos = new Vec3d(
                            currentPos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.1,
                            currentPos.getY() + 0.5,
                            currentPos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.1
                    );

                    ItemStack blockDrop = new ItemStack(currentBlock);
                    ItemEntity itemEntity = new ItemEntity(
                            serverWorld,
                            dropPos.x,
                            dropPos.y,
                            dropPos.z,
                            blockDrop
                    );

                    double velocityX = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05
                    double velocityY = RANDOM.nextDouble() * 0.1 + 0.15;  // 0.15 to 0.25
                    double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05

                    itemEntity.setVelocity(velocityX, velocityY, velocityZ);
                    serverWorld.spawnEntity(itemEntity);
                } else {
                    // Calculate drops untuk specific ore
                    if (currentBlock == ModBlocks.NETHER_RUBY_ORE) {
                        int scrapCount = calculateNetherRubyScrapDrop(fortuneLevel);
                        totalDrops += scrapCount;
                        dropItem = new ItemStack(ModItems.RUBY_NUGGET);

                        if (RANDOM.nextBoolean()) {
                            totalExperience += 3;
                        }
                    }
                    else if (currentBlock == ModBlocks.NETHER_EMERALD_ORE) {
                        int nuggetCount = calculateNetherEmeraldNuggetDrop(fortuneLevel);
                        totalDrops += nuggetCount;
                        dropItem = new ItemStack(ModItems.EMERALD_NUGGET);

                        if (RANDOM.nextBoolean()) {
                            totalExperience += 3;
                        }
                    }
                    else if (currentBlock == Blocks.NETHER_GOLD_ORE) {
                        int nuggetCount = calculateNetherGoldNuggetDrop(fortuneLevel);
                        totalDrops += nuggetCount;
                        dropItem = new ItemStack(Items.GOLD_NUGGET);
                    }
                }

                // ✅ FIXED: Remove block tanpa sound
                serverWorld.removeBlock(currentPos, false);

                // Cari ore di sekitar block ini
                for (BlockPos neighborPos : getNeighborPositions(currentPos)) {
                    if (!processedBlocks.contains(neighborPos) &&
                            isWithinSearchRadius(startPos, neighborPos)) {

                        BlockState neighborState = serverWorld.getBlockState(neighborPos);
                        Block neighborBlock = neighborState.getBlock();

                        // Hanya tambahkan jika block tetangga adalah ore yang SAMA
                        if (neighborBlock == targetOre) {
                            toProcess.add(neighborPos);
                            processedBlocks.add(neighborPos);
                        }
                    }
                }
            }
        }

        // ✅ FIXED: Play break sound SEKALI SAJA
        serverWorld.playSound(
                null,
                startPos,
                startState.getSoundGroup().getBreakSound(),
                SoundCategory.BLOCKS,
                (startState.getSoundGroup().getVolume() + 1.0F) / 2.0F,
                startState.getSoundGroup().getPitch() * 0.8F
        );

        // ✅ FIXED: Drop all items di posisi awal dengan random spread
        Vec3d dropCenter = new Vec3d(
                startPos.getX() + 0.5,
                startPos.getY() + 0.5,
                startPos.getZ() + 0.5
        );

        if (totalDrops > 0 && dropItem != null) {
            int maxStackSize = dropItem.getMaxCount();

            while (totalDrops > 0) {
                int stackSize = Math.min(totalDrops, maxStackSize);
                ItemStack dropStack = dropItem.copy();
                dropStack.setCount(stackSize);

                // ✅ FIXED: Random offset lebih dekat (reduced spread)
                double offsetX = (RANDOM.nextDouble() - 0.5) * 0.2; // ±0.1 block
                double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.2; // ±0.1 block

                ItemEntity itemEntity = new ItemEntity(
                        serverWorld,
                        dropCenter.x + offsetX,
                        dropCenter.y,
                        dropCenter.z + offsetZ,
                        dropStack
                );

                double velocityX = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05
                double velocityY = RANDOM.nextDouble() * 0.1 + 0.15;  // 0.15 to 0.25
                double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.1; // -0.05 to 0.05

                itemEntity.setVelocity(velocityX, velocityY, velocityZ);
                serverWorld.spawnEntity(itemEntity);
                totalDrops -= stackSize;
            }
        }

        // ✅ FIXED: Spawn XP orbs di posisi awal
        if (totalExperience > 0) {
            ExperienceOrbEntity.spawn(serverWorld, dropCenter, totalExperience);
        }

        // Damage tool berdasarkan jumlah ore yang di-mine
        int durabilityDamage = Math.min(totalOresMined, tool.getMaxDamage() - tool.getDamage());
        if (durabilityDamage > 0) {
            tool.damage(durabilityDamage, player, net.minecraft.entity.EquipmentSlot.MAINHAND);
        }

        EmeraldMod.LOGGER.info("Vein mining (NO SMELT) completed: {} {} ores + {} XP (Fortune {}, Silk Touch {})",
                totalOresMined,
                targetOre.getName().getString(),
                totalExperience,
                fortuneLevel,
                silkTouchLevel);
    }

    /**
     * Calculate drop count untuk Nether Ruby Ore (Ruby Scrap)
     * Base: 2-4, affected by Fortune
     */
    private static int calculateNetherRubyScrapDrop(int fortuneLevel) {
        int baseCount = RANDOM.nextInt(3) + 2; // 2-4

        if (fortuneLevel > 0) {
            int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
            baseCount += fortuneBonus;
        }

        return Math.max(2, baseCount);
    }

    /**
     * Calculate drop count untuk Nether Gold Ore (Gold Nugget)
     * Base: 2-6, up to 24 with Fortune III (vanilla behavior)
     */
    private static int calculateNetherGoldNuggetDrop(int fortuneLevel) {
        int baseCount = RANDOM.nextInt(5) + 2; // 2-6

        if (fortuneLevel > 0) {
            int maxBonus = fortuneLevel * 4;
            int fortuneBonus = RANDOM.nextInt(maxBonus + 1);
            baseCount += fortuneBonus;
        }

        return Math.max(2, baseCount);
    }

    /**
     * Calculate drop count untuk Nether Emerald Ore (Emerald Nugget)
     * Base: 2-4, affected by Fortune
     */
    private static int calculateNetherEmeraldNuggetDrop(int fortuneLevel) {
        int baseCount = RANDOM.nextInt(3) + 2; // 2-4

        if (fortuneLevel > 0) {
            int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
            baseCount += fortuneBonus;
        }

        return Math.max(2, baseCount);
    }

    /**
     * Helper method untuk menambahkan drops ke total
     */
    private static void addToTotalDrops(Map<ItemStack, Integer> totalDrops, ItemStack item, int count) {
        boolean found = false;
        for (Map.Entry<ItemStack, Integer> entry : totalDrops.entrySet()) {
            if (ItemStack.areItemsEqual(entry.getKey(), item)) {
                entry.setValue(entry.getValue() + count);
                found = true;
                break;
            }
        }
        if (!found) {
            totalDrops.put(item.copy(), count);
        }
    }

    /**
     * Get semua posisi tetangga (26 blocks di sekitar - 3x3x3 minus center)
     */
    private static List<BlockPos> getNeighborPositions(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    neighbors.add(pos.add(x, y, z));
                }
            }
        }
        return neighbors;
    }

    /**
     * Cek apakah posisi masih dalam radius pencarian
     */
    private static boolean isWithinSearchRadius(BlockPos start, BlockPos current) {
        return Math.abs(current.getX() - start.getX()) <= SEARCH_RADIUS &&
                Math.abs(current.getY() - start.getY()) <= SEARCH_RADIUS &&
                Math.abs(current.getZ() - start.getZ()) <= SEARCH_RADIUS;
    }

    /**
     * Calculate drop count untuk ore dengan auto-smelt (vanilla-like)
     * Supports Fortune enchantment
     */
    private static int calculateDropCount(Block block, int fortuneLevel) {
        int baseCount = 1;

        // Copper Ore: 2-5 raw copper base
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            baseCount = RANDOM.nextInt(4) + 2; // 2-5

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Ancient Debris: 1 scrap
        else if (block == Blocks.ANCIENT_DEBRIS || block == ModBlocks.RUBY_DEBRIS) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Iron Ore: 1 iron ingot
        else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Gold Ore: 1 gold ingot
        else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Coal: 1 coal
        else if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Diamond: 1 diamond
        else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Emerald: 1 emerald
        else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Lapis: 4-9 lapis base
        else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
            baseCount = RANDOM.nextInt(6) + 4; // 4-9

            if (fortuneLevel > 0) {
                baseCount += RANDOM.nextInt((fortuneLevel + 1) * 3);
            }
        }

        // Redstone: 4-5 redstone base
        else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            baseCount = RANDOM.nextInt(2) + 4; // 4-5

            if (fortuneLevel > 0) {
                baseCount += RANDOM.nextInt(fortuneLevel + 1);
            }
        }

        // Nether Quartz: 1 quartz
        else if (block == Blocks.NETHER_QUARTZ_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        // Ruby Ore: 1 ruby ingot
        else if (block == ModBlocks.RUBY_ORE || block == ModBlocks.DEEPSLATE_RUBY_ORE) {
            baseCount = 1;

            if (fortuneLevel > 0) {
                int fortuneBonus = RANDOM.nextInt(fortuneLevel + 1);
                baseCount += fortuneBonus;
            }
        }

        return Math.max(1, baseCount);
    }

    /**
     * Calculate experience untuk ore yang di-smelt
     * Returns vanilla-accurate XP amounts
     */
    private static int calculateExperience(Block block, int dropCount) {
        int baseExp = 0;

        // Copper Ore: 0-1 XP
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            baseExp = RANDOM.nextBoolean() ? 1 : 0;
        }
        // Iron Ore: 0-1 XP
        else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            baseExp = RANDOM.nextBoolean() ? 1 : 0;
        }
        // Gold Ore: 0-1 XP
        else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            baseExp = RANDOM.nextBoolean() ? 1 : 0;
        }
        // Ancient Debris: 0-2 XP
        else if (block == Blocks.ANCIENT_DEBRIS) {
            baseExp = RANDOM.nextInt(3); // 0, 1, or 2
        }
        // Coal Ore: 0-2 XP
        else if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            baseExp = RANDOM.nextInt(3); // 0, 1, or 2
        }
        // Diamond Ore: 3-7 XP
        else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            baseExp = RANDOM.nextInt(5) + 3; // 3-7
        }
        // Emerald Ore: 3-7 XP
        else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            baseExp = RANDOM.nextInt(5) + 3; // 3-7
        }
        // Lapis Ore: 2-5 XP
        else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
            baseExp = RANDOM.nextInt(4) + 2; // 2-5
        }
        // Redstone Ore: 1-5 XP
        else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            baseExp = RANDOM.nextInt(5) + 1; // 1-5
        }
        // Nether Quartz Ore: 2-5 XP
        else if (block == Blocks.NETHER_QUARTZ_ORE) {
            baseExp = RANDOM.nextInt(4) + 2; // 2-5
        }
        // Ruby Ore: 3-7 XP
        else if (block == ModBlocks.RUBY_ORE || block == ModBlocks.DEEPSLATE_RUBY_ORE) {
            baseExp = RANDOM.nextInt(5) + 3; // 3-7
        }
        // Ruby Debris: 0-2 XP
        else if (block == ModBlocks.RUBY_DEBRIS) {
            baseExp = RANDOM.nextInt(3); // 0, 1, or 2
        }

        return baseExp;
    }
}