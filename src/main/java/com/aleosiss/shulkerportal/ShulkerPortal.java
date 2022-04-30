package com.aleosiss.shulkerportal;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import com.aleosiss.shulkerportal.command.ShulkerPortalCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPortal implements ModInitializer, CarpetExtension {
	public static final Logger logger = LoggerFactory.getLogger(ShulkerPortal.class);

	public static void mixin() { return; }

	static {
		CarpetServer.manageExtension(new ShulkerPortal());
	}

	@Override
	public void onGameStarted()
	{
		CarpetServer.settingsManager.parseSettingsClass(ShulkerPortalCarpetSettings.class);
	}

	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		ShulkerPortalCommand.register(dispatcher);
	}

	@Override
	public void onInitialize() {}
}
