
package com.akicater;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import static com.akicater.ItemPlacer.MODID;

@Config(name = MODID)
public class ItemPlacerConfig implements ConfigData {
    public float absoluteSize = 1.0f;
    public float tempItemSize = 1.0f;
    public float tempBlockSize = 1.0f;
    public boolean oldRendering = false;
}
