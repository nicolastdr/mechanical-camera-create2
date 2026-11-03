package com.mechanicalcamera.registry;

import com.mechanicalcamera.MechanicalCameraMod;
import com.mechanicalcamera.blockentity.MechanicalCameraBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MechanicalCameraMod.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MechanicalCameraBlockEntity>> MECHANICAL_CAMERA =
            BLOCK_ENTITY_TYPES.register("mechanical_camera", () ->
                    BlockEntityType.Builder.of(MechanicalCameraBlockEntity::new, ModBlocks.MECHANICAL_CAMERA.get()).build(null)
            );
    private ModBlockEntities() {}
}