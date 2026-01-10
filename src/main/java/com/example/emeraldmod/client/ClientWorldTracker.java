package com.example.emeraldmod.client;

import com.example.emeraldmod.EmeraldMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

/**
 * Tracks current world name on client side
 * ✅ FIXED: Support dedicated server (not just integrated server)
 */
@Environment(EnvType.CLIENT)
public class ClientWorldTracker {

    private static String currentWorldName = null;
    private static String retrofitWorldName = null;
    private static long lastWorldChangeTime = 0;

    /**
     * ✅ FIXED: Update current world - works for both integrated and dedicated servers
     */
    public static void updateCurrentWorld(String worldName) {
        String previous = currentWorldName;
        currentWorldName = worldName;
        lastWorldChangeTime = System.currentTimeMillis();

        if (previous == null) {
            EmeraldMod.LOGGER.info("[ClientWorldTracker] ✅ Initial world set: '{}'", worldName);
        } else if (!previous.equals(worldName)) {
            EmeraldMod.LOGGER.info("[ClientWorldTracker] 🔄 World changed: '{}' -> '{}'", previous, worldName);

            if (retrofitWorldName != null && !retrofitWorldName.equals(worldName)) {
                EmeraldMod.LOGGER.warn("[ClientWorldTracker] ⚠️ Clearing retrofit world '{}' (now in '{}')",
                        retrofitWorldName, worldName);
                retrofitWorldName = null;
            }
        }
    }

    /**
     * ✅ NEW: Auto-detect world name from client connection
     */
    public static void autoDetectWorld() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null) {
            EmeraldMod.LOGGER.warn("[ClientWorldTracker] Client is null");
            return;
        }

        String detectedWorld = null;

        // Try integrated server first (singleplayer/LAN)
        if (client.getServer() != null) {
            detectedWorld = client.getServer().getSaveProperties().getLevelName();
            EmeraldMod.LOGGER.info("[ClientWorldTracker] Detected integrated server world: '{}'", detectedWorld);
        }
        // Try dedicated server (multiplayer)
        else if (client.getNetworkHandler() != null) {
            ClientPlayNetworkHandler handler = client.getNetworkHandler();

            // Get server address or connection info
            if (client.getCurrentServerEntry() != null) {
                // Connected to a saved server
                detectedWorld = client.getCurrentServerEntry().address;
                EmeraldMod.LOGGER.info("[ClientWorldTracker] Detected dedicated server: '{}'", detectedWorld);
            } else {
                // Direct connect or realms
                detectedWorld = "multiplayer_server";
                EmeraldMod.LOGGER.info("[ClientWorldTracker] Connected to multiplayer (using generic name)");
            }
        }

        if (detectedWorld != null && !detectedWorld.equals(currentWorldName)) {
            updateCurrentWorld(detectedWorld);
        }
    }

    /**
     * ✅ FIXED: Set world yang sedang retrofit dengan validation
     */
    public static void setRetrofitWorld(String worldName) {
        if (worldName == null) {
            EmeraldMod.LOGGER.warn("[ClientWorldTracker] Attempted to set null retrofit world");
            return;
        }

        // ✅ FIX: Auto-detect if current world is null
        if (currentWorldName == null) {
            EmeraldMod.LOGGER.warn("[ClientWorldTracker] Current world is null, auto-detecting...");
            autoDetectWorld();
        }

        // ✅ RELAXED: Always set retrofit world (don't require exact match for dedicated servers)
        retrofitWorldName = worldName;
        EmeraldMod.LOGGER.info("[ClientWorldTracker] ✅ Retrofit world set to: '{}'", worldName);

        // If current world is still null, set it to retrofit world
        if (currentWorldName == null) {
            currentWorldName = worldName;
            EmeraldMod.LOGGER.info("[ClientWorldTracker] Set current world to retrofit world: '{}'", worldName);
        }
    }

    /**
     * Clear retrofit world (ketika complete atau cancelled)
     */
    public static void clearRetrofitWorld() {
        if (retrofitWorldName != null) {
            EmeraldMod.LOGGER.info("[ClientWorldTracker] 🧹 Clearing retrofit world: '{}'", retrofitWorldName);
            retrofitWorldName = null;
        }
    }

    /**
     * ✅ FIXED: Check if currently in the world yang sedang retrofit
     * More lenient for dedicated servers
     */
    public static boolean isInRetrofitWorld() {
        if (retrofitWorldName == null) {
            return false;
        }

        // If current world is null, assume we're in the retrofit world
        if (currentWorldName == null) {
            EmeraldMod.LOGGER.debug("[ClientWorldTracker] Current world null, assuming in retrofit world");
            return true;
        }

        boolean inRetrofitWorld = currentWorldName.equals(retrofitWorldName);

        if (!inRetrofitWorld) {
            EmeraldMod.LOGGER.debug("[ClientWorldTracker] Not in retrofit world - current: '{}', retrofit: '{}'",
                    currentWorldName, retrofitWorldName);
        }

        return inRetrofitWorld;
    }

    /**
     * Get current world name
     */
    public static String getCurrentWorldName() {
        return currentWorldName;
    }

    /**
     * Get retrofit world name
     */
    public static String getRetrofitWorldName() {
        return retrofitWorldName;
    }

    /**
     * ✅ FIXED: Reset dengan proper logging
     */
    public static void reset() {
        if (currentWorldName != null || retrofitWorldName != null) {
            EmeraldMod.LOGGER.info("[ClientWorldTracker] 🔄 Resetting - was in world '{}', retrofit world '{}'",
                    currentWorldName, retrofitWorldName);
        }

        currentWorldName = null;
        retrofitWorldName = null;
        lastWorldChangeTime = System.currentTimeMillis();

        EmeraldMod.LOGGER.info("[ClientWorldTracker] ✅ Reset complete");
    }

    /**
     * ✅ MODIFIED: Check if should show retrofit UI - more lenient for dedicated servers
     */
    public static boolean shouldShowRetrofitUI() {
        // Rule 1: Harus ada retrofit world yang di-set
        if (retrofitWorldName == null) {
            return false;
        }

        // ✅ NEW: If current world is null (dedicated server), allow showing UI
        if (currentWorldName == null) {
            EmeraldMod.LOGGER.debug("[ClientWorldTracker] Current world null, allowing retrofit UI");
            return true;
        }

        // Rule 2: Current world harus sama dengan retrofit world
        if (!currentWorldName.equals(retrofitWorldName)) {
            EmeraldMod.LOGGER.debug("[ClientWorldTracker] ❌ Not showing retrofit UI - " +
                    "current: '{}', retrofit: '{}'", currentWorldName, retrofitWorldName);
            return false;
        }

        // Rule 3: Tidak baru saja ganti world (debounce) - reduced to 500ms for better server response
        long timeSinceChange = System.currentTimeMillis() - lastWorldChangeTime;
        if (timeSinceChange < 500) {
            EmeraldMod.LOGGER.debug("[ClientWorldTracker] ⏳ Waiting for world stabilization ({}ms)",
                    timeSinceChange);
            return false;
        }

        return true;
    }

    /**
     * Get time since last world change (untuk debugging)
     */
    public static long getTimeSinceLastWorldChange() {
        return System.currentTimeMillis() - lastWorldChangeTime;
    }

    /**
     * Force clear all state (emergency cleanup)
     */
    public static void forceReset() {
        EmeraldMod.LOGGER.warn("[ClientWorldTracker] ⚠️ FORCE RESET called");
        currentWorldName = null;
        retrofitWorldName = null;
        lastWorldChangeTime = 0;
    }

    /**
     * Get debug info
     */
    public static String getDebugInfo() {
        long timeSinceChange = System.currentTimeMillis() - lastWorldChangeTime;
        return String.format(
                "ClientWorldTracker[current='%s', retrofit='%s', inRetrofit=%s, timeSince=%dms]",
                currentWorldName, retrofitWorldName, isInRetrofitWorld(), timeSinceChange
        );
    }
}