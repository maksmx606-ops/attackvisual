package dev.attackvisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AttackVisual implements ClientModInitializer {
	public static final String ID = "attackvisual";
	private boolean wasDown;

	@Override
	public void onInitializeClient() {
		Config.load();

		HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, Identifier.of(ID, "hud"), new AttackHud());

		ScreenEvents.AFTER_INIT.register((client, screen, w, h) ->
			ScreenEvents.afterRender(screen).register((s, ctx, mx, my, delta) ->
				AttackHud.drawScreenFrame(ctx, s.width, s.height)));

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (world.isClient() && entity instanceof LivingEntity) Effects.onHit(entity);
			return ActionResult.PASS;
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			Effects.tick(client);
			Esp.tick(client);
			if (client.getWindow() == null) return;
			boolean down = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
			if (down && !wasDown && client.currentScreen == null) client.setScreen(new MenuScreen());
			wasDown = down;
		});
	}
}
