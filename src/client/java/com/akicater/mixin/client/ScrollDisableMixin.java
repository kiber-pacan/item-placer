package com.akicater.mixin.client;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.akicater.ItemplacerClient.STOP_SCROLLING_KEY;

@Mixin(PlayerInventory.class)
public class ScrollDisableMixin {

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", cancellable = true)
	private void disableScrolling(double scrollAmount, CallbackInfo callbackInfo) {
		if (STOP_SCROLLING_KEY.isPressed()) {
			callbackInfo.cancel();
		}
	}
}