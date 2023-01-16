package me.dolphin2410.mcphysics

import org.bukkit.Location
import org.bukkit.World

object Maths {
    fun Location.toPhysics() = PhysicsVector(x, y, z)
    fun PhysicsVector.toBukkit(world: World) = Location(world, x, y, z)
}