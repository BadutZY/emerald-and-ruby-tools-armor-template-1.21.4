package com.example.emeraldmod.client;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.network.RetrofitDecisionPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * ✅ NEW: Screen konfirmasi untuk FORCE re-generation
 * Memberikan warning lebih jelas sebelum reset & start
 */
@Environment(EnvType.CLIENT)
public class RetrofitForceConfirmationScreen extends Screen {

    private static RetrofitForceConfirmationScreen instance;
    private ButtonWidget yesButton;
    private ButtonWidget noButton;

    public RetrofitForceConfirmationScreen() {
        super(Text.literal("Force Ore Re-generation"));
    }

    public static RetrofitForceConfirmationScreen getInstance() {
        if (instance == null) {
            instance = new RetrofitForceConfirmationScreen();
        }
        return instance;
    }

    public static void show() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            RetrofitForceConfirmationScreen screen = getInstance();
            client.execute(() -> {
                EmeraldMod.LOGGER.info("[ForceConfirmation] Showing FORCE confirmation screen");
                client.setScreen(screen);
            });
        }
    }

    public static void hide() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.currentScreen instanceof RetrofitForceConfirmationScreen) {
            client.execute(() -> client.setScreen(null));
        }
        instance = null;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // YES Button (Orange - warning color)
        yesButton = ButtonWidget.builder(Text.literal("YES, FORCE RE-GENERATE"), button -> {
                    onYesPressed();
                })
                .dimensions(centerX - 155, centerY + 60, 150, 30)
                .build();

        // NO Button (Green - safe option)
        noButton = ButtonWidget.builder(Text.literal("NO, CANCEL"), button -> {
                    onNoPressed();
                })
                .dimensions(centerX + 5, centerY + 60, 150, 30)
                .build();

        addDrawableChild(yesButton);
        addDrawableChild(noButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark background
        context.fill(0, 0, this.width, this.height, 0xFF1a1a1a);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // ⚠️ WARNING HEADER
        drawCenteredText(context, "⚠️ FORCE RE-GENERATION", centerX, centerY - 90, 0xFF5555, 2.0f);

        // Main warning message
        drawCenteredText(context, "This will RE-GENERATE ALL ores!",
                centerX, centerY - 55, 0xFFFFFF, 1.3f);

        // Warning details
        drawCenteredText(context, "• All existing ore data will be reset",
                centerX, centerY - 30, 0xFFAA00, 1.0f);
        drawCenteredText(context, "• New ores will be added to all chunks",
                centerX, centerY - 15, 0xFFAA00, 1.0f);
        drawCenteredText(context, "• This may take several minutes",
                centerX, centerY, 0xFFAA00, 1.0f);

        // When to use
        drawCenteredText(context, "Use this when:",
                centerX, centerY + 20, 0xCCCCCC, 0.9f);
        drawCenteredText(context, "→ Mod was updated with new ores",
                centerX, centerY + 33, 0xAAAAAA, 0.85f);
        drawCenteredText(context, "→ You want to fix incomplete generation",
                centerX, centerY + 44, 0xAAAAAA, 0.85f);

        // Render buttons
        super.render(context, mouseX, mouseY, delta);

        // Button descriptions
        drawCenteredText(context, "⚠️ Reset & Re-generate",
                centerX - 80, centerY + 95, 0xFFAA00, 0.75f);
        drawCenteredText(context, "✓ Keep existing",
                centerX + 80, centerY + 95, 0x55FF55, 0.75f);
    }

    private void onYesPressed() {
        EmeraldMod.LOGGER.info("[ForceConfirmation] User chose YES - Force re-generation");

        // Send YES decision to server WITH force flag
        RetrofitDecisionPacket.sendDecision(true, true); // ✅ force = true

        // Close confirmation screen
        this.close();

        // Server will:
        // 1. Reset retrofit state
        // 2. Start retrofit
        // 3. Send show loading packet back to client
    }

    private void onNoPressed() {
        EmeraldMod.LOGGER.info("[ForceConfirmation] User chose NO - Cancelled");

        // Just close the screen
        this.close();
    }

    private void drawCenteredText(DrawContext context, String text, int x, int y, int color, float scale) {
        context.getMatrices().push();

        if (scale != 1.0f) {
            context.getMatrices().scale(scale, scale, 1.0f);
            x = (int) (x / scale);
            y = (int) (y / scale);
        }

        int textWidth = this.textRenderer.getWidth(text);
        x = x - textWidth / 2;

        context.drawText(this.textRenderer, text, x, y, color, true);

        context.getMatrices().pop();
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // ESC acts as NO
        onNoPressed();
        return false;
    }
}