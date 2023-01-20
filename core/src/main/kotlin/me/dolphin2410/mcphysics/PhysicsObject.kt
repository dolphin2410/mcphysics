package me.dolphin2410.mcphysics

import kotlin.math.*

/**
 * McPhysics의 엔티티 단위
 */
abstract class PhysicsObject(private val runtime: PhysicsRuntime) {

    // 가한 힘
    private val force = ArrayList<Force>()

    var gravity: Force

    init {
        gravity = addForce(PhysicsVector(0.0, -9.8, 0.0))
    }

    protected fun initCenter() {
        physicsCenter.set(position.clone())
    }

    // 위치
    abstract var position: PhysicsVector
    
    // 비행 시간
    internal var flyTicks = 0

    // 작용점
    private val physicsCenter = PhysicsVector(0, 0, 0)
    
    // 작용점 대비 상대적 위치
    private val relPos = PhysicsVector(0, 0, 0)

    // 속도 설정
    private val velocity = PhysicsVector(0, 0, 0)
    
    // 질량
    abstract val mass: Double

    private val updates = ArrayList<ActionHandle>()

    abstract fun getFloorPos(applyDistance: Double): PhysicsVector

    /**
     * 매 틱마다 실행, 개체의 물리량을 변경
     */
    fun update() {
        flyTicks++
        for (update in updates) {
            update.task()
        }

        val deltaSeconds = 1.0 / runtime.ticksPerSecond

        // val acceleration = force / mass // F = ma -> a = F / m
        // acceleration -= PhysicsVector(0.0, 9.8, 0.0)   // Gravity

        // s = v0 * t + 1/2 a * t^2
        physicsCenter += velocity * deltaSeconds + calcForce()

        // 엔티티 이동 전 계산값
        val pre = physicsCenter + relPos

        if (pre.y <= position.y) {
            val floorPos = getFloorPos(position.y - pre.y + 1)

            // 지면과 충돌했는가?
            if (pre.y < floorPos.y + 1) {
                pre.y = floorPos.y + 1
            }

            if (pre.y == floorPos.y + 1) {
                gravity.startTick = flyTicks
            }

        }

        position = pre

        relPos.set(PhysicsVector.ZERO)
    }

    /**
     * 등속원운동
     *
     * y축 평형, 주어진 반지름 값을 갖도록 가상의 장력 설정
     */
    fun circle(center: PhysicsVector, radius: Double): ActionHandle {

        // y축 평형 (정지)
        val mg = PhysicsVector(0.0, 9.8, 0.0) * mass
        addForce(gravity.child(this, mg))
        
        // 중심과 위치
        val distance = center - position
        
        // 중력 벡터와 장력 벡터가 이루는 각
        val theta = asin(radius / distance.length)

        // 각속도
        val w = sqrt(9.8 / (distance.length * cos(theta)))

        physicsCenter.set(center - PhysicsVector(0.0, distance.length * cos(theta), 0.0))

        return registerAction {
            val flySeconds = flyTicks.toDouble() / runtime.ticksPerSecond

            relPos += PhysicsVector(radius * cos(w * flySeconds), 0.0, radius * sin(w * flySeconds))
        }
    }

    /**
     * update() 단계에서 실행되는 task 등록
     */
    fun registerAction(action: () -> Unit): ActionHandle {
        val identical = updates.find { it.task == action }
        if (identical != null) {
            return identical
        }

        val handle = ActionHandle(action)
        updates.add(handle)
        return handle
    }

    /**
     * 등록한 task 제거
     */
    fun unregisterAction(handle: ActionHandle): Boolean {
        initCenter()
        return updates.remove(handle)
    }

    /**
     * 단위: m/s (블록/s)
     * 
     * 등속 운동
     */
    fun applyVelocity(velocity: PhysicsVector) {
        this.velocity += velocity
    }

    /**
     * 단위: N `[뉴턴]`
     * 
     * 등가속 운동
     */
    fun addForce(force: PhysicsVector): Force {
        val f = Force(flyTicks, force)
        this.force.add(f)
        return f
    }

    fun addForce(force: Force): Force {
        this.force.add(force)
        return force
    }

    fun calcForce(): PhysicsVector {
        val res = PhysicsVector.ZERO

        for (force in this.force) {
            val elapsed = (flyTicks - force.startTick).toDouble()
            val appliedSec = (elapsed) / runtime.ticksPerSecond
            val prevSec = (elapsed - 1) / runtime.ticksPerSecond

            val delta = force.v * (appliedSec.pow(2) - prevSec.pow(2)) * 0.5
            res += delta
        }

        return res
    }
}