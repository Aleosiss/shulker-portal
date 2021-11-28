package com.aleosiss.shulkerportal.carpet;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

import static carpet.settings.RuleCategory.COMMAND;

public class ShulkerPortalCarpetSettings {

	@Rule(desc = "Enables /ale command to view Shulker portal debugging info", category = COMMAND)
	public static String commandShulker = "ops";

	@Rule(
			desc = "Enable shulker positioning fix",
			category = { RuleCategory.BUGFIX, "ale" }
	)
	public static boolean enableShulkerPortalPositioningFix = true;

	@Rule(
			desc = "Enable shulker positioning debugging",
			category = { "ale" }
	)
	public static boolean enableShulkerPortalDebugging = false;
}
