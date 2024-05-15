package com.akicater.blocks;

import com.akicater.Itemplacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class layingItemBlockEntity extends BlockEntity {

    public Vec3d p = new Vec3d(0,0,0);
    public Quaternionf quaternionf = new Quaternionf();

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public layingItemBlockEntity(BlockPos pos, BlockState state) {
        super(Itemplacer.LAYING_ITEM_BLOCK_ENTITY, pos, state);

        switch (state.get(Properties.FACING)){
            case NORTH -> {
                p = new Vec3d(0.5F, 0.5F, 0.025F);
                quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees(180);
            }
            case SOUTH -> {
                p = new Vec3d(0.5F, 0.5F, 0.975F);
                quaternionf = RotationAxis.POSITIVE_X.rotationDegrees(0);
            }
            case WEST -> {
                p = new Vec3d(0.025F, 0.5F, 0.5F);
                quaternionf = RotationAxis.NEGATIVE_Y.rotationDegrees(90);
            }
            case EAST -> {
                p = new Vec3d(0.975F, 0.5F, 0.5F);
                quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees(90);
            }
            case UP -> {
                p = new Vec3d(0.5F, 0.975F, 0.5F);
                quaternionf = RotationAxis.NEGATIVE_X.rotationDegrees(90.f);
            }
            case DOWN -> {
                p = new Vec3d(0.5F, 0.025F, 0.5F);
                quaternionf = RotationAxis.POSITIVE_X.rotationDegrees(90.f);
            }
        };
    }

    void rotate(BlockState state, int degrees) {
        switch (state.get(Properties.FACING)){
            case NORTH -> {
                quaternionf.rotateLocalZ((float) Math.toRadians(degrees));
            }
            case SOUTH -> {
                quaternionf.rotateLocalZ((float) Math.toRadians(-degrees));
            }
            case WEST -> {
                quaternionf.rotateLocalX((float) Math.toRadians(degrees));
            }
            case EAST -> {
                quaternionf.rotateLocalX((float) Math.toRadians(-degrees));
            }
            case UP -> {
                quaternionf.rotateLocalY((float) Math.toRadians(degrees));
            }
            case DOWN -> {
                quaternionf.rotateLocalY((float) Math.toRadians(-degrees));
            }
        };
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
        inventory.clear();
        Inventories.readNbt(nbt, inventory);
        quaternionf = new Quaternionf(nbt.getFloat("a"), nbt.getFloat("b"), nbt.getFloat("c"), nbt.getFloat("d"));
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("a", quaternionf.x);
        nbt.putFloat("b", quaternionf.y);
        nbt.putFloat("c", quaternionf.z);
        nbt.putFloat("d", quaternionf.w);
        Inventories.writeNbt(nbt, inventory);
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
    public ItemStack getStack() {
        return inventory.get(0);
    }
    public void setStack(ItemStack stack) {
        inventory.set(0, stack);
    }
}