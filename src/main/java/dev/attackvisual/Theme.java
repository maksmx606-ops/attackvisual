package dev.attackvisual;

/** Color themes. Accent is ARGB. */
public enum Theme {
	OCEAN("Ocean", 0xFF3BA7FF),
	VIOLET("Violet", 0xFFA066FF),
	CRIMSON("Crimson", 0xFFFF4D5E),
	EMERALD("Emerald", 0xFF34D399),
	AMBER("Amber", 0xFFFFB020);

	public final String label;
	public final int accent;

	Theme(String label, int accent) { this.label = label; this.accent = accent; }

	public static Theme current() { return values()[Config.themeIndex]; }

	public static void next() {
		Config.themeIndex = (Config.themeIndex + 1) % values().length;
		Config.save();
	}

	public static int withAlpha(int argb, int alpha) { return (alpha << 24) | (argb & 0xFFFFFF); }
}
