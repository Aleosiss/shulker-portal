package io.aleosiss.shulkerportal

import carpet.CarpetExtension
import carpet.CarpetServer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mojang.brigadier.CommandDispatcher
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings
import io.aleosiss.shulkerportal.command.ShulkerPortalCommand
import net.fabricmc.api.ModInitializer
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.ServerCommandSource
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets


class ShulkerPortal : ModInitializer, CarpetExtension {
    companion object {
        const val MOD_ID: String = "shulker-portal"
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

    override fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>, commandBuildContext: CommandRegistryAccess) {
        ShulkerPortalCommand.register(dispatcher)
    }

    override fun canHasTranslations(lang: String?): MutableMap<String, String> {
        val langFile: InputStream = ShulkerPortal::class.java.classLoader
            .getResourceAsStream("assets/$MOD_ID/lang/$lang.json")
            ?: return mutableMapOf()
        val jsonData: String = try {
            IOUtils.toString(langFile, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            return mutableMapOf()
        }

        val gson: Gson = GsonBuilder().setLenient().create()
        val map: Map<String, String> = gson.fromJson(jsonData, object : TypeToken<Map<String?, String?>?>() {}.type)
        val map2: MutableMap<String, String> = HashMap()

        // create translation keys for both carpet and extension settingsManagers
        map.forEach { (key: String, value: String) ->
            map2[key] = value
            if (key.startsWith("$MOD_ID.rule.")) {
                map2[key.replace("$MOD_ID.rule.", "carpet.rule.")] = value
            }
        }

        return map2
    }

    override fun onInitialize() {}
}