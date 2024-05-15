package com.akicater.network;

import com.akicater.Itemplacer;
import com.akicater.blocks.layingItemBlockEntity;
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

public class ItemPlacePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = player.getMainHandStack();
        World world = player.getEntityWorld();
        BlockPos pos = buf.readBlockPos();
        if (stack != ItemStack.EMPTY && world.getBlockState(pos).getBlock() == Blocks.AIR) {
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            Direction dir = buf.readBlockHitResult().getSide().getOpposite();
            BlockState state = Itemplacer.LAYING_ITEM.getDefaultState().with(Properties.FACING, dir);
            world.setBlockState(pos, state);
            layingItemBlockEntity blockEntity = (layingItemBlockEntity)world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.setStack(stack);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                blockEntity.markDirty();
            }
        }
    }
}
