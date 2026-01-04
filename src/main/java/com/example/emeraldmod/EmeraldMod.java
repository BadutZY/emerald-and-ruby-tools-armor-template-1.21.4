package com.example.emeraldmod;

import com.example.emeraldmod.block.ModBlocks;
import com.example.emeraldmod.command.RetrofitCommand;
import com.example.emeraldmod.effect.ModEffects;
import com.example.emeraldmod.event.*;
import com.example.emeraldmod.item.ModItemGroups;
import com.example.emeraldmod.item.ModItems;
import com.example.emeraldmod.network.EffectStateSyncPacket;
import com.example.emeraldmod.network.ServerPacketHandler;
import com.example.emeraldmod.network.ToggleEffectPacket;
import com.example.emeraldmod.state.EffectStateManager;
import com.example.emeraldmod.world.gen.InstantRetrofitSystem;
import com.example.emeraldmod.world.gen.ModWorldGeneration;
import com.example.emeraldmod.world.gen.OreRetrofitState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod class untuk Emerald & Ruby Mod
 * ✅ COMPLETE VERSION dengan Resume Support dan State Sync
 */
public class EmeraldMod implements ModInitializer {
    public static final String MOD_ID = "emeraldmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean retrofitCheckDone = false;

    @Override
    public void onInitialize() {
        LOGGER.info("========================================");
        LOGGER.info("Initializing Emerald & Ruby Mod");
        LOGGER.info("========================================");

        // ============================================
        // PHASE 1: NETWORK PACKETS
        // ============================================
        registerNetworkPackets();

        // ============================================
        // PHASE 2: GAME CONTENT
        // ============================================
        registerGameContent();

        // ============================================
        // PHASE 3: WORLD GENERATION
        // ============================================
        registerWorldGeneration();

        // ============================================
        // PHASE 4: RETROFIT SYSTEM (WITH SCANNING & RESUME)
        // ============================================
        registerRetrofitSystem();

        // ============================================
        // PHASE 5: GAMEPLAY HANDLERS
        // ============================================
        registerGameplayHandlers();

        // ============================================
        // PHASE 6: PLAYER CONNECTION HANDLERS
        // ============================================
        registerPlayerConnectionHandlers();

        // ============================================
        // PHASE 7: SERVER LIFECYCLE HANDLERS
        // ============================================
        registerServerLifecycleHandlers();

        // ============================================
        // PHASE 8: FINALIZATION
        // ============================================
        logInitializationComplete();
    }

    /**
     * PHASE 1: Register all network packets
     */
    private void registerNetworkPackets() {
        LOGGER.info("--- Phase 1: Network Packets ---");

        // Toggle effect packets
        try {
            ToggleEffectPacket.register();
            LOGGER.info("✅ Registered Toggle Effect Packet");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Toggle Effect Packet already registered, skipping");
        }

        // ✅ NEW: Effect state sync packet
        try {
            EffectStateSyncPacket.register();
            LOGGER.info("✅ Registered Effect State Sync Packet");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Effect State Sync Packet already registered, skipping");
        }

        // Server packet handler
        try {
            ServerPacketHandler.register();
            LOGGER.info("✅ Registered Server Packet Handler");
        } catch (Exception e) {
            LOGGER.warn("Server Packet Handler already registered, skipping");
        }

        // Scanning packets
        com.example.emeraldmod.network.ScanningPackets.registerServer();
        LOGGER.info("✅ Registered Scanning Packets");

        // Retrofit packets
        com.example.emeraldmod.network.RetrofitPackets.registerServer();
        LOGGER.info("✅ Registered Retrofit Network Packets");

        // Decision packets
        com.example.emeraldmod.network.RetrofitDecisionPacket.registerServer();
        LOGGER.info("✅ Registered Retrofit Decision Packets");
    }

    /**
     * PHASE 2: Register game content (effects, blocks, items)
     */
    private void registerGameContent() {
        LOGGER.info("--- Phase 2: Game Content ---");

        // Custom effects
        ModEffects.registerModEffects();
        LOGGER.info("✅ Registered Custom Effects");

        // Blocks (must be before items)
        ModBlocks.registerModBlocks();
        LOGGER.info("✅ Registered Blocks");

        // Items (must be after blocks)
        ModItems.registerModItems();
        LOGGER.info("✅ Registered Items");

        // Item groups (must be after items)
        ModItemGroups.registerItemGroups();
        LOGGER.info("✅ Registered Item Groups");
    }

    /**
     * PHASE 3: Register world generation
     */
    private void registerWorldGeneration() {
        LOGGER.info("--- Phase 3: World Generation ---");

        ModWorldGeneration.generateModWorldGen();
        LOGGER.info("✅ Registered Ruby Ore World Generation");
    }

    /**
     * PHASE 4: Register retrofit system with SCANNING & RESUME
     */
    private void registerRetrofitSystem() {
        LOGGER.info("--- Phase 4: Retrofit System (With Scanning & Resume) ---");

        // Player disconnect event - CLEANUP STATE
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            String playerName = handler.player.getName().getString();
            String worldName = server.getSaveProperties().getLevelName();

            LOGGER.info("[Retrofit] Player {} disconnected from world '{}'", playerName, worldName);

            // Check if retrofit is running untuk world ini
            if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
                LOGGER.info("[Retrofit] ⚠️ Player left during retrofit - keeping retrofit running");
                // Don't cancel - biarkan retrofit selesai
            }
        });

        // ENHANCED: Player join event - Check for RESUME or start scanning
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            String playerName = handler.player.getName().getString();
            String worldName = server.getSaveProperties().getLevelName();

            LOGGER.info("[Retrofit] Player {} joined world '{}'", playerName, worldName);

            // NEW: Check if can resume retrofit
            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            ServerWorld nether = server.getWorld(World.NETHER);

            boolean canResumeOverworld = false;
            boolean canResumeNether = false;

            if (overworld != null) {
                OreRetrofitState state = OreRetrofitState.get(server, overworld);
                canResumeOverworld = state.canResume();

                if (canResumeOverworld) {
                    LOGGER.info("[Retrofit] 🔄 Overworld can resume: {}", state.getResumeInfo());
                }
            }

            if (nether != null) {
                OreRetrofitState state = OreRetrofitState.get(server, nether);
                canResumeNether = state.canResume();

                if (canResumeNether) {
                    LOGGER.info("[Retrofit] 🔄 Nether can resume: {}", state.getResumeInfo());
                }
            }

            // CHECK 1: Retrofit currently running
            if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
                LOGGER.info("[Retrofit] ⚡ World '{}' is currently retrofitting - showing loading screen", worldName);

                // Show loading screen langsung (skip scanning)
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        server.execute(() -> {
                            com.example.emeraldmod.network.RetrofitPackets.sendShowLoading(handler.player, worldName);
                        });
                    } catch (InterruptedException e) {
                        LOGGER.error("[Retrofit] Interrupted", e);
                    }
                }, "ShowLoading-" + playerName).start();

                return; // Skip scanning and resume check
            }

            // CHECK 2: Can resume? Auto-resume!
            if (canResumeOverworld || canResumeNether) {
                LOGGER.info("[Retrofit] 🔄 Auto-resuming retrofit for world '{}'", worldName);

                new Thread(() -> {
                    try {
                        Thread.sleep(1500);

                        server.execute(() -> {
                            // Start resume directly
                            boolean started = InstantRetrofitSystem.runInitialRetrofit(server);

                            if (started) {
                                LOGGER.info("[Retrofit] ✅ Resume started successfully");
                            } else {
                                LOGGER.warn("[Retrofit] ❌ Failed to resume retrofit");
                            }
                        });
                    } catch (InterruptedException e) {
                        LOGGER.error("[Retrofit] Interrupted during resume", e);
                    }
                }, "AutoResume-" + playerName).start();

                return; // Skip scanning - directly resume
            }

            // CHECK 3: Normal flow - start scanning
            new Thread(() -> {
                try {
                    Thread.sleep(1500);

                    server.execute(() -> {
                        startScanningProcess(server, handler.player);
                    });
                } catch (InterruptedException e) {
                    LOGGER.error("[Retrofit] Interrupted during join", e);
                }
            }, "ScanStart-" + playerName).start();
        });

        // Server started event - Check status only
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            synchronized (EmeraldMod.class) {
                if (retrofitCheckDone) {
                    LOGGER.info("[Retrofit] Server start check already done");
                    return;
                }
                retrofitCheckDone = true;
            }

            String worldName = server.getSaveProperties().getLevelName();

            LOGGER.info("========================================");
            LOGGER.info("[Retrofit] Server Started");
            LOGGER.info("[Retrofit] World: {}", worldName);
            LOGGER.info("========================================");

            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            if (overworld == null) {
                LOGGER.warn("[Retrofit] Overworld not found!");
                return;
            }

            OreRetrofitState state = OreRetrofitState.get(server, overworld);

            if (state.isComplete()) {
                LOGGER.info("[Retrofit] ✅ Already complete for world '{}'", worldName);
            } else if (state.isInProgress()) {
                LOGGER.info("[Retrofit] ⏳ In progress for world '{}' ({} chunks done)",
                        worldName, state.getRetrofittedChunkCount());

                // Log resume info
                if (state.canResume()) {
                    LOGGER.info("[Retrofit] 🔄 {}", state.getResumeInfo());
                }
            } else {
                LOGGER.info("[Retrofit] ⏳ Not started for world '{}'", worldName);
                LOGGER.info("[Retrofit] Will scan on player join");
            }

            LOGGER.info("========================================");
        });

        // Server stopping event - CANCEL ALL RETROFITS
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            String worldName = server.getSaveProperties().getLevelName();

            LOGGER.info("[Retrofit] Server stopping - cleaning up world '{}'", worldName);

            // Cancel retrofit jika masih running
            if (InstantRetrofitSystem.isRetrofitRunning(worldName)) {
                LOGGER.info("[Retrofit] Cancelling active retrofit for world '{}'", worldName);
                InstantRetrofitSystem.cancelRetrofit(worldName);
            }

            // ✅ NEW: Force save effect states
            EffectStateManager stateManager = EffectStateManager.getServerState(server);
            stateManager.forceSave();
            LOGGER.info("[EffectState] Force saved all player states before shutdown");
        });

        // Register retrofit commands
        CommandRegistrationCallback.EVENT.register(RetrofitCommand::register);

        LOGGER.info("✅ Registered Retrofit System (With Scanning & Resume)");
        LOGGER.info("  → Shows scanning screen for new worlds");
        LOGGER.info("  → Auto-detects existing ores");
        LOGGER.info("  → Auto-resumes from last checkpoint");
        LOGGER.info("  → Per-world independent progress");
    }

    /**
     * Start scanning process dengan proper world checking
     */
    private void startScanningProcess(MinecraftServer server, ServerPlayerEntity player) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            LOGGER.warn("[Scanning] Overworld not found!");
            return;
        }

        String worldName = server.getSaveProperties().getLevelName();
        String playerName = player.getName().getString();

        LOGGER.info("========================================");
        LOGGER.info("[Scanning] 🔍 Starting scan for world '{}'", worldName);
        LOGGER.info("[Scanning] Player: {}", playerName);
        LOGGER.info("========================================");

        OreRetrofitState overworldState = OreRetrofitState.get(server, overworld);

        // STEP 1: Check if already complete (skip scanning)
        if (overworldState.isComplete()) {
            LOGGER.info("[Scanning] ✅ World '{}' already COMPLETE - no scanning needed", worldName);
            return;
        }

        // CHECK 2: Check if retrofit currently running UNTUK WORLD INI
        boolean isThisWorldRetrofitting = InstantRetrofitSystem.isRetrofitRunning(worldName);

        if (isThisWorldRetrofitting) {
            LOGGER.info("[Scanning] ⚡ Retrofit RUNNING for world '{}' - showing loading screen", worldName);

            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    server.execute(() -> {
                        com.example.emeraldmod.network.RetrofitPackets.sendShowLoading(player, worldName);
                    });
                } catch (InterruptedException e) {
                    LOGGER.error("[Scanning] Interrupted", e);
                }
            }, "ShowLoading-" + playerName).start();

            return;
        }

        // STEP 3: Start scanning process
        LOGGER.info("[Scanning] 🔍 Starting ore detection scan...");

        // Send scanning screen to player
        com.example.emeraldmod.network.ScanningPackets.sendStartScan(player, worldName);

        // Run scan in background
        new Thread(() -> {
            try {
                // Update status: Initializing
                Thread.sleep(500);
                server.execute(() -> {
                    com.example.emeraldmod.network.ScanningPackets.sendScanStatus(
                            player, worldName, "Initializing scanner"
                    );
                });

                // Update status: Scanning
                Thread.sleep(500);
                server.execute(() -> {
                    com.example.emeraldmod.network.ScanningPackets.sendScanStatus(
                            player, worldName, "Scanning world chunks"
                    );
                });

                // Perform actual scan
                Thread.sleep(500);
                boolean hasOres = com.example.emeraldmod.world.gen.WorldOreDetector.worldHasRubyOres(
                        server, overworld
                );

                // Update status: Analyzing
                server.execute(() -> {
                    com.example.emeraldmod.network.ScanningPackets.sendScanStatus(
                            player, worldName, "Analyzing results"
                    );
                });

                Thread.sleep(500);

                // Send result
                final boolean finalHasOres = hasOres;
                server.execute(() -> {
                    if (finalHasOres) {
                        LOGGER.info("[Scanning] ✅ Found Ruby Ores in world '{}'", worldName);

                        // Mark as complete
                        overworldState.setComplete(true);

                        // Also mark nether
                        ServerWorld nether = server.getWorld(World.NETHER);
                        if (nether != null) {
                            OreRetrofitState netherState = OreRetrofitState.get(server, nether);
                            netherState.setComplete(true);
                        }

                        // Send success to client
                        com.example.emeraldmod.network.ScanningPackets.sendScanComplete(
                                player, worldName, true
                        );
                    } else {
                        LOGGER.info("[Scanning] ❌ No Ruby Ores found in world '{}'", worldName);

                        // Send failure to client (will show confirmation)
                        com.example.emeraldmod.network.ScanningPackets.sendScanComplete(
                                player, worldName, false
                        );
                    }
                });

            } catch (InterruptedException e) {
                LOGGER.error("[Scanning] Scan interrupted", e);
            }
        }, "OreScan-" + playerName).start();
    }

    /**
     * PHASE 5: Register gameplay handlers
     */
    private void registerGameplayHandlers() {
        LOGGER.info("--- Phase 5: Gameplay Handlers ---");

        // Armor effects
        ArmorEffectsHandler.register();
        LOGGER.info("✅ Armor Effects Handler");

        // Horse armor effects
        HorseArmorEffectsHandler.register();
        LOGGER.info("✅ Horse Armor Effects Handler");

        // Tool effects
        ToolEffectsHandler.register();
        LOGGER.info("✅ Tool Effects Handler");

        // Fire damage prevention
        DamagePreventionHandler.register();
        LOGGER.info("✅ Fire Damage Prevention Handler");

        // Auto-smelt (Pickaxe)
        AutoSmeltHandler.register();
        LOGGER.info("✅ Auto-Smelt Handler");

        // Tree chopping (Axe)
        TreeChoppingHandler.register();
        LOGGER.info("✅ Tree Chopping Handler");

        // Auto-replant (Hoe)
        AutoReplantHandler.register();
        LOGGER.info("✅ Auto-Replant Handler");

        // Shockwave (Sword)
        SwordShockwaveHandler.register();
        LOGGER.info("✅ Shockwave Handler");

        // Anti-gravity (Shovel)
        AntiGravityHandler.register();
        LOGGER.info("✅ Anti-Gravity Handler");

        // Powder snow handler
        PowderSnowHandler.register();
        LOGGER.info("✅ Powder Snow Handler");

        // Server tick for anti-gravity
        ServerTickEvents.END_WORLD_TICK.register(AntiGravityHandler::tick);
        LOGGER.info("✅ Server Tick Events");
    }

    /**
     * ✅ NEW PHASE 6: Register player connection handlers untuk state sync
     */
    private void registerPlayerConnectionHandlers() {
        LOGGER.info("--- Phase 6: Player Connection Handlers ---");

        // ✅ Player join - Send initial state sync
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // Delay untuk memastikan client sudah ready
            new Thread(() -> {
                try {
                    Thread.sleep(500); // 500ms delay

                    server.execute(() -> {
                        EffectStateManager stateManager = EffectStateManager.getServerState(server);
                        ServerPacketHandler.sendInitialSync(handler.player, stateManager);

                        LOGGER.info("[EffectState] Sent initial sync to player {}: Tools={}, Armor={}",
                                handler.player.getName().getString(),
                                stateManager.isToolsEnabled(handler.player.getUuid()),
                                stateManager.isArmorEnabled(handler.player.getUuid()));
                    });
                } catch (InterruptedException e) {
                    LOGGER.error("[EffectState] Interrupted during initial sync", e);
                }
            }, "StateSync-" + handler.player.getName().getString()).start();
        });

        LOGGER.info("✅ Player Connection Handlers (State Sync)");
    }

    /**
     * ✅ NEW PHASE 7: Register server lifecycle handlers
     */
    private void registerServerLifecycleHandlers() {
        LOGGER.info("--- Phase 7: Server Lifecycle Handlers ---");

        // Force save states setiap 5 menit (auto-save)
        ServerTickEvents.END_SERVER_TICK.register(new Object() {
            private int tickCounter = 0;
            private static final int SAVE_INTERVAL = 20 * 60 * 5; // 5 minutes

            public void onServerTick(MinecraftServer server) {
                tickCounter++;
                if (tickCounter >= SAVE_INTERVAL) {
                    tickCounter = 0;

                    EffectStateManager stateManager = EffectStateManager.getServerState(server);
                    stateManager.forceSave();

                    LOGGER.debug("[EffectState] Auto-saved player states (5min interval)");
                }
            }
        }::onServerTick);

        LOGGER.info("✅ Server Lifecycle Handlers (Auto-save)");
    }

    /**
     * PHASE 8: Log initialization complete with feature summary
     */
    private void logInitializationComplete() {
        LOGGER.info("========================================");
        LOGGER.info("Emerald & Ruby Mod Initialized!");
        LOGGER.info("========================================");
        LOGGER.info("");

        // Keybind controls
        LOGGER.info("🎮 KEYBIND CONTROLS:");
        LOGGER.info("  - Toggle Tools: V (default)");
        LOGGER.info("  - Toggle Armor: B (default)");
        LOGGER.info("  - Maximize Retrofit: M (default)");
        LOGGER.info("  - Generate Now: N (default)");
        LOGGER.info("  - Customize in: Options → Controls");
        LOGGER.info("");

        // Ruby features
        LOGGER.info("💎 RUBY FEATURES:");
        LOGGER.info("  - UNBREAKABLE Tools & Armor");
        LOGGER.info("  - Mining Speed: 12.0 (Fastest)");
        LOGGER.info("  - Attack Damage: 6.0 (Strongest)");
        LOGGER.info("  - Enchantability: 15 (Best)");
        LOGGER.info("  - Toughness: 6.0 (Highest)");
        LOGGER.info("");

        // ✅ NEW: State management info
        LOGGER.info("💾 STATE MANAGEMENT:");
        LOGGER.info("  - ✅ Effect states saved per-player");
        LOGGER.info("  - ✅ Auto-sync on player join");
        LOGGER.info("  - ✅ Persistent across sessions");
        LOGGER.info("  - ✅ Auto-save every 5 minutes");
        LOGGER.info("  - ✅ Force save on server shutdown");
        LOGGER.info("");

        // Scanning & Retrofit system
        LOGGER.info("🔍 SCANNING & RETROFIT SYSTEM:");
        LOGGER.info("  - 🔍 Auto-scan on world join");
        LOGGER.info("  - ✅ Detects existing ruby ores");
        LOGGER.info("  - 🔄 Auto-resumes from checkpoint");
        LOGGER.info("  - 💾 Saves progress per-world");
        LOGGER.info("  - 💬 Confirmation dialog if needed");
        LOGGER.info("  - 📦 Processes all chunks");
        LOGGER.info("  - ⏱️ Takes 2-10 minutes");
        LOGGER.info("  - 🎮 Can minimize and play");
        LOGGER.info("  - 🌍 Per-world independent");
        LOGGER.info("");

        // Armor features
        LOGGER.info("🛡️ ARMOR FEATURES:");
        LOGGER.info("  - Water Breathing (Helmet)");
        LOGGER.info("  - Dolphin's Grace (Chestplate)");
        LOGGER.info("  - Fire Immunity (All Armor)");
        LOGGER.info("  - Powder Snow Walker (Boots)");
        LOGGER.info("  - Piglin Neutral (All Armor)");
        LOGGER.info("  - Silent Step (Leggings)");
        LOGGER.info("");

        // Tool features
        LOGGER.info("⚔️ TOOL FEATURES:");
        LOGGER.info("  - Shockwave (Sword - 3rd hit)");
        LOGGER.info("  - Auto-Smelt (Pickaxe)");
        LOGGER.info("  - Tree Chopping (Axe)");
        LOGGER.info("  - Anti-Gravity (Shovel)");
        LOGGER.info("  - Auto-Replant (Hoe)");
        LOGGER.info("");

        LOGGER.info("========================================");
        LOGGER.info("Ready to play! 🎮");
        LOGGER.info("========================================");
    }
}