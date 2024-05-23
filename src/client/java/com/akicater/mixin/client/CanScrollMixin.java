package com.akicater.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.akicater.Itemplacer.LAYING_ITEM;
import static com.akicater.ItemplacerClient.STOP_SCROLLING_KEY;

@Mixin(InGameHud.class)
public class CanScrollMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V"), method = "render")
	private void canScroll(DrawContext context, float tickDelta, CallbackInfo ci) {
		//if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult && MinecraftClient.getInstance().world.getBlockState(((BlockHitResult)MinecraftClient.getInstance().crosshairTarget).getBlockPos()).getBlock() == LAYING_ITEM) {
			context.drawTexture(new Identifier("minecraft:block/furnace_top"), 1,0,0,0, 10, 10);
		//}
	}
}