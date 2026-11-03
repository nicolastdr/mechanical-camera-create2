package com.mechanicalcamera.block;
import com.mojang.serialization.MapCodec;
import com.mechanicalcamera.blockentity.MechanicalCameraBlockEntity;
import com.mechanicalcamera.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class MechanicalCameraBlock extends BaseEntityBlock {
    public static final MapCodec<MechanicalCameraBlock> CODEC = simpleCodec(MechanicalCameraBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override public MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    public MechanicalCameraBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    @Override public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    @Override public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MechanicalCameraBlockEntity(pos, state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.MECHANICAL_CAMERA.get(),
            MechanicalCameraBlockEntity::serverTick);
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() != Direction.UP || !stack.is(Items.PAPER))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.mayBuild()) return ItemInteractionResult.FAIL;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MechanicalCameraBlockEntity camera)) return ItemInteractionResult.FAIL;
        ItemStack remainder = camera.insertPaper(stack.copyWithCount(1));
        if (remainder.isEmpty() && !player.getAbilities().instabuild) stack.shrink(1);
        return ItemInteractionResult.SUCCESS;
    }
}
