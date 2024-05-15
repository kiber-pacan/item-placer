package com.akicater;

import com.akicater.network.ItemPlacePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import static com.akicater.Itemplacer.MODID;

public class ItemplacerClient implements ClientModInitializer {

	private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Place item", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_V, // The keycode of the key
			"item-placer" // The translation key of the keybinding's category.
	));
	public static final Identifier ITEMPLACE = new Identifier(MODID, "itemplace");

	@Override
	public void onInitializeClient() {
		ServerPlayNetworking.registerGlobalReceiver(ITEMPLACE, ItemPlacePacket::receive);
		BlockEntityRendererFactories.register(Itemplacer.LAYING_ITEM_BLOCK_ENTITY, layingItemBER::new);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keyBinding.wasPressed()) {
				if (client.crosshairTarget instanceof BlockHitResult) {
					PacketByteBuf buf = PacketByteBufs.create();
					Direction side = ((BlockHitResult) client.crosshairTarget).getSide();
					BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
					buf.writeBlockPos(pos.offset(side, 1));
					buf.writeBlockHitResult((BlockHitResult) client.crosshairTarget);
					ClientPlayNetworking.send(ITEMPLACE, buf);
				}
			}
		});
	}
}