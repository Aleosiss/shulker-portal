package io.aleosiss.shulkerportal.carpet

import carpet.api.settings.Rule
import carpet.api.settings.RuleCategory

object ShulkerPortalCarpetSettings {

    private const val CATEGORY_ALEOSISS = "aleosiss"


    @JvmField
//    @Rule(desc = "Enables /ale command to view Shulker portal debugging info", category = [RuleCategory.COMMAND])
    @Rule(categories = [RuleCategory.COMMAND])
    var commandShulker: String = "ops"

    @JvmField
//    @Rule(desc = "Enable shulker positioning fix", category = [RuleCategory.BUGFIX, "ale"])
    @Rule(categories = [RuleCategory.COMMAND, CATEGORY_ALEOSISS])
    var enableShulkerPortalPositioningFix: Boolean = true

    @JvmField
//    @Rule(desc = "Enable shulker positioning debugging", category = ["ale"])
    @Rule(categories = [RuleCategory.COMMAND, CATEGORY_ALEOSISS])
    var enableShulkerPortalDebugging: Boolean = false

    @JvmField
//    @Rule(desc = "Enable 1.17 Raid mechanics", category = [RuleCategory.CREATIVE, "ale"])
    @Rule(categories = [RuleCategory.COMMAND, CATEGORY_ALEOSISS])
    var enable117RaiderDiscoveryMechanic: Boolean = false
}