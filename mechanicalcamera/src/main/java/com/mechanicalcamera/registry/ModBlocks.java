package com.mechanicalcamera.registry;

import com.mechanicalcamera.MechanicalCameraMod;
import com.mechanicalcamera.block.MechanicalCameraBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MechanicalCameraMod.MOD_ID);
    public static final DeferredBlock<MechanicalCameraBlock> MECHANICAL_CAMERA = BLOCKS.register(
            "mechanical_camera",
            () -> new MechanicalCameraBlock(BlockBehaviour.Properties.of().strength(3.5F).sound(SoundType.METAL).noOcclusion())
    );
    private ModBlocks() {}
}