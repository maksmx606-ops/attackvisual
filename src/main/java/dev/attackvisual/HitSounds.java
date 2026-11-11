package dev.attackvisual;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public final class HitSounds {
	public static final String[] NAMES = {"Orb", "Arrow", "Crit", "Chime"};
	public static final SoundEvent[] EVENTS = {
		SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
		SoundEvents.ENTITY_ARROW_HIT_PLAYER,
		SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
		SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME
	};
}
