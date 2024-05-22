package com.akicater.network;

import com.akicater.Itemplacer;
import com.akicater.blocks.layingItemBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

import static com.akicater.Itemplacer.LAYING_ITEM;
import static com.akicater.Itemplacer.dirToInt;

public class ItemPlacePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = player.getMainHandStack();
        World world = player.getEntityWorld();
        BlockPos pos = buf.readBlockPos();
        if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            Direction dir = buf.readBlockHitResult().getSide().getOpposite();
            BlockState state = LAYING_ITEM.getDefaultState();
            world.setBlockState(pos, state);
            state.initShapeCache();
            layingItemBlockEntity blockEntity = (layingItemBlockEntity)world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity != null) {
                int i = dirToInt(dir);
                blockEntity.directions.list.set(i, true);
                blockEntity.inventory.set(i, stack);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                blockEntity.markDirty();
            }
        } else if (world.getBlockState(pos).getBlock() == LAYING_ITEM) {
            Direction dir = buf.readBlockHitResult().getSide().getOpposite();
            layingItemBlockEntity blockEntity = (layingItemBlockEntity)world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity != null) {
                int i = dirToInt(dir);
                if(blockEntity.inventory.get(i).isEmpty()) {
                    player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    blockEntity.directions.list.set(i, true);
                    blockEntity.inventory.set(i, stack);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    blockEntity.markDirty();
                }
            }
        }
    }

}
