package com.akicater.network;

import com.akicater.blocks.layingItemBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.akicater.Itemplacer.getDirection;

public class RotateItemPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        World world = player.getEntityWorld();
        BlockEntity blockEntity = world.getChunk(pos).getBlockEntity(pos);
        if (blockEntity instanceof layingItemBlockEntity) {
            ((layingItemBlockEntity) blockEntity).rotate(buf.readFloat(), getDirection(buf.readBlockHitResult()));
        }
    }
}
