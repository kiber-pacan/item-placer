package com.akicater.network;

import com.akicater.ItemPlacer;
import com.akicater.blocks.layingItemBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

import static com.akicater.ItemPlacer.LAYING_ITEM;
import static com.akicater.ItemPlacer.dirToInt;

public class ItemPlacePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = player.getMainHandStack();
        ServerWorld world = player.getServerWorld();
        BlockPos pos = buf.readBlockPos();
        BlockHitResult hitResult = buf.readBlockHitResult();
        if (world.getBlockState(pos).getBlock() == Blocks.AIR || world.getBlockState(pos).getBlock() == Blocks.WATER) {
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            Direction dir = hitResult.getSide().getOpposite();
            BlockState state = ItemPlacer.LAYING_ITEM.getDefaultState();
            if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
                state = state.with(Properties.WATERLOGGED, true);
            }
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
