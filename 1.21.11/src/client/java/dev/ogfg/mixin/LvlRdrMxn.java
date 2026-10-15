package dev.ogfg.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.ogfg.Fg;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public abstract class LvlRdrMxn {
	@Inject(method = "prepareChunkRenders", at = @At("HEAD"))
	private void a(Matrix4fc m, double x, double y, double z, CallbackInfoReturnable<?> ci) {
		Fg.cap();
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void b(GraphicsResourceAllocator a, DeltaTracker d, boolean b, Camera cam, Matrix4f rot, Matrix4f proj, Matrix4f cull, GpuBufferSlice f, Vector4f fog, boolean sky, CallbackInfo ci) {
		Fg.run(rot, proj);
	}
}
