package dev.attackvisual;

import java.util.Properties;

/** Draggable HUD elements. Positions are stored as fractions of the screen. */
public final class HudLayout {
	public static final class El {
		public final String id, label;
		public final float dx, dy;
		public float fx, fy;
		public int w, h;

		El(String id, String label, float dx, float dy, int w, int h) {
			this.id = id; this.label = label; this.dx = dx; this.dy = dy; this.fx = dx; this.fy = dy; this.w = w; this.h = h;
		}
		public int px(int sw) { return Math.max(0, Math.min(sw - w, Math.round(fx * sw))); }
		public int py(int sh) { return Math.max(0, Math.min(sh - h, Math.round(fy * sh))); }
		public void setPixel(int x, int y, int sw, int sh) { fx = (float) x / sw; fy = (float) y / sh; }
		public void reset() { fx = dx; fy = dy; }
	}

	public static final El WATERMARK = new El("watermark", "Watermark", 0.01f, 0.01f, 170, 16);
	public static final El COORDS = new El("coords", "Coords", 0.01f, 0.09f, 120, 14);
	public static final El KEYS = new El("keys", "Keystrokes", 0.01f, 0.70f, 58, 58);
	public static final El POTIONS = new El("potions", "Potions", 0.01f, 0.18f, 100, 22);
	public static final El ARMOR = new El("armor", "Armor", 0.95f, 0.55f, 16, 80);
	public static final El TARGET = new El("target", "Target HUD", 0.56f, 0.62f, 110, 30);
	public static final El[] ALL = {WATERMARK, COORDS, KEYS, POTIONS, ARMOR, TARGET};

	public static void resetAll() { for (El e : ALL) e.reset(); }

	public static void load(Properties p) {
		for (El e : ALL) {
			e.fx = f(p, e.id + ".x", e.dx);
			e.fy = f(p, e.id + ".y", e.dy);
		}
	}

	public static void save(Properties p) {
		for (El e : ALL) {
			p.setProperty(e.id + ".x", String.valueOf(e.fx));
			p.setProperty(e.id + ".y", String.valueOf(e.fy));
		}
	}

	private static float f(Properties p, String k, float d) {
		try { return Float.parseFloat(p.getProperty(k, String.valueOf(d))); } catch (Exception e) { return d; }
	}
}
