package com.akicater.blocks;

import com.akicater.Itemplacer;
import com.akicater.codecs.DirectionsWithCodec;
import com.akicater.codecs.QuaternionfsWithCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.akicater.Itemplacer.LOGGER;
import static com.akicater.Itemplacer.LAYING_ITEM;


public class layingItemBlockEntity extends BlockEntity {
    //NORTH, SOUTH, EAST, WEST, UP, DOWN (6)
    public DirectionsWithCodec directions = new DirectionsWithCodec();
    public QuaternionfsWithCodec quaternions = new QuaternionfsWithCodec();
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
        if(!inventory.get(i).isEmpty() && directions.list.get(i) && world != null) {
            directions.list.set(i, false);
            ItemStack itemStack = this.getStack(i);
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            inventory.set(i, ItemStack.EMPTY);
            this.markDirty();
            world.updateComparators(pos, LAYING_ITEM);
        }
    }

    public layingItemBlockEntity(BlockPos pos, BlockState state) {
        super(Itemplacer.LAYING_ITEM_BLOCK_ENTITY, pos, state);
    }
    public void rotate(float degrees, int dir) {
        switch (dir){
            case 0 -> {
                quaternions.list.get(0).rotateLocalZ((float) Math.toRadians(degrees));
            }
            case 1 -> {
                quaternions.list.get(1).rotateLocalZ((float) Math.toRadians(-degrees));
            }
            case 2 -> {
                quaternions.list.get(2).rotateLocalX((float) Math.toRadians(degrees));
            }
            case 3 -> {
                quaternions.list.get(3).rotateLocalX((float) Math.toRadians(-degrees));
            }
            case 4 -> {
                quaternions.list.get(4).rotateLocalY((float) Math.toRadians(degrees));
            }
            case 5 -> {
                quaternions.list.get(5).rotateLocalY((float) Math.toRadians(-degrees));
            }
        };
        markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup lookup) {
        return createNbt(lookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
        if (nbt.contains("quat")) {
            QuaternionfsWithCodec.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("quat")).resultOrPartial(LOGGER::error).ifPresent(quat -> {
                this.quaternions = quat;
            });
        }
        if (nbt.contains("dir")) {
            DirectionsWithCodec.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("dir")).resultOrPartial(LOGGER::error).ifPresent(dir -> {
                this.directions = dir;
            });
        }
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        QuaternionfsWithCodec.CODEC.encodeStart(NbtOps.INSTANCE, this.quaternions).resultOrPartial(LOGGER::error).ifPresent(
                quat -> nbt.put("quat", quat)
        );
        DirectionsWithCodec.CODEC.encodeStart(NbtOps.INSTANCE, this.directions).resultOrPartial(LOGGER::error).ifPresent(
                dir -> nbt.put("dir", dir)
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
    public ItemStack getStack(int i) {
        return inventory.get(i);
    }

    public void setStack(ItemStack stack, int i) {
        inventory.set(i, stack);
    }
}