package com.akicater.blocks.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RotationCodec {
    public static final Codec<RotationCodec> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.listOf().fieldOf("rotation").forGetter(list -> list.list)
            ).apply(instance, RotationCodec::new)
    );

    Random random = new Random();

    public List<Float> list;

    public RotationCodec(List<Float> list) {
        this.list = new ArrayList<>(list);
    }

    public RotationCodec() {
        list = new ArrayList<>(
                List.of(
                        random.nextFloat(-360,360),     //SOUTH
                        random.nextFloat(-360,360),   //NORTH
                        random.nextFloat(-360,360),    //EAST
                        random.nextFloat(-360,360),    //WEST
                        random.nextFloat(-360,360),    //UP
                        random.nextFloat(-360,360)    //DOWN
                )
        );
    }
}
