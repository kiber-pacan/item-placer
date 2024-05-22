package com.akicater;

import com.akicater.network.ItemPlacePayload;
import com.akicater.network.ItemRotatePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import static com.akicater.Itemplacer.MODID;

public class ItemplacerClient implements ClientModInitializer {

	private static final KeyBinding PLACE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Place item",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V,
			"item-placer"
	));

	public static final KeyBinding STOP_SCROLLING_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Rotate item",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_ALT,
			"item-placer"
	));
	public static final Identifier ITEMPLACE = new Identifier(MODID, "itemplace");
	public static final Identifier ITEMROTATE= new Identifier(MODID, "itemrotate");

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(Itemplacer.LAYING_ITEM_BLOCK_ENTITY, layingItemBER::new);

		PayloadTypeRegistry.playC2S().register(ItemPlacePayload.ID, ItemPlacePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ItemPlacePayload.ID, (payload, handler) ->
				payload.receive(handler.player(), payload.pos(), payload.hitResult())
		);

		PayloadTypeRegistry.playC2S().register(ItemRotatePayload.ID, ItemRotatePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ItemRotatePayload.ID, (payload, handler) ->
				payload.receive(handler.player(), payload.pos(), payload.degrees(), payload.hitResult())
		);


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (PLACE_KEY.wasPressed()) {
				if (client.crosshairTarget instanceof BlockHitResult && client.player.getStackInHand(Hand.MAIN_HAND) != ItemStack.EMPTY && MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).getBlock() != Blocks.AIR) {
					Direction side = ((BlockHitResult) client.crosshairTarget).getSide();
					BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
					ItemPlacePayload payload = new ItemPlacePayload(pos.offset(side,1), (BlockHitResult) client.crosshairTarget);
					ClientPlayNetworking.send(payload);
				}
			}
		});
	}
}