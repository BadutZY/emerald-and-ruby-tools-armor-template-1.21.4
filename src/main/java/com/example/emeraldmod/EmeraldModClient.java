package com.example.emeraldmod;

import com.example.emeraldmod.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Client-side initialization for Emerald & Ruby Mod
 * ✅ FIXED: Added RetrofitForceConfirmationScreen registration
 */
public class EmeraldModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EmeraldMod.LOGGER.info("========================================");
        EmeraldMod.LOGGER.info("Initializing Emerald Mod Client");
        EmeraldMod.LOGGER.info("========================================");

        // PHASE 1: NETWORK HANDLERS
        registerNetworkHandlers();

        // PHASE 2: INPUT HANDLERS
        registerInputHandlers();

        // PHASE 3: UI COMPONENTS
        registerUIComponents();

        // PHASE 4: RENDERING
        registerRendering();

        // PHASE 5: WORLD TRACKING
        registerWorldTracking();

        // PHASE 6: FINALIZATION
        logClientInitComplete();
    }

    /**
     * PHASE 1: Register network packet handlers (client-side)
     */
    private void registerNetworkHandlers() {
        EmeraldMod.LOGGER.info("--- Phase 1: Network Handlers ---");

        // Scanning packets
        com.example.emeraldmod.client.network.ScanningClientPackets.registerClient();
        EmeraldMod.LOGGER.info("✅ Scanning Client Packets");

        // Retrofit loading screen packets
        com.example.emeraldmod.client.network.RetrofitClientPackets.registerClient();
        EmeraldMod.LOGGER.info("✅ Retrofit Client Packets");

        // Retrofit decision packets (client-side)
        com.example.emeraldmod.network.RetrofitDecisionPacket.registerClient();
        EmeraldMod.LOGGER.info("✅ Retrofit Decision Packets");
    }

    /**
     * PHASE 2: Register input handlers (keybinds)
     */
    private void registerInputHandlers() {
        EmeraldMod.LOGGER.info("--- Phase 2: Input Handlers ---");

        // Toggle keybinds (V for tools, B for armor)
        ModKeybinds.register();
        EmeraldMod.LOGGER.info("✅ Toggle Keybinds (V, B) with State Sync");

        // Retrofit keybinds (M for maximize, N for generate now, J for toggle hide)
        RetrofitKeybind.register();
        EmeraldMod.LOGGER.info("✅ Retrofit Keybinds (M, N, J)");
    }

    /**
     * PHASE 3: Register UI components
     */
    private void registerUIComponents() {
        EmeraldMod.LOGGER.info("--- Phase 3: UI Components ---");

        // Scanning screen
        RubyOreScanningScreen.getInstance();
        EmeraldMod.LOGGER.info("✅ Ruby Ore Scanning Screen");

        // Retrofit confirmation screen (normal)
        RetrofitConfirmationScreen.getInstance();
        EmeraldMod.LOGGER.info("✅ Retrofit Confirmation Screen");

        // ✅ NEW: Retrofit FORCE confirmation screen
        RetrofitForceConfirmationScreen.getInstance();
        EmeraldMod.LOGGER.info("✅ Retrofit Force Confirmation Screen");

        // Retrofit loading screen
        RetrofitLoadingScreen.getInstance();
        EmeraldMod.LOGGER.info("✅ Retrofit Loading Screen");

        // Retrofit overlay (minimized state)
        RetrofitOverlayRenderer.register();
        EmeraldMod.LOGGER.info("✅ Retrofit Overlay Renderer");

        // Retrofit reminder widget (top-right corner)
        RetrofitReminderWidget.register();
        EmeraldMod.LOGGER.info("✅ Retrofit Reminder Widget");
    }

    /**
     * PHASE 4: Register rendering components
     */
    private void registerRendering() {
        EmeraldMod.LOGGER.info("--- Phase 4: Rendering ---");

        // Tooltip handler
        TooltipHandler.register();
        EmeraldMod.LOGGER.info("✅ Tooltip Handler");

        // Effect sprite loader
        EffectSpriteLoader.register();
        EmeraldMod.LOGGER.info("✅ Effect Sprite Loader");

        // ✅ NEW: Totem animation handler
        TotemAnimationHandler.register();
        EmeraldMod.LOGGER.info("✅ Totem Animation Handler");
    }

    /**
     * PHASE 5: Register world tracking
     * ✅ FIXED: Proper cleanup ketika disconnect dan world change + State reset
     */
    private void registerWorldTracking() {
        EmeraldMod.LOGGER.info("--- Phase 5: World Tracking ---");

        // Track ketika join server/world
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // ✅ FIXED: Better world detection for dedicated servers
            String worldName = null;

            if (client.getServer() != null) {
                // Integrated server (singleplayer/LAN)
                worldName = client.getServer().getSaveProperties().getLevelName();
                EmeraldMod.LOGGER.info("[Client] Joining integrated server world: {}", worldName);
            } else if (client.getCurrentServerEntry() != null) {
                // Dedicated server with saved entry
                worldName = client.getCurrentServerEntry().address;
                EmeraldMod.LOGGER.info("[Client] Joining dedicated server: {}", worldName);
            } else {
                // Direct connect or other
                worldName = "multiplayer_server";
                EmeraldMod.LOGGER.info("[Client] Joining multiplayer server (generic)");
            }

            if (worldName != null) {
                EmeraldMod.LOGGER.info("[Client] ========================================");
                EmeraldMod.LOGGER.info("[Client] Joining world: {}", worldName);
                EmeraldMod.LOGGER.info("[Client] ========================================");

                // Reset SEMUA UI sebelum update world
                hideAllUI();

                // Update current world
                ClientWorldTracker.updateCurrentWorld(worldName);

                // ✅ IMPORTANT: State akan di-sync dari server via packet
                // Client tidak perlu reset state di sini karena server akan send sync packet
                EmeraldMod.LOGGER.info("[Client] Waiting for effect state sync from server...");
            }
        });

        // ✅ FIXED: Track ketika disconnect - CLEANUP SEMUA + RESET STATE
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            String previousWorld = ClientWorldTracker.getCurrentWorldName();

            EmeraldMod.LOGGER.info("[Client] ========================================");
            EmeraldMod.LOGGER.info("[Client] Disconnecting from: {}", previousWorld);
            EmeraldMod.LOGGER.info("[Client] ========================================");

            // Hide semua UI
            hideAllUI();

            // Reset world tracker
            ClientWorldTracker.reset();

            // ✅ NEW: Reset keybind state ke default
            ModKeybinds.reset();

            // ✅ NEW: Reset totem animation tracking
            TotemAnimationHandler.reset();

            EmeraldMod.LOGGER.info("[Client] ✅ All UI hidden, state reset, keybinds reset, and totem tracking reset");
        });

        // Track setiap tick untuk detect world changes
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // ✅ FIXED: Better world detection
            String currentWorld = null;

            if (client.getServer() != null) {
                currentWorld = client.getServer().getSaveProperties().getLevelName();
            } else if (client.getCurrentServerEntry() != null) {
                currentWorld = client.getCurrentServerEntry().address;
            } else if (client.getNetworkHandler() != null) {
                currentWorld = "multiplayer_server";
            }

            if (currentWorld != null) {
                String trackedWorld = ClientWorldTracker.getCurrentWorldName();

                // Detect world change
                if (trackedWorld != null && !trackedWorld.equals(currentWorld)) {
                    EmeraldMod.LOGGER.info("[Client] ========================================");
                    EmeraldMod.LOGGER.info("[Client] World changed: {} -> {}", trackedWorld, currentWorld);
                    EmeraldMod.LOGGER.info("[Client] ========================================");

                    // Hide SEMUA UI ketika world berubah
                    hideAllUI();

                    // Update current world
                    ClientWorldTracker.updateCurrentWorld(currentWorld);

                    // ✅ NEW: Reset keybind state ketika world berubah
                    ModKeybinds.reset();

                    EmeraldMod.LOGGER.info("[Client] ✅ UI reset and keybinds reset for new world");
                }
            }
        });

        EmeraldMod.LOGGER.info("✅ World Tracking System (with State Reset)");
    }

    /**
     * Helper method untuk hide semua UI
     */
    private void hideAllUI() {
        try {
            RubyOreScanningScreen.hide();
            RetrofitLoadingScreen.hide();
            RetrofitConfirmationScreen.hide();
            RetrofitForceConfirmationScreen.hide(); // ✅ NEW
            RetrofitOverlayRenderer.deactivate();
            RetrofitReminderWidget.hide();

            EmeraldMod.LOGGER.info("[Client] All UI components hidden");
        } catch (Exception e) {
            EmeraldMod.LOGGER.error("[Client] Error hiding UI: {}", e.getMessage());
        }
    }

    /**
     * PHASE 6: Log client initialization complete
     */
    private void logClientInitComplete() {
        EmeraldMod.LOGGER.info("========================================");
        EmeraldMod.LOGGER.info("Emerald Mod Client Initialized!");
        EmeraldMod.LOGGER.info("========================================");
        EmeraldMod.LOGGER.info("");

        // Keybind info
        EmeraldMod.LOGGER.info("⌨️ CLIENT KEYBINDS:");
        EmeraldMod.LOGGER.info("  - [V] Toggle Tool Effects");
        EmeraldMod.LOGGER.info("  - [B] Toggle Armor Effects");
        EmeraldMod.LOGGER.info("  - [M] Maximize Retrofit Screen");
        EmeraldMod.LOGGER.info("  - [N] Generate Now (from widget)");
        EmeraldMod.LOGGER.info("  - [J] Toggle Hide Widgets");
        EmeraldMod.LOGGER.info("  - Customizable in Controls menu");
        EmeraldMod.LOGGER.info("");

        // UI info
        EmeraldMod.LOGGER.info("🖥️ CLIENT UI:");
        EmeraldMod.LOGGER.info("  - Ruby Ore Scanning Screen 🔍");
        EmeraldMod.LOGGER.info("  - Retrofit Confirmation Dialog");
        EmeraldMod.LOGGER.info("  - Retrofit Force Confirmation Dialog ✨");
        EmeraldMod.LOGGER.info("  - Retrofit Loading Screen");
        EmeraldMod.LOGGER.info("  - Retrofit Reminder Widget");
        EmeraldMod.LOGGER.info("  - Status Tooltips");
        EmeraldMod.LOGGER.info("  - Per-World UI Tracking ✨");
        EmeraldMod.LOGGER.info("  - Custom Totem Pop Animations ✨");
        EmeraldMod.LOGGER.info("");

        // ✅ NEW: State sync info
        EmeraldMod.LOGGER.info("💾 STATE MANAGEMENT:");
        EmeraldMod.LOGGER.info("  - ✅ Auto-sync from server on join");
        EmeraldMod.LOGGER.info("  - ✅ Persistent effect states");
        EmeraldMod.LOGGER.info("  - ✅ Reset on disconnect/world change");
        EmeraldMod.LOGGER.info("");

        EmeraldMod.LOGGER.info("========================================");
        EmeraldMod.LOGGER.info("Client ready! 🎮");
        EmeraldMod.LOGGER.info("========================================");
    }
}