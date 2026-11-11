package dev.attackvisual;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Drag HUD elements with the left mouse button. */
public class HudEditorScreen extends Screen {
	private HudLayout.El drag;
	private int offX, offY;
	private boolean wasDown;

	public HudEditorScreen() { super(Text.literal("Edit HUD")); }

	@Override
	protected void init() {
		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
			.dimensions(width / 2 - 102, height - 28, 100, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> { HudLayout.resetAll(); Config.save(); })
			.dimensions(width / 2 + 2, height - 28, 100, 20).build());
	}

	@Override
	public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
		// no blur/darkening so the real HUD stays visible
	}

	@Override
	public void render(DrawContext ctx, int mx, int my, float delta) {
		super.render(ctx, mx, my, delta);

		boolean down = client != null && GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		if (down && !wasDown) {
			drag = null;
			for (int i = HudLayout.ALL.length - 1; i >= 0; i--) {
				HudLayout.El e = HudLayout.ALL[i];
				int x = e.px(width), y = e.py(height);
				if (mx >= x && mx <= x + e.w && my >= y && my <= y + e.h) { drag = e; offX = mx - x; offY = my - y; break; }
			}
		}
		if (down && drag != null) {
			int nx = Math.max(0, Math.min(width - drag.w, mx - offX));
			int ny = Math.max(0, Math.min(height - drag.h, my - offY));
			drag.setPixel(nx, ny, width, height);
		}
		if (!down && wasDown && drag != null) { Config.save(); drag = null; }
		wasDown = down;

		int accent = Theme.current().accent;
		for (HudLayout.El e : HudLayout.ALL) {
			int x = e.px(width), y = e.py(height);
			int c = e == drag ? accent : Theme.withAlpha(accent, 0xA0);
			ctx.fill(x - 1, y - 1, x + e.w + 1, y, c);
			ctx.fill(x - 1, y + e.h, x + e.w + 1, y + e.h + 1, c);
			ctx.fill(x - 1, y, x, y + e.h, c);
			ctx.fill(x + e.w, y, x + e.w + 1, y + e.h, c);
			ctx.drawTextWithShadow(textRenderer, e.label, x, y - 10, 0xFFFFFFFF);
		}
		ctx.drawCenteredTextWithShadow(textRenderer, "Drag elements with the mouse", width / 2, 10, 0xFFFFFFFF);
	}
}
