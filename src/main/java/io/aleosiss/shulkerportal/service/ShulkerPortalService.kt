package io.aleosiss.shulkerportal.service

import net.minecraft.util.math.Vec3d
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import java.util.stream.Collectors

class ShulkerPortalService {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ShulkerPortalService::class.java)
        lateinit var INSTANCE: ShulkerPortalService

        private fun getAverageNetherPos(teleports: List<Teleport>): Vec3d {
            return getAvgPos(teleports.stream().map { t: Teleport -> t.netherPos }.collect(Collectors.toList()))
        }

        private fun getAverageOverworldPos(teleports: List<Teleport>): Vec3d {
            return getAvgPos(teleports.stream().map { t: Teleport -> t.overworldPos }.collect(Collectors.toList()))
        }

        private fun getAvgPos(positions: List<Vec3d>): Vec3d {
            val num = positions.size
            var output = Vec3d(0.0, 0.0, 0.0)
            for (pos in positions) {
                output = output.add(pos)
            }
            return output.multiply(1.0 / num)
        }
    }

    data class Teleport(val id: Int, val netherPos: Vec3d, val overworldPos: Vec3d)
    var teleports: MutableList<Teleport> = ArrayList()
    val badTeleports: MutableList<Teleport> = ArrayList()
    val errorMessages: MutableList<String> = ArrayList()

    fun processShulkerTeleport(entityMixin: Int, netherPos: Vec3d, overworldPos: Vec3d, errors: List<String>?) {
        logger.info(String.format("Shulker[%s] was teleported from %s in the nether to %s in the overworld!", entityMixin, netherPos, overworldPos))
        val avgPos = getAverageOverworldPos(teleports)
        val avgNetherPos = getAverageNetherPos(teleports)
        val teleport = Teleport(entityMixin, netherPos, overworldPos)
        teleports.add(teleport)
        errorMessages.addAll(errors!!)
        if (teleports.size < 20) {
            return
        }
        if (!netherPos.isInRange(avgNetherPos, 4.0) || !overworldPos.isInRange(avgPos, 4.0)) {
            logger.warn("And that's out of whack, what happened?!")
            badTeleports.add(teleport)
        }
        if (teleports.size > 150) {
            teleports = teleports.subList(130, 150)
        }
    }
}