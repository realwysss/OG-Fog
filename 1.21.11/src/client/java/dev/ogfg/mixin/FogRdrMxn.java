package dev.ogfg.mixin;

import dev.ogfg.Fg;
import java.nio.ByteBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRdrMxn {
	@Inject(method = "setupFog", at = @At("HEAD"))
	private void a(Camera cam, int rd, DeltaTracker dt, float f, ClientLevel lvl, CallbackInfoReturnable<Vector4f> ci) {
		Fg.st(cam, rd, dt.getGameTimeDeltaPartialTick(false), lvl);
	}

	@Inject(method = "setupFog", at = @At("RETURN"))
	private void b(Camera cam, int rd, DeltaTracker dt, float f, ClientLevel lvl, CallbackInfoReturnable<Vector4f> ci) {
		Fg.live = false;
	}

	@Inject(method = "updateBuffer", at = @At("HEAD"))
	private void c(ByteBuffer bb, int i, Vector4f v, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		if (Fg.live) Fg.col(v);
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float d(float x) {
		return Fg.live ? Fg.es(x) : x;
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private float e(float x) {
		return Fg.live ? Fg.ee(x) : x;
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 4)
	private float f(float x) {
		return Fg.live ? Fg.sk(x) : x;
	}
}
