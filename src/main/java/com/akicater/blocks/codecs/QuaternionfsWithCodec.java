package com.akicater.blocks.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuaternionfsWithCodec {
    public static final Codec<QuaternionfsWithCodec> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.QUATERNIONF.listOf().fieldOf("quaternionfs").forGetter(list -> list.list)
            ).apply(instance, QuaternionfsWithCodec::new)
    );

    Random random = new Random();

    public List<Quaternionf> list = new ArrayList<>(
            List.of(
                    RotationAxis.POSITIVE_X.rotationDegrees(0).rotateZ(random.nextFloat(-360,360)),     //SOUTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(180).rotateZ(random.nextFloat(-360,360)),   //NORTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(90).rotateZ(random.nextFloat(-360,360)),    //EAST
                    RotationAxis.NEGATIVE_Y.rotationDegrees(90).rotateZ(random.nextFloat(-360,360)),    //WEST
                    RotationAxis.NEGATIVE_X.rotationDegrees(90).rotateZ(random.nextFloat(-360,360)),    //UP
                    RotationAxis.POSITIVE_X.rotationDegrees(90).rotateZ(random.nextFloat(-360,360))    //DOWN
            )
    );

    public QuaternionfsWithCodec(List<Quaternionf> list) {
        this.list = new ArrayList<>(list);
    }

    public QuaternionfsWithCodec() {

    }
}
