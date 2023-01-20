package me.dolphin2410.mcphysics.plugin

import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer
import me.dolphin2410.mcphysics.PhysicsVector
import me.dolphin2410.mcphysics.tap.ConversionUtil.toPhysics
import me.dolphin2410.mcphysics.tap.ConversionUtil.toPhysicsObject
import me.dolphin2410.mcphysics.tap.TapPhysicsRuntime
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class DebugPlugin: JavaPlugin(), Listener {
    lateinit var fakeServer: FakeEntityServer
    companion object {
        lateinit var instance: DebugPlugin
    }

    override fun onEnable() {
        instance = this
        val runtime = TapPhysicsRuntime()
        fakeServer = FakeEntityServer.create(this)
        server.scheduler.runTaskTimer(this, fakeServer::update, 0, 1)
        server.scheduler.runTaskTimer(this, runtime::update, 0, 1)
        server.pluginManager.registerEvents(this, this)

        kommand {
            register("testme") {
                executes {
                    val entity = fakeServer.spawnEntity(player.location, ArmorStand::class.java)
                    val obj = entity.toPhysicsObject(runtime)
                    runtime.addObject(obj)
                    object: BukkitRunnable() {
                        override fun run() {
                            obj.circle(player.location.toPhysics() + PhysicsVector(3, 3, 3), 5.0)
                        }
                    }.runTaskLater(this@DebugPlugin, 20)
                    // obj.addForce(PhysicsVector(1, 0, 0))
                    // obj.applyVelocity(PhysicsVector(0, 5, 0))
                }
            }
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        fakeServer.addPlayer(e.player)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        fakeServer.removePlayer(e.player)
    }
}