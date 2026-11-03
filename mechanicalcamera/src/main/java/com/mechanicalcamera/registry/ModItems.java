package com.mechanicalcamera.registry;

import com.mechanicalcamera.MechanicalCameraMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MechanicalCameraMod.MOD_ID);
    public static final DeferredItem<BlockItem> MECHANICAL_CAMERA = ITEMS.register(
            "mechanical_camera", () -> new BlockItem(ModBlocks.MECHANICAL_CAMERA.get(), new Item.Properties())
    );
    public static final DeferredItem<Item> PHOTO = ITEMS.register(
            "photo", () -> new Item(new Item.Properties().stacksTo(64))
    );
    private ModItems() {}
}