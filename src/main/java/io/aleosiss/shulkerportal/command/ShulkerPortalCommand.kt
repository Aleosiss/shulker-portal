package io.aleosiss.shulkerportal.command

import carpet.settings.SettingsManager
import carpet.utils.Messenger
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings
import io.aleosiss.shulkerportal.service.ShulkerPortalService
import io.aleosiss.shulkerportal.service.ShulkerPortalService.Teleport
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.Vec3d
import java.util.function.Consumer

object ShulkerPortalCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val argumentBuilder = CommandManager.literal("ale")
                .requires { player: ServerCommandSource? -> SettingsManager.canUseCommand(player, ShulkerPortalCarpetSettings.commandShulker) }
                .then(makeGetCommand())
                .then(makeClearCommand())
                .then(makeAddCommand())
        dispatcher.register(argumentBuilder)
    }

    private fun makeAddCommand(): ArgumentBuilder<ServerCommandSource, *> {
        return CommandManager.literal("add")
                .then(CommandManager.literal("bad_shulker_teleport").executes { addDebugBadShulkerTeleport(it) })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun addDebugBadShulkerTeleport(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        ShulkerPortalService.INSTANCE.badTeleports.add(Teleport(1, Vec3d(0.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0)))
        return 1
    }

    private fun makeClearCommand(): ArgumentBuilder<ServerCommandSource, *> {
        return CommandManager.literal("clear")
                .then(CommandManager.literal("bad_shulker_teleports").executes { clearBadShulkerTeleports(it) })
                .then(CommandManager.literal("errors").executes { clearErrors(it) })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun clearErrors(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        ShulkerPortalService.INSTANCE.errorMessages.clear()
        return 1
    }

    @Suppress("UNUSED_PARAMETER")
    private fun clearBadShulkerTeleports(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        ShulkerPortalService.INSTANCE.badTeleports.clear()
        return 1
    }

    private fun makeGetCommand(): ArgumentBuilder<ServerCommandSource, *> {
        return CommandManager.literal("get")
                .then(CommandManager.literal("bad_shulker_teleports").executes { getBadShulkerTeleports(it) })
                .then(CommandManager.literal("all_shulker_teleports").executes { getAllShulkerTeleports(it) })
                .then(CommandManager.literal("errors").executes { getErrors(it) })
    }

    @Throws(CommandSyntaxException::class)
    private fun getErrors(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        val player = serverCommandSourceCommandContext.source.player
        if (ShulkerPortalService.INSTANCE.errorMessages.size < 1) {
            Messenger.m(player, "l No errors logged.")
        } else {
            ShulkerPortalService.INSTANCE.errorMessages.forEach(Consumer { str: String -> Messenger.m(player, "w $str") })
        }
        return 1
    }

    @Throws(CommandSyntaxException::class)
    private fun getAllShulkerTeleports(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player
        if (ShulkerPortalService.INSTANCE.teleports.isEmpty()) {
            Messenger.m(player, "w No teleports yet!")
            return 1
        }
        Messenger.m(player, "w All teleports:")
        ShulkerPortalService.INSTANCE.teleports.forEach(Consumer { tp: Teleport -> Messenger.m(player, String.format("w Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id, tp.netherPos, tp.overworldPos)) })
        return 1
    }

    @Suppress("UNUSED_PARAMETER")
    private fun listSettings(source: ServerCommandSource): Int {
        return 1
    }

    @Suppress("unused")
    @Throws(CommandSyntaxException::class)
    private fun getBadShulkerTeleports(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player
        if (ShulkerPortalService.INSTANCE.badTeleports.isEmpty()) {
            Messenger.m(player, "l No bad teleports yet!")
            return 1
        }
        Messenger.m(player, "w All bad teleports:")
        ShulkerPortalService.INSTANCE.badTeleports.forEach(Consumer { tp: Teleport -> Messenger.m(player, String.format("w Shulker[%s]: Nether Origin: [%s] | Overworld Dest: [%s]", tp.id, tp.netherPos, tp.overworldPos)) })
        return 1
    }
}