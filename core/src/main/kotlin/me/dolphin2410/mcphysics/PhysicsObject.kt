package me.dolphin2410.mcphysics

import java.util.LinkedList
import kotlin.math.*

/**
 * McPhysics의 엔티티 단위
 */
abstract class PhysicsObject(private val runtime: PhysicsRuntime) {
    private val motionFactors = ArrayList<MotionFactor>()

    // gravity value for the object
    private val gravity = NewtonForce(PhysicsVector(0.0, -9.8, 0.0))

    // extra movement evaluated by updates
    private val extraMovement = PhysicsVector(0.0, 0.0, 0.0)

    // updates
    private val tasksQueue = LinkedList<ActionHandle>()

    // 속도 설정
    private val velocity = ProtectedPhysicsVector(PhysicsVector(0, 0, 0))

    // position
    abstract var position: PhysicsVector
    
    // 질량
    abstract val mass: Double

    abstract val valid: Boolean

    abstract fun getFloorPos(applyDistance: Double): PhysicsVector

    /**
     * 매 틱마다 실행, 개체의 물리량을 변경
     */
    fun update() {
        while (tasksQueue.isNotEmpty()) {
            tasksQueue.poll()
        }

        // pre-calculated version of the new position
        val temporaryPosition = position + extraMovement + motionByForce()

        if (temporaryPosition.y <= position.y) {
            val floorPos = getFloorPos(position.y - temporaryPosition.y + 1)

            // 지면과 충돌했는가?
            if (temporaryPosition.y < floorPos.y + 1) {
                temporaryPosition.y = floorPos.y + 1
                gravity.invalidate()
            } else {
                gravity.revalidate()
            }
        }

        extraMovement.setZero()

        position = temporaryPosition
    }

    /**
     * 등속원운동
     */
    fun circle(radius: Double, angularVelocity: Double): ActionHandle {
        val originTime = System.currentTimeMillis()

        var priorTime = originTime

        return registerAction {
            val newTime = System.currentTimeMillis()

            val priorTheta = angularVelocity * (priorTime - originTime) * 0.001
            val newTheta = angularVelocity * (newTime - originTime) * 0.001

            // TODO: allow explicit setting of plane
            extraMovement += PhysicsVector(radius * (cos(newTheta) - cos(priorTheta)), 0.0, radius * (sin(newTheta) - sin(priorTheta)))

            priorTime = newTime
        }
    }

    /**
     * update() 단계에서 실행되는 task 등록
     */
    fun registerAction(action: (ActionHandle) -> Unit): ActionHandle {
        val identical = tasksQueue.find { it.task == action }
        if (identical != null) {
            return identical
        }

        val handle = ActionHandle(action)
        tasksQueue.add(handle)
        return handle
    }

    /**
     * 등록한 task 제거
     */
    fun unregisterAction(handle: ActionHandle): Boolean {
        return tasksQueue.remove(handle)
    }

    /**
     * 순간적인 속도 변화
     */
    fun addInstantVelocity(velocity: InstantVelocity): Boolean {
        return this.motionFactors.add(velocity)
    }

    /**
     * 뉴턴 힘 가하기
     */
    fun addNewtonForce(force: NewtonForce): Boolean {
        return this.motionFactors.add(force)
    }

    private fun motionByForce(): PhysicsVector {
        val res = PhysicsVector.ZERO

        for (motionFactor in this.motionFactors) {
            velocity.setValue(velocity.getValue() + motionFactor.deltaVelocity(mass))
            res += motionFactor.deltaPosition(mass)
        }

        return res
    }
}