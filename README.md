# McPhysics
물리가 없는 가상엔티티를 위한 물리엔진

- [x] 등가속도 운동(F=ma)
- [x] 등속 운동
- [x] 등속 원운동
- [ ] 나머지

### Notice
Currently in Beta. It is ***Experimental And Unstable***

### 사용법

#### build.gradle.kts 
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.github.dolphin2410:mcphysics-core:<version>")
    compileOnly("io.github.dolphin2410:mcphysics-tap:<version>") // Tap 사용시
}
```

#### plugin.yml
```yaml
libraries:
  - io.github.dolphin2410:mcphysics-core:<version>
  - io.github.dolphin2410:mcphysics-tap:<version> # Tap 사용시
```

### 예제
Tap의 FakeEntity는 중력과 같은 기본 적인 힘이 작용하지 않습니다. McPhysics를 이용해 가능하게 만들어봅시다. 

```kotlin
// mcphysics-tap이 필요합니다

class YourPlugin: JavaPlugin(), Listener {
    lateinit var fakeServer: FakeEntityServer

    override fun onEnable() {
        val runtime = TapPhysicsRuntime()
        fakeServer = FakeEntityServer.create(this)
        server.scheduler.runTaskTimer(this, fakeServer::update, 0, 1)
        server.scheduler.runTaskTimer(this, runtime::update, 0, 1)
        server.pluginManager.registerEvents(this, this)

        kommand {
            register("createObject") {
                executes {
                    val entity = fakeServer.spawnEntity(player.location, ArmorStand::class.java)
                    val obj = runtime.addObject(entity)
                    
                    // +y 방향으로 5m/s 등속운동
                    obj.project(PhysicsVector(0, 5, 0))
                    
                    // 플레이어 좌표 + (3, 3, 3) 을 중심으로 반지름이 5m인 등속 원운동
                    // m * r * w^2 = m * g * tan(theta)
                    // y축 평형(등속운동), xz평면 위를 등속 운동
                    obj.circle(player.location.toPhysics() + PhysicsVector(3, 3, 3), 5.0)
                    
                    // 힘 작용
                    obj.addForce(PhysicsVector(1, 0, 0))
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
```


### 참고 코드

- [monun/paper-sample-complex](https://github.com/monun/paper-sample-complex)의 publish 기능

### Licensed Under GPL-v3.0
