package com.aleosiss.shulkerportal.command;


import carpet.settings.SettingsManager;
import carpet.utils.Messenger;
import com.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import com.aleosiss.shulkerportal.service.ShulkerPortalService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static com.aleosiss.shulkerportal.service.ShulkerPortalService.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ShulkerPortalCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("ale")
				.requires((player) -> SettingsManager.canUseCommand(player, ShulkerPortalCarpetSettings.commandShulker))
				.then(makeGetCommand())
				.then(makeClearCommand())
				.then(makeAddCommand())
		;

		dispatcher.register(argumentBuilder);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> makeAddCommand() {
		return literal("add")
				.then(literal("bad_shulker_teleport").executes(ShulkerPortalCommand::addDebugBadShulkerTeleport))
		;
	}

	private static int addDebugBadShulkerTeleport(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
		ShulkerPortalService.getInstance().getBadTeleports().add(new Teleport(1, new Vec3d(0, 0, 0), new Vec3d(0, 0, 0)));
		return 1;
	}

	private static ArgumentBuilder<ServerCommandSource, ?> makeClearCommand() {
		return literal("clear")
				.then(literal("bad_shulker_teleports").executes(ShulkerPortalCommand::clearBadShulkerTeleports))
				.then(literal("errors").executes(ShulkerPortalCommand::clearErrors))
		;
	}

	private static int clearErrors(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
		ShulkerPortalService.getInstance().getErrorMessages().clear();
		return 1;
	}

	private static int clearBadShulkerTeleports(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
		ShulkerPortalService.getInstance().getBadTeleports().clear();
		return 1;
	}

	private static ArgumentBuilder<ServerCommandSource, ?> makeGetCommand() {
		return literal("get")
				.then(literal("bad_shulker_teleports").executes(ShulkerPortalCommand::getBadShulkerTeleports))
				.then(literal("all_shulker_teleports").executes(ShulkerPortalCommand::getAllShulkerTeleports))
				.then(literal("errors")               .executes(ShulkerPortalCommand::getErrors))
		;
	}

	private static int getErrors(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) throws CommandSyntaxException {
		ServerPlayerEntity player = serverCommandSourceCommandContext.getSource().getPlayer();
		if(ShulkerPortalService.getInstance().getErrorMessages().size() < 1) {
			Messenger.m(player, "l No errors logged.");
		} else {
			ShulkerPortalService.getInstance().getErrorMessages().forEach(str -> Messenger.m(player, "w " + str));
		}
		return 1;
	}

	private static int getAllShulkerTeleports(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(ShulkerPortalService.getInstance().getTeleports().size() < 1) {
			Messenger.m(player, "w No teleports yet!");
			return 1;
		}

		Messenger.m(player, "w All teleports:");
		ShulkerPortalService.getInstance().getTeleports().forEach(tp -> Messenger.m(player,
				String.format("w Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id(), tp.netherPos(), tp.overworldPos())
		));
		return 1;
	}


	private static int listSettings(ServerCommandSource source)
	{
		return 1;
	}

	private static int getBadShulkerTeleports(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(ShulkerPortalService.getInstance().getBadTeleports().size() < 1) {
			Messenger.m(player, "l No bad teleports yet!");
			return 1;
		}

		Messenger.m(player, "w All bad teleports:");
		ShulkerPortalService.getInstance().getBadTeleports().forEach(tp -> Messenger.m(player,
				String.format("w Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id(), tp.netherPos(), tp.overworldPos())
		));
		return 1;
	}
}
