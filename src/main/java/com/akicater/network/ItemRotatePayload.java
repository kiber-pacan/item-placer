package com.akicater.network;

import com.akicater.blocks.layingItemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static com.akicater.Itemplacer.*;

public record ItemRotatePayload(BlockPos pos, float degrees, BlockHitResult hitResult) implements CustomPayload {
    public static final Id<ItemRotatePayload> ID = CustomPayload.id("rotate_item");
    public static final PacketCodec<PacketByteBuf, ItemRotatePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeBlockPos(value.pos).writeFloat(value.degrees).writeBlockHitResult(value.hitResult), buf -> new ItemRotatePayload(buf.readBlockPos(), buf.readFloat(), buf.readBlockHitResult()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void receive(ServerPlayerEntity player, BlockPos pos, float degrees, BlockHitResult hitResult) {
        World world = player.getEntityWorld();
        BlockEntity blockEntity = world.getChunk(pos).getBlockEntity(pos);
        if (blockEntity instanceof layingItemBlockEntity) {
            ((layingItemBlockEntity) blockEntity).rotate(degrees, getDirection(hitResult));
        }
    }
}
