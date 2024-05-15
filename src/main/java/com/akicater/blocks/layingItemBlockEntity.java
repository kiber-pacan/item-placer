package com.akicater.blocks;

import com.akicater.Itemplacer;
import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignText;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.slf4j.Logger;



public class layingItemBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public Vec3d p = new Vec3d(0,0,0);
    public QuaternionfWithCodec quaternionf = new QuaternionfWithCodec();

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    QuaternionfWithCodec quatToQuatWithCodec(Quaternionf quaternionf) {
        return new QuaternionfWithCodec(quaternionf);
    }

    public layingItemBlockEntity(BlockPos pos, BlockState state) {
        super(Itemplacer.LAYING_ITEM_BLOCK_ENTITY, pos, state);

        switch (state.get(Properties.FACING)){
            case NORTH -> {
                p = new Vec3d(0.5F, 0.5F, 0.025F);
                quaternionf = quatToQuatWithCodec(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
            case SOUTH -> {
                p = new Vec3d(0.5F, 0.5F, 0.975F);
                quaternionf = quatToQuatWithCodec(RotationAxis.POSITIVE_X.rotationDegrees(0));
            }
            case WEST -> {
                p = new Vec3d(0.025F, 0.5F, 0.5F);
                quaternionf = quatToQuatWithCodec(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
            }
            case EAST -> {
                p = new Vec3d(0.975F, 0.5F, 0.5F);
                quaternionf = quatToQuatWithCodec(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            }
            case UP -> {
                p = new Vec3d(0.5F, 0.975F, 0.5F);
                quaternionf = quatToQuatWithCodec(RotationAxis.NEGATIVE_X.rotationDegrees(90.f));
            }
            case DOWN -> {
                p = new Vec3d(0.5F, 0.025F, 0.5F);
                quaternionf = quatToQuatWithCodec(RotationAxis.POSITIVE_X.rotationDegrees(90.f));
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
        RegistryOps<NbtElement> dynamicOps = registryLookup.getOps(NbtOps.INSTANCE);
        if (nbt.contains("quat")) {
            QuaternionfWithCodec.CODEC1.parse(dynamicOps, nbt.getCompound("quat")).resultOrPartial(LOGGER::error).ifPresent(quat -> {
                this.quaternionf = quatToQuatWithCodec(quat);
            });
        }
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        RegistryOps<NbtElement> dynamicOps = registryLookup.getOps(NbtOps.INSTANCE);
        QuaternionfWithCodec.CODEC1.encodeStart(dynamicOps, this.quaternionf).resultOrPartial(LOGGER::error).ifPresent(
                quat -> nbt.put("quat", quat)
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
    public ItemStack getStack() {
        return inventory.get(0);
    }
    public void setStack(ItemStack stack) {
        inventory.set(0, stack);
    }
}