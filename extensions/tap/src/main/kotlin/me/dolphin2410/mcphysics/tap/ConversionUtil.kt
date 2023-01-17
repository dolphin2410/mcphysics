package me.dolphin2410.mcphysics.tap

import io.github.monun.tap.fake.FakeEntity
import me.dolphin2410.mcphysics.PhysicsVector
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

object ConversionUtil {
    fun Location.toPhysics() = PhysicsVector(x, y, z)

    fun PhysicsVector.toBukkit(world: World) = Location(world, x, y, z)

    fun FakeEntity<out Entity>.toPhysicsObject(runtime: TapPhysicsRuntime) = PaperPhysicsObject(runtime, this)
}