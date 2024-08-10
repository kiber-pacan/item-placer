package com.akicater;

import com.akicater.blocks.layingItemBlockEntity;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class layingItemBER implements BlockEntityRenderer<layingItemBlockEntity> {

    ItemPlacerConfig config = AutoConfig.getConfigHolder(ItemPlacerConfig.class).getConfig();

    public static List<Quaternionf> list = new ArrayList<>(
            List.of(
                    RotationAxis.POSITIVE_X.rotationDegrees(0),   //SOUTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(180),   //NORTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(90),    //EAST
                    RotationAxis.NEGATIVE_Y.rotationDegrees(90),    //WEST
                    RotationAxis.NEGATIVE_X.rotationDegrees(90),    //UP
                    RotationAxis.POSITIVE_X.rotationDegrees(90)    //DOWN
            )
    );

    public layingItemBER(BlockEntityRendererFactory.Context ctx) {}

    static int getLight(World world, BlockPos pos){
        return LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos));
    }

    public Quaternionf rotateZ(float angle, Quaternionf dest) {
        float sin = Math.sin(angle * 0.5f);
        float cos = Math.cosFromSin(sin, angle * 0.5f);
        return new Quaternionf(dest.x * cos + dest.y * sin,
                dest.y * cos - dest.x * sin,
                dest.w * sin + dest.z * cos,
                dest.w * cos - dest.z * sin);
    }

    public Quaternionf rotateX(float angle, Quaternionf dest) {
        float sin = Math.sin(angle * 0.5f);
        float cos = Math.cosFromSin(sin, angle * 0.5f);
        return new Quaternionf(dest.w * sin + dest.x * cos,
                dest.y * cos + dest.z * sin,
                dest.z * cos - dest.y * sin,
                dest.w * cos - dest.x * sin);
    }

    @Override
    public void render(layingItemBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        float absoluteSize = config.absoluteSize;
        boolean oldRendering = config.oldRendering;

        float itemSize = config.tempItemSize * absoluteSize;
        float blockSize = config.tempBlockSize * absoluteSize;

        int x = getLight(entity.getWorld(), entity.getPos());

        for (int i = 0; i < 6; i++) {
            if(!entity.inventory.get(i).isEmpty()) {
                ItemStack item = entity.inventory.get(i);

                matrices.push();

                matrices.translate(entity.positions.get(i).x, entity.positions.get(i).y, entity.positions.get(i).z);

                // Differentiate item and block rendering
                if(item.getItem() instanceof BlockItem) {
                    // Differentiate new and old block rendering
                    if (!oldRendering) {
                        matrices.multiply(rotateX(Math.toRadians(-90), rotateZ(Math.toRadians(entity.rotation.list.get(i)), list.get(i))));
                        matrices.translate(0,0.25 * blockSize - 0.025,0);
                    } else {
                        matrices.multiply(rotateZ(Math.toRadians(entity.rotation.list.get(i)), list.get(i)));
                    }
                    matrices.scale(blockSize, blockSize, blockSize);
                } else {
                    matrices.scale(itemSize, itemSize, itemSize);
                    matrices.multiply(rotateZ(Math.toRadians(entity.rotation.list.get(i)), list.get(i)));
                }

                itemRenderer.renderItem(item, ModelTransformationMode.FIXED, x, overlay, matrices,vertexConsumers, entity.getWorld(),1);

                matrices.pop();
            }
        }
    }
}