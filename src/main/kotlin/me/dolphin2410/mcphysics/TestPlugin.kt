package me.dolphin2410.mcphysics

import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer
import me.dolphin2410.mcphysics.Maths.toPhysics
import me.dolphin2410.mcphysics.paper.PaperPhysics
import me.dolphin2410.mcphysics.paper.PaperPhysicsObject
import me.dolphin2410.mcphysics.paper.toPhysicsObject
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin: JavaPlugin(), Listener {
    lateinit var fakeServer: FakeEntityServer
    companion object {
        lateinit var instance: TestPlugin
    }

    override fun onEnable() {
        instance = this
        val runtime = PaperPhysics()
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
                    obj.circle(player.location.toPhysics() + PhysicsVector(3, 3, 3), 5.0)
                    obj.addForce(PhysicsVector(1,0, 0))
                    // obj.project(PhysicsVector(0, 5, 0))
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