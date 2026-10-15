package dev.ogfg;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class OgFg implements ClientModInitializer {
	static final String[] MS = {"vanilla", "custom", "sky"};

	static int say(FabricClientCommandSource s, String m) {
		Cfg.sv();
		s.sendFeedback(Component.literal(m));
		return 1;
	}

	static LiteralArgumentBuilder<FabricClientCommandSource> num(String n, int lo, int hi) {
		return ClientCommandManager.literal(n).then(ClientCommandManager.argument("v", IntegerArgumentType.integer(lo, hi)).executes(x -> {
			int v = IntegerArgumentType.getInteger(x, "v");
			if (n.equals("start")) Cfg.c.st = v;
			else if (n.equals("end")) Cfg.c.en = v;
			else Cfg.c.sky = v;
			Cfg.fx();
			return say(x.getSource(), "fog " + n + " " + v + "%");
		}));
	}

	public void onInitializeClient() {
		Cfg.ld();
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("ogfog")
				.executes(x -> {
					Cfg c = Cfg.c;
					x.getSource().sendFeedback(Component.literal("og fog " + (c.on ? "on" : "off") + ", " + MS[c.mode] + " " + String.format("%06x", c.col) + ", start " + c.st + "%, end " + c.en + "%, sky " + c.sky + "%"));
					return 1;
				})
				.then(ClientCommandManager.literal("on").executes(x -> {
					Cfg.c.on = true;
					return say(x.getSource(), "og fog on");
				}))
				.then(ClientCommandManager.literal("off").executes(x -> {
					Cfg.c.on = false;
					return say(x.getSource(), "og fog off");
				}))
				.then(ClientCommandManager.literal("color").then(ClientCommandManager.argument("hex", StringArgumentType.word()).executes(x -> {
					String h = StringArgumentType.getString(x, "hex").replace("#", "");
					try {
						Cfg.c.col = Integer.parseInt(h, 16) & 0xffffff;
					} catch (Exception e) {
						x.getSource().sendFeedback(Component.literal("use a hex color like 3a1f5c"));
						return 0;
					}
					Cfg.c.mode = 1;
					return say(x.getSource(), "fog color " + String.format("%06x", Cfg.c.col));
				})))
				.then(ClientCommandManager.literal("mode").then(ClientCommandManager.argument("m", StringArgumentType.word()).suggests((x, b) -> {
					for (String s : MS) b.suggest(s);
					return b.buildFuture();
				}).executes(x -> {
					String m = StringArgumentType.getString(x, "m");
					for (int i = 0; i < 3; i++) {
						if (MS[i].equals(m)) {
							Cfg.c.mode = i;
							return say(x.getSource(), "fog color mode " + m);
						}
					}
					x.getSource().sendFeedback(Component.literal("use vanilla, custom or sky"));
					return 0;
				})))
				.then(num("start", 0, 95))
				.then(num("end", 5, 100))
				.then(num("sky", 10, 300))));
	}
}
