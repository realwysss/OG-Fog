package dev.ogfg;

import java.util.Arrays;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CfgScr {
	static Component t(String s) {
		return Component.literal(s);
	}

	public static Screen mk(Screen p) {
		ConfigBuilder b = ConfigBuilder.create().setParentScreen(p).setTitle(t("OG Fog")).setSavingRunnable(Cfg::sv);
		ConfigCategory cc = b.getOrCreateCategory(t("Settings"));
		ConfigEntryBuilder e = b.entryBuilder();
		Cfg c = Cfg.c;
		cc.addEntry(e.startBooleanToggle(t("OG Fog On"), c.on).setDefaultValue(true).setTooltip(t("Turn this off to go back to the normal game fog.")).setSaveConsumer(v -> c.on = v).build());
		cc.addEntry(e.startIntSlider(t("Fog Start"), c.st, 0, 250).setDefaultValue(20).setTextGetter(v -> t(v + " blocks")).setTooltip(t("How many blocks away from you the fog starts. Everything closer is clear.")).setSaveConsumer(v -> c.st = v).build());
		cc.addEntry(e.startIntSlider(t("Fog End"), c.en, 2, 256).setDefaultValue(48).setTextGetter(v -> t(v + " blocks")).setTooltip(t("How many blocks away you stop seeing blocks. Everything further is only fog and sky. Try 40 to 50.")).setSaveConsumer(v -> c.en = v).build());
		String[] ms = {"Pack Sky", "Custom Color"};
		cc.addEntry(e.startSelector(t("Fog Color"), ms, ms[c.mode]).setDefaultValue(ms[0]).setTooltip(t("Pack Sky makes the fog take the colors of the sky behind it, so it blends in with a custom sky from a resource pack. Custom Color uses the color below.")).setSaveConsumer(v -> c.mode = Arrays.asList(ms).indexOf(v)).build());
		cc.addEntry(e.startColorField(t("Custom Color"), c.col).setDefaultValue(0x3a1f5c).setTooltip(t("The fog color when Fog Color is Custom Color.")).setSaveConsumer(v -> c.col = v & 0xffffff).build());
		cc.addEntry(e.startIntSlider(t("Fog Strength"), c.str, 0, 100).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("How thick the fog gets at the end. 100% hides everything behind it, lower keeps far blocks faintly visible.")).setSaveConsumer(v -> c.str = v).build());
		cc.addEntry(e.startBooleanToggle(t("Soft Fade"), c.soft).setDefaultValue(true).setTooltip(t("Fades the fog in smoothly. Turn it off for the straight line fade of the old game.")).setSaveConsumer(v -> c.soft = v).build());
		cc.addEntry(e.startBooleanToggle(t("Nether And End"), c.dims).setDefaultValue(false).setTooltip(t("Also use the fog in the Nether and the End. Off keeps their normal fog.")).setSaveConsumer(v -> c.dims = v).build());
		return b.build();
	}
}
