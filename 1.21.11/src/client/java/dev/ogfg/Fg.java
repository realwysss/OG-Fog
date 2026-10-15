package dev.ogfg;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;

public class Fg {
	public static boolean live;
	static float hh;
	static int sky;
	static boolean ow;

	public static void st(Camera cam, int rd, float pt, ClientLevel lvl) {
		ow = lvl.dimension() == Level.OVERWORLD;
		live = Cfg.c.on && cam.getFluidInCamera() == FogType.NONE && (ow || Cfg.c.dims);
		if (live && cam.entity() instanceof LivingEntity e && (e.hasEffect(MobEffects.BLINDNESS) || e.hasEffect(MobEffects.DARKNESS))) live = false;
		hh = rd * 16f;
		if (live && Cfg.c.mode == 2) sky = cam.attributeProbe().getValue(EnvironmentAttributes.SKY_COLOR, pt);
	}

	public static void col(Vector4f v) {
		int m = Cfg.c.mode;
		if (m == 0) return;
		int k = m == 1 ? Cfg.c.col : sky;
		float r = (k >> 16 & 255) / 255f, g = (k >> 8 & 255) / 255f, b = (k & 255) / 255f;
		if (Cfg.c.dim && ow) {
			float d = Math.max(0.12f, Math.min(1f, Math.max(v.x, Math.max(v.y, v.z)) / 0.75f));
			r *= d;
			g *= d;
			b *= d;
		}
		v.set(r, g, b, 1f);
	}

	public static float es(float x) {
		return hh * Cfg.c.st / 100f;
	}

	public static float ee(float x) {
		return Math.max(hh * Cfg.c.en / 100f, hh * Cfg.c.st / 100f + 1f);
	}

	public static float sk(float x) {
		return x * Cfg.c.sky / 100f;
	}
}
