package com.example.emeraldmod.command;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.world.gen.InstantRetrofitSystem;
import com.example.emeraldmod.world.gen.OreRetrofitGenerator;
import com.example.emeraldmod.world.gen.OreRetrofitState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Command untuk manual retrofit chunks
 * ✅ UPDATED: Added /retrofit force for mod updates
 */
public class RetrofitCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("emeraldmod")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("retrofit")
                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 50))
                                .executes(RetrofitCommand::retrofitArea))
                        .executes(RetrofitCommand::retrofitAreaDefault)
                )
                .then(CommandManager.literal("retrofit-here")
                        .executes(RetrofitCommand::retrofitHere))
                .then(CommandManager.literal("retrofit-verify")
                        .executes(RetrofitCommand::verifyOres))
                .then(CommandManager.literal("retrofit-all")
                        .executes(RetrofitCommand::retrofitAllExisting))
                .then(CommandManager.literal("retrofit-stats")
                        .executes(RetrofitCommand::showRetrofitStats))
                .then(CommandManager.literal("retrofit-reset")
                        .executes(RetrofitCommand::resetRetrofit))
                // ⭐ NEW: Force re-generate command
                .then(CommandManager.literal("retrofit-force")
                        .executes(RetrofitCommand::forceRetrofit))
        );

        // ⭐ NEW: Shorter alias "/retrofit" commands
        dispatcher.register(CommandManager.literal("retrofit")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("status")
                        .executes(RetrofitCommand::retrofitStatus))
                .then(CommandManager.literal("start")
                        .executes(RetrofitCommand::retrofitStart))
                .then(CommandManager.literal("force")
                        .executes(RetrofitCommand::forceRetrofit))
                .then(CommandManager.literal("reset")
                        .executes(RetrofitCommand::resetRetrofit))
        );
    }

    // ============================================
    // ⭐ NEW: SIMPLIFIED COMMANDS
    // ============================================

    /**
     * /retrofit status - Show detailed retrofit status
     */
    private static int retrofitStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            String worldName = source.getServer().getSaveProperties().getLevelName();

            source.sendFeedback(() -> Text.literal("========================================")
                    .formatted(Formatting.GOLD), false);
            source.sendFeedback(() -> Text.literal("   RETROFIT STATUS")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);
            source.sendFeedback(() -> Text.literal("========================================")
                    .formatted(Formatting.GOLD), false);

            source.sendFeedback(() -> Text.literal("World: " + worldName)
                    .formatted(Formatting.WHITE), false);
            source.sendFeedback(() -> Text.literal(""), false);

            // Check Overworld
            ServerWorld overworld = source.getServer().getWorld(World.OVERWORLD);
            if (overworld != null) {
                OreRetrofitState state = OreRetrofitState.get(source.getServer(), overworld);

                String status = getStatusString(state);
                String versionInfo = state.getVersionInfo();
                boolean needsUpdate = state.needsUpdate();

                source.sendFeedback(() -> Text.literal("📍 OVERWORLD")
                        .formatted(Formatting.AQUA, Formatting.BOLD), false);
                source.sendFeedback(() -> Text.literal("  Status: " + status)
                        .formatted(getStatusColor(state)), false);
                source.sendFeedback(() -> Text.literal("  " + versionInfo)
                        .formatted(needsUpdate ? Formatting.YELLOW : Formatting.GRAY), false);

                if (needsUpdate && state.isComplete()) {
                    source.sendFeedback(() -> Text.literal("  ⚠️  OUTDATED - New ores available!")
                            .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                }

                if (state.canResume()) {
                    source.sendFeedback(() -> Text.literal("  → " + state.getResumeInfo())
                            .formatted(Formatting.YELLOW), false);
                }

                source.sendFeedback(() -> Text.literal(""), false);
            }

            // Check Nether
            ServerWorld nether = source.getServer().getWorld(World.NETHER);
            if (nether != null) {
                OreRetrofitState state = OreRetrofitState.get(source.getServer(), nether);

                String status = getStatusString(state);
                String versionInfo = state.getVersionInfo();
                boolean needsUpdate = state.needsUpdate();

                source.sendFeedback(() -> Text.literal("🔥 NETHER")
                        .formatted(Formatting.RED, Formatting.BOLD), false);
                source.sendFeedback(() -> Text.literal("  Status: " + status)
                        .formatted(getStatusColor(state)), false);
                source.sendFeedback(() -> Text.literal("  " + versionInfo)
                        .formatted(needsUpdate ? Formatting.YELLOW : Formatting.GRAY), false);

                if (needsUpdate && state.isComplete()) {
                    source.sendFeedback(() -> Text.literal("  ⚠️  OUTDATED - New ores available!")
                            .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                }

                if (state.canResume()) {
                    source.sendFeedback(() -> Text.literal("  → " + state.getResumeInfo())
                            .formatted(Formatting.YELLOW), false);
                }
            }

            // Check if running
            source.sendFeedback(() -> Text.literal(""), false);
            if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
                source.sendFeedback(() -> Text.literal("⚡ CURRENTLY RUNNING")
                        .formatted(Formatting.AQUA, Formatting.BOLD), false);
                source.sendFeedback(() -> Text.literal("  Check your game screen for progress")
                        .formatted(Formatting.GRAY), false);
            } else {
                // Check if needs update
                boolean needsUpdate = false;
                if (overworld != null) {
                    needsUpdate = OreRetrofitState.get(source.getServer(), overworld).needsUpdate();
                }
                if (!needsUpdate && nether != null) {
                    needsUpdate = OreRetrofitState.get(source.getServer(), nether).needsUpdate();
                }

                if (needsUpdate) {
                    source.sendFeedback(() -> Text.literal("💡 AVAILABLE COMMANDS:")
                            .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                    source.sendFeedback(() -> Text.literal("  /retrofit force")
                            .formatted(Formatting.AQUA), false);
                    source.sendFeedback(() -> Text.literal("    └─ Re-generate to add new ores")
                            .formatted(Formatting.GRAY), false);
                } else {
                    source.sendFeedback(() -> Text.literal("💡 AVAILABLE COMMANDS:")
                            .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                    source.sendFeedback(() -> Text.literal("  /retrofit start")
                            .formatted(Formatting.AQUA), false);
                    source.sendFeedback(() -> Text.literal("    └─ Start retrofit generation")
                            .formatted(Formatting.GRAY), false);
                }
            }

            source.sendFeedback(() -> Text.literal("========================================")
                    .formatted(Formatting.GOLD), false);

            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Error checking status: " + e.getMessage()));
            EmeraldMod.LOGGER.error("Retrofit status error", e);
            return 0;
        }
    }

    /**
     * /retrofit start - Start retrofit
     */
    private static int retrofitStart(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            String worldName = source.getServer().getSaveProperties().getLevelName();

            if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
                source.sendError(Text.literal("⚠️  Retrofit is already running!"));
                source.sendFeedback(() -> Text.literal("Check your game screen for progress")
                        .formatted(Formatting.GRAY), false);
                return 0;
            }

            source.sendFeedback(() -> Text.literal("⚡ Starting retrofit generation...")
                    .formatted(Formatting.GOLD, Formatting.BOLD), true);
            source.sendFeedback(() -> Text.literal("💡 Check your game screen for progress!")
                    .formatted(Formatting.AQUA), false);

            boolean started = InstantRetrofitSystem.runInitialRetrofit(source.getServer());

            if (started) {
                source.sendFeedback(() -> Text.literal("✅ Retrofit started successfully!")
                        .formatted(Formatting.GREEN, Formatting.BOLD), true);
                return 1;
            } else {
                source.sendError(Text.literal("❌ Failed to start retrofit"));
                source.sendFeedback(() -> Text.literal("May already be complete. Use /retrofit status")
                        .formatted(Formatting.GRAY), false);
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("Error starting retrofit: " + e.getMessage()));
            EmeraldMod.LOGGER.error("Retrofit start error", e);
            return 0;
        }
    }

    /**
     * ⭐ NEW: /retrofit force - Force re-generate (for mod updates)
     */
    private static int forceRetrofit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            String worldName = source.getServer().getSaveProperties().getLevelName();

            // Show warning header
            source.sendFeedback(() -> Text.literal("========================================")
                    .formatted(Formatting.RED), false);
            source.sendFeedback(() -> Text.literal("   FORCE RE-GENERATE")
                    .formatted(Formatting.RED, Formatting.BOLD), false);
            source.sendFeedback(() -> Text.literal("========================================")
                    .formatted(Formatting.RED), false);

            source.sendFeedback(() -> Text.literal("⚠️  This will re-generate ALL ores!")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), true);
            source.sendFeedback(() -> Text.literal(""), false);

            source.sendFeedback(() -> Text.literal("Use this command when:")
                    .formatted(Formatting.WHITE), false);
            source.sendFeedback(() -> Text.literal("  • Mod was updated with new ores")
                    .formatted(Formatting.GRAY), false);
            source.sendFeedback(() -> Text.literal("  • You want to add missing ores")
                    .formatted(Formatting.GRAY), false);
            source.sendFeedback(() -> Text.literal("  • Retrofit was incomplete")
                    .formatted(Formatting.GRAY), false);
            source.sendFeedback(() -> Text.literal(""), false);

            source.sendFeedback(() -> Text.literal("⏳ Resetting retrofit state...")
                    .formatted(Formatting.YELLOW), false);

            // Reset retrofit state
            InstantRetrofitSystem.resetRetrofitStatus(source.getServer());

            // Wait a moment for cleanup
            Thread.sleep(1000);

            source.sendFeedback(() -> Text.literal("⚡ Starting forced retrofit...")
                    .formatted(Formatting.GOLD, Formatting.BOLD), true);
            source.sendFeedback(() -> Text.literal("💡 Check your game screen for progress!")
                    .formatted(Formatting.AQUA), false);

            // Start retrofit
            boolean started = InstantRetrofitSystem.runInitialRetrofit(source.getServer());

            if (started) {
                source.sendFeedback(() -> Text.literal(""), false);
                source.sendFeedback(() -> Text.literal("✅ Force retrofit started!")
                        .formatted(Formatting.GREEN, Formatting.BOLD), true);
                source.sendFeedback(() -> Text.literal("New ores will be added to all chunks")
                        .formatted(Formatting.AQUA), false);
                source.sendFeedback(() -> Text.literal("Including newly added Nether Emerald Ore!")
                        .formatted(Formatting.GREEN), false);
                source.sendFeedback(() -> Text.literal("========================================")
                        .formatted(Formatting.GREEN), false);
                return 1;
            } else {
                source.sendError(Text.literal("❌ Failed to start forced retrofit"));
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("Error during force retrofit: " + e.getMessage()));
            EmeraldMod.LOGGER.error("Force retrofit error", e);
            return 0;
        }
    }

    // ============================================
    // EXISTING COMMANDS (unchanged)
    // ============================================

    /**
     * Verify ores di sekitar player (count blocks)
     */
    private static int verifyOres(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());

        source.sendFeedback(() -> Text.literal("🔍 Scanning for Mod Ores in 50 block radius...")
                .formatted(Formatting.YELLOW), false);

        int rubyOreCount = 0;
        int deepslateRubyOreCount = 0;
        int netherRubyOreCount = 0;
        int rubyDebrisCount = 0;
        int netherEmeraldOreCount = 0; // ⭐ NEW!

        int scanRadius = 50;

        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);

                    try {
                        net.minecraft.block.BlockState state = world.getBlockState(pos);

                        if (state.getBlock() == com.example.emeraldmod.block.ModBlocks.RUBY_ORE) {
                            rubyOreCount++;
                        } else if (state.getBlock() == com.example.emeraldmod.block.ModBlocks.DEEPSLATE_RUBY_ORE) {
                            deepslateRubyOreCount++;
                        } else if (state.getBlock() == com.example.emeraldmod.block.ModBlocks.NETHER_RUBY_ORE) {
                            netherRubyOreCount++;
                        } else if (state.getBlock() == com.example.emeraldmod.block.ModBlocks.RUBY_DEBRIS) {
                            rubyDebrisCount++;
                        } else if (state.getBlock() == com.example.emeraldmod.block.ModBlocks.NETHER_EMERALD_ORE) {
                            netherEmeraldOreCount++; // ⭐ NEW!
                        }
                    } catch (Exception e) {
                        // Skip
                    }
                }
            }
        }

        int totalOres = rubyOreCount + deepslateRubyOreCount + netherRubyOreCount +
                rubyDebrisCount + netherEmeraldOreCount;

        int finalRubyOreCount = rubyOreCount;
        int finalDeepslateRubyOreCount = deepslateRubyOreCount;
        int finalNetherRubyOreCount = netherRubyOreCount;
        int finalRubyDebrisCount = rubyDebrisCount;
        int finalNetherEmeraldOreCount = netherEmeraldOreCount;
        int finalTotalOres = totalOres;

        source.sendFeedback(() -> Text.literal("=== Ore Scan Results ===")
                .formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Scan radius: 50 blocks")
                .formatted(Formatting.GRAY), false);
        source.sendFeedback(() -> Text.literal("Ruby Ore: " + finalRubyOreCount)
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Deepslate Ruby Ore: " + finalDeepslateRubyOreCount)
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Nether Ruby Ore: " + finalNetherRubyOreCount)
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Ruby Debris: " + finalRubyDebrisCount)
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Nether Emerald Ore: " + finalNetherEmeraldOreCount)
                .formatted(Formatting.GREEN), false); // ⭐ NEW!
        source.sendFeedback(() -> Text.literal("Total: " + finalTotalOres + " Ores found")
                .formatted(finalTotalOres > 0 ? Formatting.GREEN : Formatting.RED), false);

        if (totalOres == 0) {
            source.sendFeedback(() -> Text.literal("⚠ No ores found! Try: /emeraldmod retrofit-here")
                    .formatted(Formatting.RED), false);
        }

        return totalOres;
    }

    /**
     * Retrofit chunk di posisi player sekarang (instant)
     */
    private static int retrofitHere(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(playerPos);

        source.sendFeedback(() -> Text.literal("⚡ Retrofitting current chunk...")
                .formatted(Formatting.YELLOW), false);

        try {
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            OreRetrofitState state = OreRetrofitState.get(world.getServer(), world);

            if (OreRetrofitGenerator.chunkHasRubyOres(world, chunk)) {
                source.sendFeedback(() -> Text.literal("This chunk already has Mod Ores!")
                        .formatted(Formatting.YELLOW), false);

                boolean wasRetrofitted = state.isChunkRetrofitted(chunkPos);
                if (!wasRetrofitted) {
                    state.markChunkRetrofitted(chunkPos);
                    source.sendFeedback(() -> Text.literal("✓ Marked as retrofitted for future scans")
                            .formatted(Formatting.GREEN), false);
                }
            } else if (OreRetrofitGenerator.retrofitChunk(world, chunk)) {
                source.sendFeedback(() -> Text.literal("✓ Current chunk retrofitted!")
                        .formatted(Formatting.GREEN), false);
                source.sendFeedback(() -> Text.literal("Ores should now be visible here")
                        .formatted(Formatting.AQUA), false);
            } else {
                source.sendFeedback(() -> Text.literal("Chunk was already retrofitted")
                        .formatted(Formatting.GRAY), false);
            }

        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("✗ Error: " + e.getMessage())
                    .formatted(Formatting.RED), false);
        }

        return 1;
    }

    /**
     * Retrofit ALL existing chunks (manual trigger)
     */
    private static int retrofitAllExisting(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String worldName = source.getServer().getSaveProperties().getLevelName();

        if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
            source.sendFeedback(() -> Text.literal("⚠ Retrofit is already running for this world!")
                    .formatted(Formatting.YELLOW), false);
            source.sendFeedback(() -> Text.literal("Check your game screen for progress")
                    .formatted(Formatting.GRAY), false);
            return 0;
        }

        if (InstantRetrofitSystem.isRetrofitComplete(source.getServer())) {
            source.sendFeedback(() -> Text.literal("⚠ Retrofit already complete for this world!")
                    .formatted(Formatting.YELLOW), false);
            source.sendFeedback(() -> Text.literal("Use /retrofit force to re-generate (for mod updates)")
                    .formatted(Formatting.AQUA), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("⚡ Starting Ore Generation...")
                .formatted(Formatting.YELLOW), true);
        source.sendFeedback(() -> Text.literal("💡 Check your game screen for progress!")
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("You can minimize the screen and continue playing")
                .formatted(Formatting.GRAY), false);

        boolean started = InstantRetrofitSystem.runInitialRetrofit(source.getServer());

        if (started) {
            source.sendFeedback(() -> Text.literal("✓ Generation started successfully!")
                    .formatted(Formatting.GREEN), false);
        } else {
            source.sendFeedback(() -> Text.literal("✗ Failed to start generation")
                    .formatted(Formatting.RED), false);
        }

        return started ? 1 : 0;
    }

    private static int retrofitAreaDefault(CommandContext<ServerCommandSource> context) {
        return retrofitArea(context, 10);
    }

    private static int retrofitArea(CommandContext<ServerCommandSource> context) {
        int radius = IntegerArgumentType.getInteger(context, "radius");
        return retrofitArea(context, radius);
    }

    private static int retrofitArea(CommandContext<ServerCommandSource> context, int radius) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos centerChunk = new ChunkPos(playerPos);

        source.sendFeedback(() -> Text.literal("⚡ Starting ore retrofit in radius " + radius + " chunks...")
                .formatted(Formatting.YELLOW), true);

        int retrofittedCount = 0;
        int skippedCount = 0;
        int totalChunks = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos chunkPos = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
                totalChunks++;

                try {
                    WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

                    if (OreRetrofitGenerator.chunkHasRubyOres(world, chunk)) {
                        OreRetrofitState state = OreRetrofitState.get(world.getServer(), world);
                        state.markChunkRetrofitted(chunkPos);
                        skippedCount++;
                    } else if (OreRetrofitGenerator.retrofitChunk(world, chunk)) {
                        retrofittedCount++;
                    }

                } catch (Exception e) {
                    int finalX = chunkPos.x;
                    int finalZ = chunkPos.z;
                    source.sendFeedback(() -> Text.literal("Error retrofitting chunk (" +
                                    finalX + ", " + finalZ + "): " + e.getMessage())
                            .formatted(Formatting.RED), false);
                }
            }
        }

        int finalRetrofittedCount = retrofittedCount;
        int finalSkippedCount = skippedCount;
        int finalTotalChunks = totalChunks;

        source.sendFeedback(() -> Text.literal("✓ Retrofit complete!")
                .formatted(Formatting.GREEN), true);
        source.sendFeedback(() -> Text.literal("New ores placed: " + finalRetrofittedCount + " chunks")
                .formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Already had ores: " + finalSkippedCount + " chunks")
                .formatted(Formatting.GRAY), false);
        source.sendFeedback(() -> Text.literal("Total checked: " + finalTotalChunks + " chunks")
                .formatted(Formatting.YELLOW), false);

        return retrofittedCount + skippedCount;
    }

    private static int showRetrofitStats(CommandContext<ServerCommandSource> context) {
        return retrofitStatus(context); // Use new status command
    }

    private static int resetRetrofit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String worldName = source.getServer().getSaveProperties().getLevelName();

        source.sendFeedback(() -> Text.literal("⚠️ WARNING: This will reset ALL retrofit data!")
                .formatted(Formatting.RED, Formatting.BOLD), false);
        source.sendFeedback(() -> Text.literal("All chunks will be retrofitted again on next generation.")
                .formatted(Formatting.YELLOW), false);

        InstantRetrofitSystem.resetRetrofitStatus(source.getServer());

        source.sendFeedback(() -> Text.literal("✓ Retrofit data cleared for world '" + worldName + "'")
                .formatted(Formatting.GREEN), true);
        source.sendFeedback(() -> Text.literal("Use /retrofit start to begin fresh generation")
                .formatted(Formatting.AQUA), false);

        return 1;
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private static String getStatusString(OreRetrofitState state) {
        if (state.isComplete()) {
            return "✅ COMPLETE (" + state.getRetrofittedChunkCount() + " chunks)";
        } else if (state.isInProgress()) {
            int percentage = state.getPercentage();
            return "⏳ IN PROGRESS (" + percentage + "% - " +
                    state.getProcessedChunks() + "/" + state.getTotalChunks() + " chunks)";
        } else {
            return "❌ NOT STARTED";
        }
    }

    private static Formatting getStatusColor(OreRetrofitState state) {
        if (state.isComplete()) {
            return Formatting.GREEN;
        } else if (state.isInProgress()) {
            return Formatting.YELLOW;
        } else {
            return Formatting.RED;
        }
    }
}