package me.dolphin2410.mcphysics

data class ActionHandle(internal val task: (ActionHandle) -> Unit) {
    private var valid = true

    fun cancel() {
        valid = false
    }
}