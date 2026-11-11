package dev.attackvisual;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttackHud implements HudElement {
	private static final int PANEL = 0xE0101018;

	// ---- drawing helpers: rounded plate with a soft glow outline ----
	private static void rrect(DrawContext c, int x, int y, int w, int h, int col) {
		c.fill(x + 2, y, x + w - 2, y + h, col);
		c.fill(x, y + 2, x + w, y + h - 2, col);
		c.fill(x + 1, y + 1, x + w - 1, y + h - 1, col);
	}

	static void plate(DrawContext c, int x, int y, int w, int h, int accent) {
		for (int i = 3; i >= 1; i--) rrect(c, x - i, y - i, w + 2 * i, h + 2 * i, Theme.withAlpha(accent, 0x10 * (4 - i)));
		rrect(c, x - 1, y - 1, w + 2, h + 2, Theme.withAlpha(accent, 0xE0));
		rrect(c, x, y, w, h, PANEL);
	}

	@Override
	public void render(DrawContext ctx, RenderTickCounter tick) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null || mc.options.hudHidden) return;

		Theme t = Theme.current();
		int w = ctx.getScaledWindowWidth();
		int h = ctx.getScaledWindowHeight();
		int cx = w / 2, cy = h / 2;

		if (Config.crosshair) ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, t.accent);

		if (Config.hitMarker && System.currentTimeMillis() - Effects.hitTime < 200) {
			for (int i = 3; i <= 6; i++) {
				ctx.fill(cx + i, cy + i, cx + i + 1, cy + i + 1, 0xFFFFFFFF);
				ctx.fill(cx - i - 1, cy + i, cx - i, cy + i + 1, 0xFFFFFFFF);
				ctx.fill(cx + i, cy - i - 1, cx + i + 1, cy - i, 0xFFFFFFFF);
				ctx.fill(cx - i - 1, cy - i - 1, cx - i, cy - i, 0xFFFFFFFF);
			}
		}

		float hp = Math.max(0f, Math.min(1f, mc.player.getHealth() / mc.player.getMaxHealth()));
		int x0 = cx - 91, y0 = h - 26;
		ctx.fill(x0, y0, x0 + 182, y0 + 2, Theme.withAlpha(t.accent, 0x40));
		ctx.fill(x0, y0, x0 + (int) (182 * hp), y0 + 2, hp > 0.3f ? t.accent : 0xFFFF3B3B);

		if (Config.watermark) watermark(ctx, mc, t, w, h);
		if (Config.infoPanel) coords(ctx, mc, t, w, h);
		if (Config.keystrokes) drawKeys(ctx, mc, t, w, h);
		if (Config.potions) drawPotions(ctx, mc, t, w, h);
		if (Config.targetHud) target(ctx, mc, t, w, h);
		if (Config.armor) drawArmor(ctx, mc, t, w, h);
	}

	private void watermark(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.WATERMARK;
		int ping = 0;
		var nh = mc.getNetworkHandler();
		if (nh != null) {
			var entry = nh.getPlayerListEntry(mc.player.getUuid());
			if (entry != null) ping = entry.getLatency();
		}
		String name = "Attack Visual";
		String rest = "  \u2022  " + mc.player.getName().getString()
			+ "  \u2022  " + mc.getCurrentFps() + " FPS"
			+ "  \u2022  " + ping + " ms"
			+ "  \u2022  " + String.format("%.1f", Effects.bps) + " BPS"
			+ "  \u2022  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		int nw = mc.textRenderer.getWidth(name);
		el.w = nw + mc.textRenderer.getWidth(rest) + 12; el.h = 16;
		int x = el.px(sw), y = el.py(sh);
		plate(ctx, x, y, el.w, el.h, t.accent);
		ctx.drawTextWithShadow(mc.textRenderer, name, x + 6, y + 4, t.accent);
		ctx.drawTextWithShadow(mc.textRenderer, rest, x + 6 + nw, y + 4, 0xFFFFFFFF);
	}

	private void coords(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.COORDS;
		String s = "XYZ  " + (int) mc.player.getX() + "  " + (int) mc.player.getY() + "  " + (int) mc.player.getZ();
		el.w = mc.textRenderer.getWidth(s) + 12; el.h = 14;
		int x = el.px(sw), y = el.py(sh);
		plate(ctx, x, y, el.w, el.h, t.accent);
		ctx.drawTextWithShadow(mc.textRenderer, s, x + 6, y + 3, 0xFFFFFFFF);
	}

	private void key(DrawContext ctx, MinecraftClient mc, Theme t, int x, int y, int bw, String s, boolean down) {
		ctx.fill(x, y, x + bw, y + 18, down ? Theme.withAlpha(t.accent, 0xC0) : 0x80000000);
		ctx.drawCenteredTextWithShadow(mc.textRenderer, s, x + bw / 2, y + 5, 0xFFFFFFFF);
	}

	private void drawKeys(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.KEYS;
		int x = el.px(sw), y = el.py(sh);
		var o = mc.options;
		key(ctx, mc, t, x + 20, y, 18, "W", o.forwardKey.isPressed());
		key(ctx, mc, t, x, y + 20, 18, "A", o.leftKey.isPressed());
		key(ctx, mc, t, x + 20, y + 20, 18, "S", o.backKey.isPressed());
		key(ctx, mc, t, x + 40, y + 20, 18, "D", o.rightKey.isPressed());
		key(ctx, mc, t, x, y + 40, 28, "LMB", o.attackKey.isPressed());
		key(ctx, mc, t, x + 30, y + 40, 28, "RMB", o.useKey.isPressed());
	}

	private void drawPotions(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.POTIONS;
		int x = el.px(sw), y = el.py(sh), startY = y, maxW = 0, n = 0;
		for (StatusEffectInstance e : mc.player.getStatusEffects()) {
			if (n++ >= 6) break;
			String name = e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1);
			int s = e.getDuration() / 20;
			String time = e.isInfinite() ? "inf" : (s / 60) + ":" + String.format("%02d", s % 60);
			int pw = Math.max(mc.textRenderer.getWidth(name), mc.textRenderer.getWidth(time)) + 26;
			maxW = Math.max(maxW, pw);
			plate(ctx, x, y, pw, 22, t.accent);
			int col = 0xFF000000 | e.getEffectType().value().getColor();
			ctx.fill(x + 4, y + 5, x + 16, y + 17, col);
			ctx.drawTextWithShadow(mc.textRenderer, name, x + 21, y + 3, 0xFFFFFFFF);
			ctx.drawTextWithShadow(mc.textRenderer, time, x + 21, y + 12, 0xFFAAAAAA);
			y += 27;
		}
		if (n == 0) { el.w = 90; el.h = 22; } else { el.w = maxW; el.h = y - startY - 5; }
	}

	private void target(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.TARGET;
		Entity e = Effects.lastTarget;
		boolean live = e instanceof LivingEntity && e.isAlive() && System.currentTimeMillis() - Effects.hitTime < 4000;
		boolean preview = mc.currentScreen instanceof HudEditorScreen;
		if (!live && !preview) return;

		String name = live ? e.getName().getString() : "Target";
		float hp = 1f; int hpInt = 20;
		if (live) {
			LivingEntity le = (LivingEntity) e;
			hp = Math.max(0f, Math.min(1f, le.getHealth() / le.getMaxHealth()));
			hpInt = (int) Math.ceil(le.getHealth());
		}
		el.w = Math.max(110, mc.textRenderer.getWidth(name) + 36); el.h = 30;
		int x = el.px(sw), y = el.py(sh);
		plate(ctx, x, y, el.w, el.h, t.accent);
		ctx.fill(x + 4, y + 5, x + 24, y + 25, Theme.withAlpha(t.accent, 0x90));
		ctx.drawCenteredTextWithShadow(mc.textRenderer, name.substring(0, 1).toUpperCase(), x + 14, y + 11, 0xFFFFFFFF);
		ctx.drawTextWithShadow(mc.textRenderer, name, x + 29, y + 4, 0xFFFFFFFF);
		ctx.drawTextWithShadow(mc.textRenderer, "HP: " + hpInt, x + 29, y + 13, 0xFFAAAAAA);
		int bw = el.w - 35;
		ctx.fill(x + 29, y + 24, x + 29 + bw, y + 27, 0x80000000);
		ctx.fill(x + 29, y + 24, x + 29 + (int) (bw * hp), y + 27, hp > 0.3f ? t.accent : 0xFFFF3B3B);
	}

	private void drawArmor(DrawContext ctx, MinecraftClient mc, Theme t, int sw, int sh) {
		HudLayout.El el = HudLayout.ARMOR;
		int x = el.px(sw), y = el.py(sh);
		EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		for (EquipmentSlot s : slots) {
			ItemStack st = mc.player.getEquippedStack(s);
			if (!st.isEmpty()) {
				ctx.drawItem(st, x, y);
				if (st.isDamageable()) {
					float r = 1f - (float) st.getDamage() / st.getMaxDamage();
					ctx.fill(x, y + 17, x + 16, y + 19, 0x80000000);
					ctx.fill(x, y + 17, x + (int) (16 * r), y + 19, r > 0.3f ? t.accent : 0xFFFF3B3B);
				}
			}
			y += 20;
		}
	}

	/** Theme tint + frame over every screen. */
	public static void drawScreenFrame(DrawContext ctx, int w, int h) {
		if (!Config.screenTheme) return;
		int a = Theme.current().accent;
		ctx.fill(0, 0, w, h, Theme.withAlpha(a, 0x14));
		int line = Theme.withAlpha(a, 0xB0);
		ctx.fill(0, 0, w, 2, line);
		ctx.fill(0, h - 2, w, h, line);
		ctx.fill(0, 0, 2, h, line);
		ctx.fill(w - 2, 0, w, h, line);
	}
}
