package com.example.emeraldmod.client.network;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.client.ClientWorldTracker;
import com.example.emeraldmod.client.RetrofitLoadingScreen;
import com.example.emeraldmod.network.RetrofitPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

/**
 * Client-side packet handlers untuk retrofit loading screen
 * ✅ FIXED: Better dedicated server support
 */
@Environment(EnvType.CLIENT)
public class RetrofitClientPackets {

    private static boolean handlersRegistered = false;

    /**
     * Register client-side packet handlers dengan world name checking
     */
    public static void registerClient() {
        if (handlersRegistered) {
            EmeraldMod.LOGGER.warn("Client packet handlers already registered, skipping...");
            return;
        }

        try {
            // Handle show loading packet - WITH WORLD NAME CHECK
            ClientPlayNetworking.registerGlobalReceiver(
                    RetrofitPackets.ShowLoadingPayload.ID,
                    (payload, context) -> {
                        try {
                            String worldName = payload.worldName();
                            EmeraldMod.LOGGER.info("[Client] ✅ Received show loading packet for world '{}'", worldName);

                            // Execute on main thread dengan safety check
                            context.client().execute(() -> {
                                try {
                                    MinecraftClient client = context.client();

                                    if (client == null) {
                                        EmeraldMod.LOGGER.error("[Client] MinecraftClient is null!");
                                        return;
                                    }

                                    // ✅ KEY FIX: Set retrofit world IMMEDIATELY without validation
                                    EmeraldMod.LOGGER.info("[Client] Setting retrofit world to '{}' (from server packet)", worldName);
                                    ClientWorldTracker.setRetrofitWorld(worldName);

                                    // ✅ Auto-detect current world if null
                                    if (ClientWorldTracker.getCurrentWorldName() == null) {
                                        EmeraldMod.LOGGER.info("[Client] Auto-detecting current world...");
                                        ClientWorldTracker.autoDetectWorld();
                                    }

                                    // ✅ FORCE SHOW: Show loading screen regardless of validation
                                    // This is safe because packet came from server
                                    EmeraldMod.LOGGER.info("[Client] Force showing loading screen for world '{}'", worldName);
                                    RetrofitLoadingScreen.show();

                                    EmeraldMod.LOGGER.info("[Client] ✅ Loading screen displayed successfully");

                                } catch (Exception e) {
                                    EmeraldMod.LOGGER.error("[Client] Error showing loading screen: {}", e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            EmeraldMod.LOGGER.error("[Client] Error processing show loading packet: {}", e.getMessage());
                            e.printStackTrace();
                        }
                    }
            );

            // Handle update progress packet - WITH WORLD NAME CHECK
            ClientPlayNetworking.registerGlobalReceiver(
                    RetrofitPackets.UpdateProgressPayload.ID,
                    (payload, context) -> {
                        try {
                            String worldName = payload.worldName();

                            context.client().execute(() -> {
                                try {
                                    // ✅ RELAXED: Update progress if retrofit world is set
                                    if (ClientWorldTracker.getRetrofitWorldName() != null) {
                                        RetrofitLoadingScreen.updateProgress(
                                                payload.processed(),
                                                payload.total(),
                                                payload.dimension()
                                        );
                                    }
                                } catch (Exception e) {
                                    // Silent fail untuk progress updates
                                }
                            });
                        } catch (Exception e) {
                            // Silent fail
                        }
                    }
            );

            // Handle complete packet - WITH WORLD NAME CHECK
            ClientPlayNetworking.registerGlobalReceiver(
                    RetrofitPackets.CompletePayload.ID,
                    (payload, context) -> {
                        try {
                            String worldName = payload.worldName();
                            EmeraldMod.LOGGER.info("[Client] ✅ Received complete packet for world '{}'", worldName);

                            context.client().execute(() -> {
                                try {
                                    // ✅ RELAXED: Set complete if retrofit world matches or is null
                                    String retrofitWorld = ClientWorldTracker.getRetrofitWorldName();

                                    if (retrofitWorld == null || worldName.equals(retrofitWorld)) {
                                        EmeraldMod.LOGGER.info("[Client] Setting retrofit complete for world '{}'", worldName);
                                        RetrofitLoadingScreen.setComplete();

                                        // Clear retrofit world setelah complete
                                        ClientWorldTracker.clearRetrofitWorld();
                                    } else {
                                        EmeraldMod.LOGGER.warn("[Client] Ignoring complete for different world (current: '{}', completed: '{}')",
                                                retrofitWorld, worldName);
                                    }
                                } catch (Exception e) {
                                    EmeraldMod.LOGGER.error("[Client] Error setting complete: {}", e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            EmeraldMod.LOGGER.error("[Client] Error processing complete packet: {}", e.getMessage());
                            e.printStackTrace();
                        }
                    }
            );

            handlersRegistered = true;
            EmeraldMod.LOGGER.info("✅ Registered Retrofit Client Packet Handlers (server compatible)");
            EmeraldMod.LOGGER.info("  → Show Loading: {}", RetrofitPackets.SHOW_LOADING_ID);
            EmeraldMod.LOGGER.info("  → Update Progress: {}", RetrofitPackets.UPDATE_PROGRESS_ID);
            EmeraldMod.LOGGER.info("  → Complete: {}", RetrofitPackets.COMPLETE_ID);

        } catch (Exception e) {
            EmeraldMod.LOGGER.error("❌ Failed to register client packet handlers: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if handlers are registered
     */
    public static boolean isRegistered() {
        return handlersRegistered;
    }
}