package org.example.cfg

import io.github.rchowell.dotlin.DotNodeShape
import io.github.rchowell.dotlin.DotPortPos
import io.github.rchowell.dotlin.digraph
import java.util.Queue


fun draw(headNode: CfgNode) = digraph {
    node {
        shape = DotNodeShape.BOX
    }

    val q = ArrayList<CfgNode>()
    val applied = mutableSetOf<CfgNode>()
    q.add(headNode)

    while (q.isNotEmpty()) {
        val nodeA = q.removeFirst()
        if (nodeA in applied)
            continue
        + nodeA.lineNum.toString() + {
            label = nodeA.toDotString()
        }
        applied.add(nodeA)
        for (nodeB in nodeA.nextNodes) {
            nodeA.lineNum.toString() - nodeB.lineNum.toString()
            if (nodeB !in applied)
                q.add(nodeB)
        }
    }
}

