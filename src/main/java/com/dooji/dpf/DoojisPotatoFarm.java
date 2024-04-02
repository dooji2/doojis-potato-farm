package com.dooji.dpf;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class DoojisPotatoFarm implements ModInitializer {

	private final Set<ServerPlayerEntity> initializedPlayers = new HashSet<>();

	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(this::onServerStart);
		ServerTickCallback.EVENT.register(this::onServerTick);
	}

	private void onServerStart(MinecraftServer server) {
		server.getPlayerManager().getPlayerList().forEach(this::startActionsForPlayer);
	}

	private void onServerTick(MinecraftServer server) {
		server.getPlayerManager().getPlayerList().forEach(player -> {
			if (!initializedPlayers.contains(player)) {
				startActionsForPlayer(player);
			}
		});
	}

	private void startActionsForPlayer(ServerPlayerEntity player) {
		PotatoActions potatoActions = new PotatoActions(player);
		potatoActions.startActions();
		initializedPlayers.add(player);
	}
}
