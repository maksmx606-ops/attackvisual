package dev.attackvisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;

/** Target ESP: particle effects around the entity you hit (4 seconds). Not visible through walls. */
public final class Esp {
	public static final String[] MODES = {"Ghosts", "Orbs", "Crystals", "Money"};

	private static void dust(MinecraftClient mc, int rgb, float size, double x, double y, double z) {
		mc.particleManager.addParticle(new DustParticleEffect(rgb, size), x, y, z, 0, 0, 0);
	}

	public static void tick(MinecraftClient mc) {
		Entity e = Effects.lastTarget;
		if (!Config.targetEsp || e == null || mc.world == null || !e.isAlive()) return;
		if (System.currentTimeMillis() - Effects.hitTime > 4000) return;

		double t = mc.world.getTime() * 0.25;
		double x = e.getX(), y = e.getY(), z = e.getZ(), hgt = e.getHeight();
		int rgb = Theme.current().accent & 0xFFFFFF;

		switch (Config.espMode) {
			case 0 -> { // Ghosts: soft glowing wisps circling the target
				for (int i = 0; i < 3; i++) {
					double a = t + i * 2.094;
					double yy = y + hgt * 0.5 + Math.sin(t * 1.3 + i * 2) * hgt * 0.35;
					dust(mc, rgb, 1.3f, x + Math.cos(a) * 0.75, yy, z + Math.sin(a) * 0.75);
				}
			}
			case 1 -> { // Orbs: three big orbs orbiting at different heights
				for (int i = 0; i < 3; i++) {
					double a = t * 1.6 + i * 2.094;
					dust(mc, rgb, 1.8f, x + Math.cos(a) * 0.9, y + hgt * (0.25 + 0.25 * i), z + Math.sin(a) * 0.9);
				}
			}
			case 2 -> { // Crystals: rotating diamond shape
				double top = y + hgt + 0.35, mid = y + hgt * 0.55, bot = y - 0.1;
				dust(mc, rgb, 1.2f, x, top, z);
				dust(mc, rgb, 1.2f, x, bot, z);
				for (int i = 0; i < 4; i++) {
					double a = t * 0.8 + i * Math.PI / 2;
					double vx = x + Math.cos(a) * 0.55, vz = z + Math.sin(a) * 0.55;
					dust(mc, rgb, 1.2f, vx, mid, vz);
					dust(mc, 0xFFFFFF, 0.7f, (vx + x) / 2, (mid + top) / 2, (vz + z) / 2);
					dust(mc, 0xFFFFFF, 0.7f, (vx + x) / 2, (mid + bot) / 2, (vz + z) / 2);
				}
			}
			default -> { // Money: green sparkles spiraling up
				for (int i = 0; i < 2; i++) {
					double a = t * 0.9 + i * Math.PI;
					double yy = y + ((mc.world.getTime() + i * 10) % 20) / 20.0 * hgt;
					mc.particleManager.addParticle(ParticleTypes.HAPPY_VILLAGER, x + Math.cos(a) * 0.6, yy, z + Math.sin(a) * 0.6, 0, 0.02, 0);
					dust(mc, 0x34D399, 1.0f, x + Math.cos(a + 1) * 0.5, yy, z + Math.sin(a + 1) * 0.5);
				}
			}
		}
	}
}
