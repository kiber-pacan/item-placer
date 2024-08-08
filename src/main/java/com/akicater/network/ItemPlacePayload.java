package com.akicater.network;

import com.akicater.ItemPlacer;
import com.akicater.blocks.layingItemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public record ItemPlacePayload(BlockPos pos, BlockHitResult hitResult) implements CustomPayload {
    public static final CustomPayload.Id<ItemPlacePayload> ID = CustomPayload.id("item-placer:place_item");
    public static final PacketCodec<PacketByteBuf, ItemPlacePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeBlockPos(value.pos).writeBlockHitResult(value.hitResult), buf -> new ItemPlacePayload(buf.readBlockPos(), buf.readBlockHitResult()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void receive(ServerPlayerEntity player, BlockPos pos, BlockHitResult hitResult) {
        ItemStack stack = player.getMainHandStack();
        ServerWorld world = player.getServerWorld();
        if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            Direction dir = hitResult.getSide().getOpposite();
            BlockState state = ItemPlacer.LAYING_ITEM.getDefaultState();
            world.setBlockState(pos, state);
            state.initShapeCache();
            layingItemBlockEntity blockEntity = (layingItemBlockEntity)world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity != null) {
                int i = ItemPlacer.dirToInt(dir);
                blockEntity.inventory.set(i, stack);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                blockEntity.markDirty();
            }
        } else if (world.getBlockState(pos).getBlock() == ItemPlacer.LAYING_ITEM) {
            Direction dir = hitResult.getSide().getOpposite();
            layingItemBlockEntity blockEntity = (layingItemBlockEntity)world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity != null) {
                int i = ItemPlacer.dirToInt(dir);
                if(blockEntity.inventory.get(i).isEmpty()) {
                    player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    blockEntity.inventory.set(i, stack);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    blockEntity.markDirty();
                }
            }
        }
    }
}
