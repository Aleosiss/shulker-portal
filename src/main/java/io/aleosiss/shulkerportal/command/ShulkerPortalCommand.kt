package io.aleosiss.shulkerportal.command

import carpet.utils.CommandHelper
import carpet.utils.Messenger
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings
import io.aleosiss.shulkerportal.service.ShulkerPortalLoggingService
import io.aleosiss.shulkerportal.service.ShulkerPortalLoggingService.Teleport
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.math.Vec3d
import java.util.function.Consumer


object ShulkerPortalCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val argumentBuilder = CommandManager.literal("ale")
                .requires { player: ServerCommandSource? -> CommandHelper.canUseCommand(player, ShulkerPortalCarpetSettings.commandShulker) }
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
        ShulkerPortalLoggingService.badTeleports.add(Teleport(1, Vec3d(0.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0)))
        return 1
    }

    private fun makeClearCommand(): ArgumentBuilder<ServerCommandSource, *> {
        return CommandManager.literal("clear")
                .then(CommandManager.literal("bad_shulker_teleports").executes { clearBadShulkerTeleports(it) })
                .then(CommandManager.literal("errors").executes { clearErrors(it) })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun clearErrors(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        ShulkerPortalLoggingService.errorMessages.clear()
        return 1
    }

    @Suppress("UNUSED_PARAMETER")
    private fun clearBadShulkerTeleports(serverCommandSourceCommandContext: CommandContext<ServerCommandSource>): Int {
        ShulkerPortalLoggingService.badTeleports.clear()
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
        if (ShulkerPortalLoggingService.errorMessages.size < 1) {
            Messenger.m(player, "l No errors logged.")
        } else {
            ShulkerPortalLoggingService.errorMessages.forEach(Consumer { str: String -> Messenger.m(player, "w $str") })
        }
        return 1
    }

    @Suppress("SameReturnValue")
    @Throws(CommandSyntaxException::class)
    private fun getAllShulkerTeleports(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player
        if (ShulkerPortalLoggingService.teleports.isEmpty()) {
            Messenger.m(player, "w No teleports yet!")
            return 1
        }
        Messenger.m(player, "w All teleports:")
        ShulkerPortalLoggingService.teleports.forEach(Consumer { tp: Teleport -> sendTeleportMessage(tp, player) })
        return 1
    }

    @Suppress("UNUSED_PARAMETER")
    private fun listSettings(source: ServerCommandSource): Int {
        return 1
    }

    @Suppress("unused", "SameReturnValue")
    @Throws(CommandSyntaxException::class)
    private fun getBadShulkerTeleports(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player
        if (ShulkerPortalLoggingService.badTeleports.isEmpty()) {
            Messenger.m(player, "l No bad teleports yet!")
            return 1
        }

        Messenger.m(player, "w All bad teleports:")
        ShulkerPortalLoggingService.badTeleports.forEach(Consumer { tp: Teleport -> sendTeleportMessage(tp, player) })
        return 1
    }


    private val clickableStyleOrigin = Style.EMPTY
        .withColor(TextColor.parse("#42c2f5").result().get())
        .withUnderline(true)

    private val clickableStyleDest = Style.EMPTY
        .withColor(TextColor.parse("#4295f5").result().get())
        .withUnderline(true)

    private fun sendTeleportMessage(
        tp: Teleport,
        player: ServerPlayerEntity?
    ) {
        val message = Text.literal("Shulker[${tp.id}]: ")
            .append("\n")
            .append(tpCommand(clickableStyleOrigin,"Nether Origin", tp.netherPos))
            .append(" | ")
            .append(tpCommand(clickableStyleDest, "Overworld Dest", tp.overworldPos))
        Messenger.m(player, message)
    }


    private fun tpCommand(style: Style, target: String, pos: Vec3d): Text {
        val unformatted1 = Text.literal("$target: [")
        val unformatted2 = Text.literal("]")
        val posCommandText = Text.literal("$pos").setStyle(
            style.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p ${pos.x} ${pos.y} ${pos.z}")))
        return unformatted1.append(posCommandText).append(unformatted2)
    }
}