package com.mechanicalcamera.blockentity;

import com.mechanicalcamera.registry.ModBlockEntities;
import com.mechanicalcamera.registry.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MechanicalCameraBlockEntity extends KineticBlockEntity {
    private boolean wasPowered;

    private final ItemStackHandler paperHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && stack.is(Items.PAPER);
        }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    public MechanicalCameraBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MECHANICAL_CAMERA.get(), pos, state);
    }

    public IItemHandler getPaperHandler() { return paperHandler; }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        boolean poweredNow = hasHorizontalRedstoneSignal();
        boolean risingEdge = poweredNow && !wasPowered;
        wasPowered = poweredNow;

        if (!risingEdge || getSpeed() == 0 || paperHandler.getStackInSlot(0).isEmpty()) return;

        ItemStack paper = paperHandler.getStackInSlot(0).copy();
        paper.shrink(1);
        paperHandler.setStackInSlot(0, paper);
        ejectPhoto();
        setChanged();
        sendData();
    }

    private boolean hasHorizontalRedstoneSignal() {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = worldPosition.relative(direction);
            if (level.getSignal(neighbour, direction.getOpposite()) > 0) return true;
        }
        return false;
    }

    private void ejectPhoto() {
        ItemStack photo = new ItemStack(ModItems.PHOTO.get());
        IItemHandler below = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.below(), Direction.UP);
        if (below != null) {
            for (int slot = 0; slot < below.getSlots() && !photo.isEmpty(); slot++)
                photo = below.insertItem(slot, photo, false);
        }
        if (!photo.isEmpty()) Block.popResource(level, worldPosition.above(), photo);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("PaperInventory", paperHandler.serializeNBT(registries));
        tag.putBoolean("WasPowered", wasPowered);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("PaperInventory")) paperHandler.deserializeNBT(registries, tag.getCompound("PaperInventory"));
        wasPowered = tag.getBoolean("WasPowered");
    }
}
