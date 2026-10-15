package dev.ogfg;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class FogTst implements FabricClientGameTest {
	ClientGameTestContext c;
	TestSingleplayerContext s;

	void ck(boolean b, String x) {
		if (!b) throw new AssertionError(x);
		System.out.println("OGFG OK: " + x);
	}

	void cmd(String x) {
		c.runOnClient(m -> m.player.connection.sendCommand(x));
		c.waitTicks(4);
	}

	void shot(String n, boolean on, int mode, int st, int en, int sky, boolean dim) {
		c.runOnClient(m -> {
			Cfg.c = new Cfg();
			Cfg.c.on = on;
			Cfg.c.mode = mode;
			Cfg.c.st = st;
			Cfg.c.en = en;
			Cfg.c.sky = sky;
			Cfg.c.dim = dim;
			Cfg.fx();
		});
		c.waitTicks(12);
		c.takeScreenshot(n);
	}

	public void runTest(ClientGameTestContext ctx) {
		c = ctx;
		try (TestSingleplayerContext sp = ctx.worldBuilder().create()) {
			s = sp;
			ctx.waitTicks(40);
			ck(Cfg.c.on && Cfg.c.mode == 1 && Cfg.c.st == 55 && Cfg.c.en == 100, "defaults");
			Cfg.c.st = 90;
			Cfg.c.en = 20;
			Cfg.fx();
			ck(Cfg.c.en == 95, "end stays after start");
			Cfg.c = new Cfg();
			Cfg.c.col = 0x112233;
			Cfg.sv();
			Cfg.c = new Cfg();
			Cfg.ld();
			ck(Cfg.c.col == 0x112233, "config reloads from the json file");
			Cfg.c = new Cfg();
			Cfg.sv();
			ctx.runOnClient(m -> {
				m.options.renderDistance().set(6);
				m.options.cloudStatus().set(net.minecraft.client.CloudStatus.OFF);
			});
			s.getServer().runCommand("gamerule advance_time false");
			s.getServer().runCommand("time set 6000");
			s.getServer().runCommand("tp @p 0.5 -60 0.5 0 0");
			for (int x = -8; x <= 8; x += 4) {
				s.getServer().runCommand("fill " + x * 12 + " -60 60 " + x * 12 + " -50 60 stone");
				s.getServer().runCommand("fill " + x * 12 + " -60 90 " + x * 12 + " -50 90 stone");
			}
			ctx.waitTicks(60);
			shot("a_vanilla", false, 1, 55, 100, 100, true);
			shot("b_default", true, 1, 55, 100, 100, true);
			shot("c_tight", true, 1, 10, 50, 100, true);
			shot("d_vanilla_color", true, 0, 55, 100, 100, true);
			shot("e_sky_color", true, 2, 55, 100, 100, true);
			shot("f_sky30", true, 1, 55, 100, 30, true);
			s.getServer().runCommand("time set 18000");
			shot("g_night_dim", true, 1, 55, 100, 100, true);
			shot("h_night_nodim", true, 1, 55, 100, 100, false);
			s.getServer().runCommand("time set 6000");
			cmd("ogfog color ff0000");
			ck(Cfg.c.col == 0xff0000 && Cfg.c.mode == 1, "color command");
			cmd("ogfog start 20");
			ck(Cfg.c.st == 20, "start command");
			cmd("ogfog end 70");
			ck(Cfg.c.en == 70, "end command");
			cmd("ogfog off");
			ck(!Cfg.c.on, "off command");
			ctx.takeScreenshot("i_cmd_off");
			cmd("ogfog on");
			ctx.takeScreenshot("j_cmd_red");
			ctx.runOnClient(m -> Cfg.c = new Cfg());
			ctx.runOnClient(m -> m.setScreen(CfgScr.mk(null)));
			ctx.waitTicks(10);
			ck(ctx.computeOnClient(m -> m.screen.getClass().getSimpleName().equals("ClothConfigScreen")), "cloth config screen opens");
			ctx.takeScreenshot("k_cfg");
			ctx.runOnClient(m -> m.setScreen(null));
			Cfg.c = new Cfg();
			Cfg.sv();
		}
	}
}
