package com.akicater.network;

import com.akicater.Itemplacer;
import com.akicater.blocks.layingItemBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import static com.akicater.Itemplacer.LOGGER;

public class RotateItemPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        World world = player.getEntityWorld();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getChunk(pos).getBlockEntity(pos);
        //LOGGER.info(blockEntity.toString());
        if (blockEntity instanceof layingItemBlockEntity) {
            ((layingItemBlockEntity) blockEntity).rotate(state, buf.readFloat());
        }
    }
}
