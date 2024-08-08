package com.akicater.blocks;

import com.akicater.ItemPlacer;
import com.akicater.blocks.codecs.RotationCodec;
import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.akicater.ItemPlacer.LAYING_ITEM;

public class layingItemBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    //NORTH, SOUTH, EAST, WEST, UP, DOWN (6)
    public RotationCodec rotation = new RotationCodec();
    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    public List<Vec3d> positions = new ArrayList<>(
            List.of(
                    new Vec3d(0.5F, 0.5F, 0.975F),
                    new Vec3d(0.5F, 0.5F, 0.025F),
                    new Vec3d(0.975F, 0.5F, 0.5F),
                    new Vec3d(0.025F, 0.5F, 0.5F),
                    new Vec3d(0.5F, 0.975F, 0.5F),
                    new Vec3d(0.5F, 0.025F, 0.5F)
            )
    );

    Boolean isInventoryClear() {
        for (ItemStack itemStack : inventory) {
            if (!ItemStack.EMPTY.equals(itemStack)) {
                return false;
            }
        }
        return true;
    }

    public void dropItem(int i) {
        ItemStack itemStack = inventory.get(i);
        if(!itemStack.isEmpty() && world != null) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            inventory.set(i, ItemStack.EMPTY);
            this.markDirty();
            world.updateComparators(pos, LAYING_ITEM);
        }
    }

    public layingItemBlockEntity(BlockPos pos, BlockState state) {
        super(ItemPlacer.LAYING_ITEM_BLOCK_ENTITY, pos, state);
    }
    public void rotate(float degrees, int dir) {
        switch (dir){
            case 0 -> rotation.list.set(0, rotation.list.get(0) + degrees);
            case 1 -> rotation.list.set(1, rotation.list.get(1) -degrees);
            case 2 -> rotation.list.set(2, rotation.list.get(2) + degrees);
            case 3 -> rotation.list.set(3, rotation.list.get(3) -degrees);
            case 4 -> rotation.list.set(4, rotation.list.get(4) + degrees);
            case 5 -> rotation.list.set(5, rotation.list.get(5) -degrees);
        }
        markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.clear();
        Inventories.readNbt(nbt, inventory);
        if (nbt.contains("rotation")) {
            RotationCodec.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("rotation")).resultOrPartial(LOGGER::error).ifPresent(quat -> {
                this.rotation = quat;
            });
        }
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        RotationCodec.CODEC.encodeStart(NbtOps.INSTANCE, this.rotation).resultOrPartial(LOGGER::error).ifPresent(
                rotation -> nbt.put("rotation", rotation)
        );
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            markDirtyInWorld(this.world, this.pos, this.getCachedState());
        }
    }

    protected void markDirtyInWorld(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);

        if (!world.isClient()) {
            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        }
    }

    public void clear() {
        inventory.clear();
    }
}