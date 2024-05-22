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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

	public static int dirToInt(Direction dir) {
		return switch (dir) {
			case SOUTH -> 0;
			case NORTH -> 1;
			case EAST -> 2;
			case WEST -> 3;
			case UP -> 4;
			case DOWN -> 5;
		};
	}

	public static Direction intToDir(int dir) {
		return switch (dir) {
			case 0 -> Direction.SOUTH;
			case 1 -> Direction.NORTH;
			case 2 -> Direction.EAST;
			case 3 -> Direction.WEST;
			case 4 -> Direction.UP;
			case 5 -> Direction.DOWN;
			default -> throw new IllegalStateException("Unexpected value: " + dir);
		};
	}

	static boolean contains(Vec3d vec, Box box) {
		return vec.x >= box.minX
				&& vec.x <= box.maxX
				&& vec.y >= box.minY
				&& vec.y <= box.maxY
				&& vec.z >= box.minZ
				&& vec.z <= box.maxZ;
	}

	public static int getDirection(BlockHitResult hit) {
		double xT = hit.getPos().getX();
		double yT = hit.getPos().getY();
		double zT = hit.getPos().getZ();

		double x = (xT > 0) ? xT - ((int)xT) : 1 - Math.abs(xT - ((int)xT));
		double y = (yT > 0) ? yT - ((int)yT) : 1 - Math.abs(yT - ((int)yT));
		double z = (zT > 0) ? zT - ((int)zT) : 1 - Math.abs(zT - ((int)zT));

		Vec3d pos = new Vec3d(x,y,z);
		List<Box> boxes = new ArrayList<>(
				List.of(
						new Box(0.125f, 0.125f, 0.875f, 0.875f, 0.875f, 1.0f),
						new Box(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.125f),
						new Box(0.875f, 0.125f, 0.125f, 1.0f, 0.875f, 0.875f),
						new Box(0.0f, 0.125f, 0.125f, 0.125f, 0.875f, 0.875f),
						new Box(0.125f, 0.875f, 0.125f, 0.875f, 1.0f, 0.875f),
						new Box(0.125f, 0.0f, 0.125f, 0.875f, 0.125f, 0.875f)
				)
		);
		for (int i = 0; i < boxes.size(); i++) {
			if (contains(pos, boxes.get(i))) {
				return i;
			}
		}
		LOGGER.warn("Somehow you got error? damn... Maybe my mod is fucking garbage? (item-placer)");
		return 0;
	}
}