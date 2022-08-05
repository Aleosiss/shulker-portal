package io.aleosiss.shulkerportal.carpet

import carpet.settings.Rule
import carpet.settings.RuleCategory

object ShulkerPortalCarpetSettings {
    @Rule(desc = "Enables /ale command to view Shulker portal debugging info", category = [RuleCategory.COMMAND])
    var commandShulker: String = "ops"

    @JvmField
    @Rule(desc = "Enable shulker positioning fix", category = [RuleCategory.BUGFIX, "ale"])
    var enableShulkerPortalPositioningFix: Boolean = true

    @JvmField
    @Rule(desc = "Enable shulker positioning debugging", category = ["ale"])
    var enableShulkerPortalDebugging: Boolean = false

    @JvmField
    @Rule(desc = "Enable 1.17 Raid mechanics", category = [RuleCategory.CREATIVE, "ale"])
    var enable117RaiderDiscoveryMechanic: Boolean = false
}