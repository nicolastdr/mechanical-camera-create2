package com.mechanicalcamera.photo;
import com.mechanicalcamera.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PhotoCapture {
    public static final int WIDTH = 96, HEIGHT = 64;
    private static final double RANGE = 48.0, HALF_FOV_TAN = Math.tan(Math.toRadians(35.0));
    private PhotoCapture() {}

    public static ItemStack capture(Level level, BlockPos pos, Direction facing) {
        int[] pixels = new int[WIDTH * HEIGHT];
        Vec3 forward = new Vec3(facing.getStepX(), 0, facing.getStepZ()).normalize();
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        Vec3 origin = Vec3.atCenterOf(pos).add(forward.scale(0.56)).add(0, 0.05, 0);
        double aspect = (double) WIDTH / HEIGHT;
        for (int y = 0; y < HEIGHT; y++) {
            double vy = (0.5 - (y + 0.5) / HEIGHT) * HALF_FOV_TAN / aspect;
            for (int x = 0; x < WIDTH; x++) {
                double vx = ((x + 0.5) / WIDTH - 0.5) * 2.0 * HALF_FOV_TAN;
                Vec3 ray = forward.add(right.scale(vx)).add(0, vy, 0).normalize();
                BlockHitResult hit = level.clip(new ClipContext(origin, origin.add(ray.scale(RANGE)),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
                pixels[y * WIDTH + x] = hit.getType() == HitResult.Type.BLOCK
                        ? blockColor(level, hit) : skyColor(y);
            }
        }
        ItemStack photo = new ItemStack(ModItems.PHOTO.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt("PhotoWidth", WIDTH);
        tag.putInt("PhotoHeight", HEIGHT);
        tag.putIntArray("PhotoPixels", pixels);
        tag.putInt("CameraX", pos.getX());
        tag.putInt("CameraY", pos.getY());
        tag.putInt("CameraZ", pos.getZ());
        tag.putString("CameraFacing", facing.getName());
        photo.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return photo;
    }

    private static int blockColor(Level level, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        MapColor map = state.getMapColor(level, pos);
        int color = map.col == 0 ? 0x777777 : map.col;
        float face = switch (hit.getDirection()) {
            case UP -> 1.08F;
            case DOWN -> 0.52F;
            default -> 0.82F;
        };
        float light = Math.max(0.28F, Math.max(level.getBrightness(LightLayer.SKY, pos),
                level.getBrightness(LightLayer.BLOCK, pos)) / 15.0F * 0.92F);
        float shade = face * light;
        int r = Math.min(255, Math.round(((color >> 16) & 255) * shade));
        int g = Math.min(255, Math.round(((color >> 8) & 255) * shade));
        int b = Math.min(255, Math.round((color & 255) * shade));
        return (r << 16) | (g << 8) | b;
    }

    private static int skyColor(int y) {
        float t = (float) y / (HEIGHT - 1);
        int r = Math.round(65 + 90 * t), g = Math.round(105 + 90 * t), b = Math.round(155 + 70 * t);
        return (r << 16) | (g << 8) | b;
    }
}
