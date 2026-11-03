package com.mechanicalcamera.blockentity;
import com.mechanicalcamera.registry.ModBlockEntities;
import com.mechanicalcamera.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MechanicalCameraBlockEntity extends BlockEntity {
    private boolean wasPowered;
    private final ItemStackHandler paperHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0 && stack.is(Items.PAPER); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
    };
    public MechanicalCameraBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MECHANICAL_CAMERA.get(), pos, state);
    }
    public IItemHandler getPaperHandler() { return paperHandler; }
    public ItemStack insertPaper(ItemStack stack) { return paperHandler.insertItem(0, stack, false); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanicalCameraBlockEntity camera) {
        boolean poweredNow = camera.hasHorizontalRedstoneSignal();
        boolean risingEdge = poweredNow && !camera.wasPowered;
        camera.wasPowered = poweredNow;
        if (!risingEdge || camera.paperHandler.getStackInSlot(0).isEmpty()) return;
        ItemStack paper = camera.paperHandler.getStackInSlot(0).copy();
        paper.shrink(1);
        camera.paperHandler.setStackInSlot(0, paper);
        camera.ejectPhoto();
        camera.setChanged();
    }
    private boolean hasHorizontalRedstoneSignal() {
        if (level == null) return false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = worldPosition.relative(direction);
            if (level.getSignal(neighbour, direction.getOpposite()) > 0) return true;
        }
        return false;
    }
    private void ejectPhoto() {
        if (level == null) return;
        ItemStack photo = new ItemStack(ModItems.PHOTO.get());
        IItemHandler below = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.below(), Direction.UP);
        if (below != null) {
            for (int slot = 0; slot < below.getSlots() && !photo.isEmpty(); slot++)
                photo = below.insertItem(slot, photo, false);
        }
        if (!photo.isEmpty()) Block.popResource(level, worldPosition.above(), photo);
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("PaperInventory", paperHandler.serializeNBT(registries));
        tag.putBoolean("WasPowered", wasPowered);
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PaperInventory")) paperHandler.deserializeNBT(registries, tag.getCompound("PaperInventory"));
        wasPowered = tag.getBoolean("WasPowered");
    }
}
