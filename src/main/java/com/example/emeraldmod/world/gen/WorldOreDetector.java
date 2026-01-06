package com.example.emeraldmod.world.gen;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detector untuk check apakah world sudah memiliki Ruby/Emerald Ores atau belum
 */
public class WorldOreDetector {

    private static final int SAMPLE_CHUNKS = 20;
    private static final int MIN_ORES_FOUND = 1;

    /**
     * ⭐ UPDATED: Check untuk Ruby ORE Emerald Ores
     */
    public static boolean worldHasRubyOres(MinecraftServer server, ServerWorld world) {
        EmeraldMod.LOGGER.info("[OreDetector] Checking if world has Ruby/Emerald Ores...");

        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();

        File regionDir;
        if (world.getRegistryKey() == World.OVERWORLD) {
            regionDir = new File(worldDir, "region");
        } else if (world.getRegistryKey() == World.NETHER) {
            regionDir = new File(new File(worldDir, "DIM-1"), "region");
        } else {
            return false;
        }

        if (!regionDir.exists() || !regionDir.isDirectory()) {
            EmeraldMod.LOGGER.info("[OreDetector] Region folder not found - new world");
            return false;
        }

        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mca"));

        if (regionFiles == null || regionFiles.length == 0) {
            EmeraldMod.LOGGER.info("[OreDetector] No region files - new world");
            return false;
        }

        EmeraldMod.LOGGER.info("[OreDetector] Found {} region files, sampling chunks...",
                regionFiles.length);

        List<ChunkPos> chunksToCheck = getRandomChunks(regionFiles, SAMPLE_CHUNKS);

        int oresFound = 0;
        int chunksChecked = 0;

        for (ChunkPos pos : chunksToCheck) {
            try {
                WorldChunk chunk = world.getChunk(pos.x, pos.z);

                if (chunk == null) {
                    continue;
                }

                if (chunkHasModOres(world, chunk)) { // Changed method name
                    oresFound++;
                    EmeraldMod.LOGGER.info("[OreDetector] Found Mod Ores in chunk ({}, {})",
                            pos.x, pos.z);

                    if (oresFound >= MIN_ORES_FOUND) {
                        EmeraldMod.LOGGER.info("[OreDetector] ✓ World HAS Mod Ores (found in {} chunks)",
                                oresFound);
                        return true;
                    }
                }

                chunksChecked++;

            } catch (Exception e) {
                continue;
            }
        }

        EmeraldMod.LOGGER.info("[OreDetector] ✗ World does NOT have Mod Ores (checked {} chunks)",
                chunksChecked);
        return false;
    }

    private static List<ChunkPos> getRandomChunks(File[] regionFiles, int maxSamples) {
        List<ChunkPos> chunks = new ArrayList<>();
        Pattern pattern = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");

        int regionsToCheck = Math.min(regionFiles.length, 5);

        for (int i = 0; i < regionsToCheck && chunks.size() < maxSamples; i++) {
            File regionFile = regionFiles[i];
            Matcher matcher = pattern.matcher(regionFile.getName());

            if (matcher.matches()) {
                int regionX = Integer.parseInt(matcher.group(1));
                int regionZ = Integer.parseInt(matcher.group(2));

                chunks.add(new ChunkPos(regionX * 32, regionZ * 32));
                chunks.add(new ChunkPos(regionX * 32 + 31, regionZ * 32));
                chunks.add(new ChunkPos(regionX * 32, regionZ * 32 + 31));
                chunks.add(new ChunkPos(regionX * 32 + 16, regionZ * 32 + 16));
            }
        }

        return chunks;
    }

    /**
     * ⭐ UPDATED: Check ruby ORE emerald.json ores
     */
    private static boolean chunkHasModOres(ServerWorld world, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();

        int minY = world.getBottomY();
        int maxY;

        if (world.getRegistryKey() == World.NETHER) {
            maxY = 128;
        } else {
            maxY = 320;
        }

        for (int x = 0; x < 16; x += 4) {
            for (int z = 0; z < 16; z += 4) {
                for (int y = minY; y < maxY; y += 8) {
                    BlockPos pos = new BlockPos(startX + x, y, startZ + z);

                    try {
                        BlockState state = world.getBlockState(pos);

                        if (isModOre(state)) { // Changed method name
                            return true;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        return false;
    }

    /**
     * ⭐ UPDATED: Check ruby ORE emerald.json ore
     */
    private static boolean isModOre(BlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.RUBY_ORE ||
                block == ModBlocks.DEEPSLATE_RUBY_ORE ||
                block == ModBlocks.NETHER_RUBY_ORE ||
                block == ModBlocks.RUBY_DEBRIS ||
                block == ModBlocks.NETHER_EMERALD_ORE; // NEW!
    }
}