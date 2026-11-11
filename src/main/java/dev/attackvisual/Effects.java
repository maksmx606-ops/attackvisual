package dev.attackvisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;

/** Hit sound, hit particles, jump circles, full bright. */
public final class Effects {
	public static long hitTime = 0;
	public static Entity lastTarget = null;
	private static boolean wasOnGround = true;
	private static boolean fbActive = false;
	private static double savedGamma = 1.0;
	public static double bps = 0;
	private static double prevX, prevZ;
	private static boolean hasPrev = false;

	public static void onHit(Entity target) {
		MinecraftClient mc = MinecraftClient.getInstance();
		hitTime = System.currentTimeMillis();
		lastTarget = target;
		if (Config.hitSound) {
			mc.getSoundManager().play(PositionedSoundInstance.master(HitSounds.EVENTS[Config.soundIndex], 1.2f, 0.8f));
		}
		if (Config.hitParticles && mc.world != null) {
			DustParticleEffect dust = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.0f);
			for (int i = 0; i < 12; i++) {
				mc.particleManager.addParticle(dust,
					target.getX() + (Math.random() - 0.5) * 0.6,
					target.getBodyY(0.3 + Math.random() * 0.6),
					target.getZ() + (Math.random() - 0.5) * 0.6,
					(Math.random() - 0.5) * 0.2, Math.random() * 0.15, (Math.random() - 0.5) * 0.2);
			}
		}
	}

	public static void tick(MinecraftClient mc) {
		PlayerEntity p = mc.player;
		if (p == null || mc.world == null) return;

		// Speed (blocks per second, smoothed)
		if (hasPrev) bps = bps * 0.8 + Math.hypot(p.getX() - prevX, p.getZ() - prevZ) * 20 * 0.2;
		prevX = p.getX(); prevZ = p.getZ(); hasPrev = true;

		// Jump circle
		boolean onGround = p.isOnGround();
		if (Config.jumpCircles && wasOnGround && !onGround && p.getVelocity().y > 0.1) {
			DustParticleEffect dust = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.0f);
			for (int i = 0; i < 24; i++) {
				double a = Math.PI * 2 * i / 24;
				mc.particleManager.addParticle(dust, p.getX() + Math.cos(a) * 0.7, p.getY() + 0.05, p.getZ() + Math.sin(a) * 0.7, 0, 0, 0);
			}
		}
		wasOnGround = onGround;

		// Full bright (max gamma, restored when turned off)
		var gamma = mc.options.getGamma();
		if (Config.fullBright) {
			if (!fbActive) { savedGamma = gamma.getValue(); fbActive = true; }
			if (gamma.getValue() < 1.0) gamma.setValue(1.0);
		} else if (fbActive) {
			gamma.setValue(savedGamma);
			fbActive = false;
		}
	}
}
