package net.coffeewarriors.incentivetimer

import java.io.Serializable

class IncentiveItem(var name: String, var chance: Int) :
    Serializable {
    var isUnlocked = false
        private set

    fun unlock() {
        isUnlocked = true
    }

    fun lock() {
        isUnlocked = false
    }

}