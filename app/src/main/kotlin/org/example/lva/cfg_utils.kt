package org.example.lva

import org.example.cfg.CfgNode

internal fun findExitPoints(head: CfgNode): Collection<CfgNode> {
    val visited = mutableSetOf<CfgNode>()
    val exitPoints = mutableListOf<CfgNode>()

    val queue = ArrayDeque<CfgNode>()
    queue.add(head)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current in visited)
            continue
        visited.add(current)

        val notVisitedNodes = current.nextNodes.filter { it !in visited }
        if (notVisitedNodes.isNotEmpty())
            queue.addAll(notVisitedNodes)
        else
            exitPoints.add(current)
    }

    return exitPoints
}
