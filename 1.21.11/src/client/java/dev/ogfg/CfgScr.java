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
		String[] ms = {"Vanilla", "Custom Color", "Sky Color"};
		cc.addEntry(e.startSelector(t("Color Mode"), ms, ms[c.mode]).setDefaultValue(ms[1]).setTooltip(t("Vanilla keeps the normal fog color. Custom Color uses the color below, so the far edge of the world can match your pack. Sky Color uses the normal sky color.")).setSaveConsumer(v -> c.mode = Arrays.asList(ms).indexOf(v)).build());
		cc.addEntry(e.startColorField(t("Fog Color"), c.col).setDefaultValue(0x3a1f5c).setTooltip(t("The fog color in Custom Color mode. This is also the color you see where chunks are not loaded yet.")).setSaveConsumer(v -> c.col = v & 0xffffff).build());
		cc.addEntry(e.startBooleanToggle(t("Dim At Night"), c.dim).setDefaultValue(true).setTooltip(t("Makes the fog color darker at night and in storms, like the normal fog does. Turn it off for the same color all day.")).setSaveConsumer(v -> c.dim = v).build());
		cc.addEntry(e.startIntSlider(t("Fog Start"), c.st, 0, 95).setDefaultValue(55).setTextGetter(v -> t(v + "%")).setTooltip(t("Where the fog begins, as a percent of your render distance. 0% starts right at you, 95% only fogs the very end.")).setSaveConsumer(v -> c.st = v).build());
		cc.addEntry(e.startIntSlider(t("Fog End"), c.en, 5, 100).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("Where the fog is fully thick, as a percent of your render distance. Lower makes a closer wall of fog. 100% hides the edge of the loaded world.")).setSaveConsumer(v -> c.en = v).build());
		cc.addEntry(e.startIntSlider(t("Sky Fog"), c.sky, 10, 300).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("How far the fog reaches into the sky near the horizon. Lower fogs more of the sky, higher keeps more of your sky clear. 100% is the normal amount.")).setSaveConsumer(v -> c.sky = v).build());
		cc.addEntry(e.startBooleanToggle(t("Nether And End"), c.dims).setDefaultValue(false).setTooltip(t("Also use these settings in the Nether and the End. Off keeps their normal fog.")).setSaveConsumer(v -> c.dims = v).build());
		return b.build();
	}
}
