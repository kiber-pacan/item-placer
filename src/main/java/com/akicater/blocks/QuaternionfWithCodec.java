package com.akicater.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Quaternionf;

public class QuaternionfWithCodec extends Quaternionf {

    public static final Codec<QuaternionfWithCodec> CODEC1 = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.QUATERNIONF.fieldOf("quaternion").forGetter(quaternionf -> {
                        return quaternionf;
                    })
            ).apply(instance, QuaternionfWithCodec::new));

    public QuaternionfWithCodec(Quaternionf quaternionf) {
        this.set(quaternionf);
    }
    public QuaternionfWithCodec() {
        super();
    }
}
