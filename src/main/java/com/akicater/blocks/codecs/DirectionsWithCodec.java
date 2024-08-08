package com.akicater.blocks.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class DirectionsWithCodec {
    public static final Codec<DirectionsWithCodec> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.listOf().fieldOf("direction").forGetter(x -> x.list)
            ).apply(instance, DirectionsWithCodec::new)
    );

    public List<Boolean> list = new ArrayList<>(6);



    public DirectionsWithCodec(List<Boolean> list) {
        this.list.addAll(list);
    }

    public DirectionsWithCodec() {
        this.list = new ArrayList<>(
                List.of(
                        false, // SOUTH
                        false, // NORTH
                        false, // WEST
                        false, // EAST
                        false, // UP
                        false  // DOWN
                )
        );
    }
}
