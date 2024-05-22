package com.akicater.blocks.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class QuaternionfsWithCodec {
    public static final Codec<QuaternionfsWithCodec> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.QUATERNIONF.listOf().fieldOf("quaternionfs").forGetter(list -> list.list)
            ).apply(instance, QuaternionfsWithCodec::new)
    );

    public List<Quaternionf> list = new ArrayList<>(
            List.of(
                    RotationAxis.POSITIVE_X.rotationDegrees(0),     //SOUTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(180),   //NORTH
                    RotationAxis.POSITIVE_Y.rotationDegrees(90),    //EAST
                    RotationAxis.NEGATIVE_Y.rotationDegrees(90),    //WEST
                    RotationAxis.NEGATIVE_X.rotationDegrees(90),    //UP
                    RotationAxis.POSITIVE_X.rotationDegrees(90)     //DOWN
            )
    );

    public QuaternionfsWithCodec(List<Quaternionf> list) {
        this.list = new ArrayList<>(list);
    }

    public QuaternionfsWithCodec() {

    }
}
