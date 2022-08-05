package io.aleosiss.shulkerportal

import carpet.CarpetExtension
import carpet.CarpetServer
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings
import io.aleosiss.shulkerportal.command.ShulkerPortalCommand
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory

class ShulkerPortal : ModInitializer, CarpetExtension {
    companion object {
        val logger = LoggerFactory.getLogger(ShulkerPortal::class.java)!!
        @JvmStatic
        fun mixin() {
            return
        }

        init {
            CarpetServer.manageExtension(ShulkerPortal())
        }
    }

    override fun onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(ShulkerPortalCarpetSettings::class.java)
    }

    override fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        ShulkerPortalCommand.register(dispatcher)
    }

    override fun onInitialize() {}
}