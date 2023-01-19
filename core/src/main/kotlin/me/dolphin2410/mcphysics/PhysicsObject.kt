package me.dolphin2410.mcphysics

import kotlin.math.*

/**
 * McPhysics의 엔티티 단위
 */
abstract class PhysicsObject(private val runtime: PhysicsRuntime) {
    protected fun initCenter() {
        physicsCenter.set(position.clone())
    }

    var isOnGround: Boolean
        get() = flyTicks == 0
        set(value) {
            if (value) {
                flyTicks = 0
            }
        }
    
    // 위치
    abstract var position: PhysicsVector
    
    // 비행 시간
    private var flyTicks = 0
    
    // 가한 힘
    private val force = PhysicsVector(0, 0, 0)

    // 작용점
    private val physicsCenter = PhysicsVector(0, 0, 0)
    
    // 작용점 대비 상대적 위치
    private val relPos = PhysicsVector(0, 0, 0)

    // 속도 설정
    private val velocity = PhysicsVector(0, 0, 0)
    
    // 질량
    abstract val mass: Double

    private val updates = ArrayList<ActionHandle>()

    abstract fun getFloorPos(): PhysicsVector

    /**
     * 매 틱마다 실행, 개체의 물리량을 변경
     */
    fun update() {
        flyTicks++
        for (update in updates) {
            update.task()
        }

        val deltaSeconds = 1.0 / runtime.ticksPerSecond
        val flySeconds = flyTicks / runtime.ticksPerSecond

        val acceleration = force / mass // F = ma -> a = F / m
        acceleration -= PhysicsVector(0.0, 9.8, 0.0)   // Gravity

        // s = v0 * t + 1/2 a * t^2
        physicsCenter += velocity * deltaSeconds + acceleration * (deltaSeconds * ( deltaSeconds + 2 * flySeconds )) / 2.0

        // 엔티티 이동 전 계산값
        val pre = physicsCenter + relPos

        // 지면과 충돌했는가?
        if (pre.y < getFloorPos().y + 1) {
            pre.y = getFloorPos().y + 1
            isOnGround = true
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
        force += mg
        
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
    fun addForce(force: PhysicsVector) {
        this.force += force
    }
}