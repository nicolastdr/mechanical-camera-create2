package com.mechanicalcamera.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class PhotoScreen extends Screen {
    private final int[] pixels;
    private final int photoWidth, photoHeight;
    public static void open(ItemStack stack) { Minecraft.getInstance().setScreen(new PhotoScreen(stack)); }

    private PhotoScreen(ItemStack stack) {
        super(Component.translatable("screen.mechanicalcamera.photo"));
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = custom == null ? new CompoundTag() : custom.copyTag();
        photoWidth = tag.getInt("PhotoWidth");
        photoHeight = tag.getInt("PhotoHeight");
        pixels = tag.getIntArray("PhotoPixels");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xFF10141B);
        if (photoWidth <= 0 || photoHeight <= 0 || pixels.length != photoWidth * photoHeight) {
            graphics.drawCenteredString(font, Component.translatable("screen.mechanicalcamera.invalid"),
                    width / 2, height / 2, 0xFFFFFFFF);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        int scale = Math.max(1, Math.min(5, Math.min((width - 40) / photoWidth, (height - 70) / photoHeight)));
        int dw = photoWidth * scale, dh = photoHeight * scale;
        int left = (width - dw) / 2, top = (height - dh) / 2;
        graphics.fill(left - 3, top - 3, left + dw + 3, top + dh + 3, 0xFF171717);
        for (int y = 0; y < photoHeight; y++) for (int x = 0; x < photoWidth; x++) {
            int rgb = pixels[y * photoWidth + x] & 0xFFFFFF;
            graphics.fill(left + x * scale, top + y * scale, left + (x + 1) * scale,
                    top + (y + 1) * scale, 0xFF000000 | rgb);
        }
        graphics.drawCenteredString(font, title, width / 2, Math.max(8, top - 18), 0xFFFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("screen.mechanicalcamera.close"),
                width / 2, Math.min(height - 14, top + dh + 12), 0xFFCCCCCC);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
