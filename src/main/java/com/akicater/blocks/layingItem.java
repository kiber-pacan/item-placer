package com.akicater.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class layingItem extends FacingBlock implements Waterloggable, BlockEntityProvider {

    public layingItem(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof layingItemBlockEntity entity && !player.isSneaking()) {
            entity.rotate(state, 2);
            return ActionResult.success(true);
        } else if (world.getBlockEntity(pos) instanceof layingItemBlockEntity entity && player.isSneaking()) {
            entity.rotate(state, 8);
            return ActionResult.success(true);
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new layingItemBlockEntity(pos, state);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof layingItemBlockEntity) {
                layingItemBlockEntity entity = (layingItemBlockEntity) world.getBlockEntity(pos);
                ItemStack itemStack = entity.getStack();

                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);

                entity.clear();
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockView, BlockPos pos, ShapeContext context) {
        switch (state.get(Properties.FACING)){
            case NORTH -> {
                return VoxelShapes.cuboid(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.125f);
            }
            case SOUTH -> {
                return VoxelShapes.cuboid(0.125f, 0.125f, 0.875f, 0.875f, 0.875f, 1.0f);
            }
            case WEST -> {
                return VoxelShapes.cuboid(0.0f, 0.125f, 0.125f, 0.125f, 0.875f, 0.875f);
            }
            case EAST -> {
                return VoxelShapes.cuboid(0.875f, 0.125f, 0.125f, 1.0f, 0.875f, 0.875f);
            }
            case UP -> {
                return VoxelShapes.cuboid(0.125f, 0.875f, 0.125f, 0.875f, 1.0f, 0.875f);
            }
            case DOWN -> {
                return VoxelShapes.cuboid(0.125f, 0.0f, 0.125f, 0.875f, 0.125f, 0.875f);
            }
        };
        return null;
    }
}