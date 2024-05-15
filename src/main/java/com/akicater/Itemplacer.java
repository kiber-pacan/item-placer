package com.akicater;

import com.akicater.blocks.layingItem;
import com.akicater.blocks.layingItemBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Itemplacer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("item-placer");
	public static final String MODID = "item-placer";

	public static final layingItem LAYING_ITEM = new layingItem(Block.Settings.create().breakInstantly().nonOpaque().noBlockBreakParticles().pistonBehavior(PistonBehavior.DESTROY));

	public static final BlockEntityType<layingItemBlockEntity> LAYING_ITEM_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier(MODID, "laying_item_block_entity"),
			BlockEntityType.Builder.create(layingItemBlockEntity::new, LAYING_ITEM).build(null)
	);

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, new Identifier(MODID, "laying_item"), LAYING_ITEM);
	}
}