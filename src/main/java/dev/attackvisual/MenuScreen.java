package dev.attackvisual;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Module menu. Opens with Right Shift. */
public class MenuScreen extends Screen {
	private int x0, y0, idx;

	public MenuScreen() { super(Text.literal("Attack Visual")); }

	@Override
	protected void init() {
		x0 = width / 2 - 100;
		y0 = Math.max(24, height / 2 - 130);
		idx = 0;
		toggle("Full Bright", () -> Config.fullBright, v -> Config.fullBright = v);
		toggle("Hit Sound", () -> Config.hitSound, v -> Config.hitSound = v);
		toggle("Hit Particles", () -> Config.hitParticles, v -> Config.hitParticles = v);
		toggle("Hit Marker", () -> Config.hitMarker, v -> Config.hitMarker = v);
		toggle("Jump Circles", () -> Config.jumpCircles, v -> Config.jumpCircles = v);
		toggle("Keystrokes", () -> Config.keystrokes, v -> Config.keystrokes = v);
		toggle("Potion HUD", () -> Config.potions, v -> Config.potions = v);
		toggle("Armor HUD", () -> Config.armor, v -> Config.armor = v);
		toggle("Crosshair", () -> Config.crosshair, v -> Config.crosshair = v);
		toggle("Coords", () -> Config.infoPanel, v -> Config.infoPanel = v);
		toggle("Watermark", () -> Config.watermark, v -> Config.watermark = v);
		toggle("Target HUD", () -> Config.targetHud, v -> Config.targetHud = v);
		toggle("Target ESP", () -> Config.targetEsp, v -> Config.targetEsp = v);
		toggle("Screen Theme", () -> Config.screenTheme, v -> Config.screenTheme = v);

		int fy = y0 + ((idx + 1) / 2) * 24;
		addDrawableChild(ButtonWidget.builder(Text.literal("ESP mode: " + Esp.MODES[Config.espMode]), btn -> {
			Config.espMode = (Config.espMode + 1) % Esp.MODES.length;
			Config.save();
			btn.setMessage(Text.literal("ESP mode: " + Esp.MODES[Config.espMode]));
		}).dimensions(x0, fy + 72, 200, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD (drag)"), btn -> client.setScreen(new HudEditorScreen()))
			.dimensions(x0, fy + 48, 200, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Theme: " + Theme.current().label), btn -> {
			Theme.next();
			btn.setMessage(Text.literal("Theme: " + Theme.current().label));
		}).dimensions(x0, fy, 200, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Hit sound: " + HitSounds.NAMES[Config.soundIndex]), btn -> {
			Config.soundIndex = (Config.soundIndex + 1) % HitSounds.NAMES.length;
			Config.save();
			btn.setMessage(Text.literal("Hit sound: " + HitSounds.NAMES[Config.soundIndex]));
		}).dimensions(x0, fy + 24, 200, 20).build());
	}

	private void toggle(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
		int col = idx % 2, row = idx / 2;
		idx++;
		addDrawableChild(ButtonWidget.builder(label(name, get.get()), btn -> {
			set.accept(!get.get());
			Config.save();
			btn.setMessage(label(name, get.get()));
		}).dimensions(x0 + col * 102, y0 + row * 24, 98, 20).build());
	}

	private static Text label(String name, boolean on) {
		return Text.literal(name + ": " + (on ? "ON" : "OFF"));
	}

	@Override
	public void render(DrawContext ctx, int mx, int my, float delta) {
		super.render(ctx, mx, my, delta);
		ctx.drawCenteredTextWithShadow(textRenderer, "Attack Visual", width / 2, y0 - 14, Theme.current().accent);
	}
}
