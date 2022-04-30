package com.aleosiss.shulkerportal.service;

import com.aleosiss.shulkerportal.command.ShulkerPortalCommand;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShulkerPortalService {
	public static final Logger logger = LoggerFactory.getLogger(ShulkerPortalService.class);
	private static ShulkerPortalService INSTANCE;

	public record Teleport(int id, Vec3d netherPos, Vec3d overworldPos) {};
	private List<Teleport> teleports = new ArrayList<>();
	private final List<Teleport> badTeleports = new ArrayList<>();
	private final List<String> errorMessages = new ArrayList<>();

	public static ShulkerPortalService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ShulkerPortalService();
		}

		return INSTANCE;
	}

	public void processShulkerTeleport(int entityMixin, Vec3d netherPos, Vec3d overworldPos, List<String> errors) {
		logger.info(String.format("Shulker[%s] was teleported from %s in the nether to %s in the overworld!", entityMixin, netherPos, overworldPos));

		Vec3d avgPos = getAverageOverworldPos(teleports);
		Vec3d avgNetherPos = getAverageNetherPos(teleports);

		Teleport teleport = new Teleport(entityMixin, netherPos, overworldPos);
		teleports.add(teleport);
		errorMessages.addAll(errors);
		if(teleports.size() < 20) {
			return;
		}

		if(!netherPos.isInRange(avgNetherPos, 4.0) || !overworldPos.isInRange(avgPos, 4.0)) {
			logger.warn("And that's out of whack, what happened?!");
			badTeleports.add(teleport);
		}

		if(teleports.size() > 150) {
			teleports = teleports.subList(130, 150);
		}
	}

	public List<Teleport> getTeleports() {
		return teleports;
	}

	public List<Teleport> getBadTeleports() {
		return badTeleports;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	private static Vec3d getAverageNetherPos(List<Teleport> teleports) {
		return ShulkerPortalService.getAvgPos(teleports.stream().map(t -> t.netherPos).collect(Collectors.toList()));
	}

	private static Vec3d getAverageOverworldPos(List<Teleport> teleports) {
		return ShulkerPortalService.getAvgPos(teleports.stream().map(t -> t.overworldPos).collect(Collectors.toList()));
	}

	public static Vec3d getAvgPos(List<Vec3d> positions) {
		int num = positions.size();
		Vec3d output = new Vec3d(0, 0, 0);
		for(Vec3d pos : positions) {
			output = output.add(pos);
		}

		return output.multiply((1.0 / num));
	}
}
