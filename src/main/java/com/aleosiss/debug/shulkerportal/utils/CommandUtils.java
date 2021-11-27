package com.aleosiss.debug.shulkerportal.utils;

import com.aleosiss.debug.shulkerportal.ShulkerPortal;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class CommandUtils {
	private static CommandUtils SINGLETON;

	private CommandUtils() {}
	public static CommandUtils getInstance() {
		if(SINGLETON == null) {
			SINGLETON = new CommandUtils();
		}

		return SINGLETON;
	}

	public void registerClientCommands() {
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("er_testdraw")
				.then(ClientCommandManager.argument("text", StringArgumentType.string()).executes(CommandUtils.this::client))
		);
	}

	private int client(CommandContext<FabricClientCommandSource> commandContext) {
		return 1;
	}

	public void registerServerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("er_test").executes(CommandUtils.this::test)));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("ale_get_bad_shulker_teleports").executes(CommandUtils.this::getBadShulkerTeleports)));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("ale_get_shulker_teleports").executes(CommandUtils.this::getAllShulkerTeleports)));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("ale_clear_bad_shulker_teleports").executes(CommandUtils.this::clearBadShulkerTeleports)));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("ale_add_bad_shulker_teleport").executes(CommandUtils.this::addBadShulkerTeleport)));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("ale_print_errors").executes(CommandUtils.this::getErrors)));
	}

	private int getErrors(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) throws CommandSyntaxException {
		ServerPlayerEntity player = serverCommandSourceCommandContext.getSource().getPlayer();
		ShulkerPortal.errorMessages.forEach(str -> sendMessageToPlayer(player, str));

		return 1;
	}

	private int getAllShulkerTeleports(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(ShulkerPortal.teleports.size() < 1) {
			sendMessageToPlayer(player, "No teleports yet!");
			return 1;
		}

		sendMessageToPlayer(player, "All teleports:");
		ShulkerPortal.teleports.forEach(tp -> sendMessageToPlayer(player,
				String.format("Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id(), tp.netherPos(), tp.overworldPos())
		));
		return 1;
	}

	private int addBadShulkerTeleport(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
		ShulkerPortal.badTeleports.add(new ShulkerPortal.Teleport(1, new Vec3d(0, 0, 0), new Vec3d(0, 0, 0)));
		return 1;
	}

	private int clearBadShulkerTeleports(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
		ShulkerPortal.badTeleports.clear();
		return 1;
	}

	private int getBadShulkerTeleports(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(ShulkerPortal.badTeleports.size() < 1) {
			sendMessageToPlayer(player, "No bad teleports yet!");
			return 1;
		}

		sendMessageToPlayer(player, "All bad teleports:");
		ShulkerPortal.badTeleports.forEach(tp -> sendMessageToPlayer(player,
				String.format("Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id(), tp.netherPos(), tp.overworldPos())
		));
		return 1;
	}

	private void sendMessageToPlayer(ServerPlayerEntity player, String message) {
		player.sendSystemMessage(new LiteralText(message), Util.NIL_UUID);
	}

	private int test(CommandContext<ServerCommandSource> context) {
		MinecraftServer server = context.getSource().getServer();

		ShulkerPortal.logger.info("Server tick count: " + server.getTicks());

		return 1;
	}

	private ServerWorld getWorld(CommandContext<ServerCommandSource> context, RegistryKey<World> dimensionRegistryKey) {
		return context.getSource().getServer().getWorld(dimensionRegistryKey);
	}
}
