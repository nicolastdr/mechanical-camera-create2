package com.mechanicalcamera.item;
import com.mechanicalcamera.client.PhotoScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;

public class PhotoItem extends Item {
    public PhotoItem(Properties properties) { super(properties); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ItemStack copy = stack.copy();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PhotoScreen.open(copy));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
