package org.example.lva

import org.example.cfg.CfgNode

private const val ITERATION_LIMIT = 1000

fun liveVariableAnalysis(head: CfgNode) = sequence {
    val exitPoints = findExitPoints(head)

    var updated = true
    var i = 0
    while (updated) {
//        print("Iteration ${i + 1}\n")
        updated = false
        i++
        if (i >= ITERATION_LIMIT) throw TooManyIterations()

        val queue = ArrayDeque<CfgNode>()
        val passed = mutableSetOf<CfgNode>()
        queue.addAll(exitPoints)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current in passed) continue
            passed.add(current)

            val newLive = calcLive(current)
            updated = updated || newLive != current.currentLive
            current.currentLive = newLive

            queue.addAll(pred(current))
        }
        yield(Unit)
    }
}