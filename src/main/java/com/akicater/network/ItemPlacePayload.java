package com.akicater.network;

import com.akicater.Itemplacer;
import com.akicater.blocks.layingItemBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record ItemPlacePayload(BlockPos pos, BlockHitResult hitResult) implements CustomPayload {
    public static final CustomPayload.Id<ItemPlacePayload> ID = CustomPayload.id("tutorial:block_highlight");
    public static final PacketCodec<PacketByteBuf, ItemPlacePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeBlockPos(value.pos).writeBlockHitResult(value.hitResult), buf -> new ItemPlacePayload(buf.readBlockPos(), buf.readBlockHitResult()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void receive(ServerPlayerEntity player, BlockPos pos, BlockHitResult hitResult) {
        ItemStack stack = player.getMainHandStack();
        World world = player.getEntityWorld();
        if (stack != ItemStack.EMPTY && world.getBlockState(pos).getBlock() == Blocks.AIR) {
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            Direction dir = hitResult.getSide().getOpposite();
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
