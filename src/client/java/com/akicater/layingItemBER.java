package com.akicater;

import com.akicater.blocks.layingItemBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class layingItemBER implements BlockEntityRenderer<layingItemBlockEntity> {

    public layingItemBER(BlockEntityRendererFactory.Context ctx) {}

    static int getLight(World world, BlockPos pos){
        return LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos));
    }

    @Override
    public void render(layingItemBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        int x = getLight(entity.getWorld(), entity.getPos());

        matrices.push();

        matrices.translate(entity.p.x,entity.p.y,entity.p.z);
        matrices.scale(0.75F, 0.75F, 0.75F);
        matrices.multiply(entity.quaternionf);

        itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.FIXED, x, overlay, matrices,vertexConsumers, entity.getWorld(),1);

        matrices.pop();
    }
}