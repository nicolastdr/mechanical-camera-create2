package com.mechanicalcamera;

import com.mechanicalcamera.registry.ModBlockEntities;
import com.mechanicalcamera.registry.ModBlocks;
import com.mechanicalcamera.registry.ModItems;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(MechanicalCameraMod.MOD_ID)
public final class MechanicalCameraMod {
    public static final String MOD_ID = "mechanicalcamera";

    public MechanicalCameraMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreativeTabContents);
        modEventBus.addListener(this::commonSetup);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.MECHANICAL_CAMERA.get(),
                (blockEntity, side) -> side == net.minecraft.core.Direction.UP
                        ? blockEntity.getPaperHandler() : null
        );
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.MECHANICAL_CAMERA.get());
            event.accept(ModItems.PHOTO.get());
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
                BlockStressValues.IMPACTS.register(ModBlocks.MECHANICAL_CAMERA.get(), () -> 4.0)
        );
    }
}
