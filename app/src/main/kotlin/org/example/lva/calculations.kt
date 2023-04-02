package org.example.lva

import org.example.cfg.CfgNode


internal fun calcLive(n: CfgNode) = succ(n).map(CfgNode::currentLive).let(::aggUnion) subtract def(n) union ref(n)

internal fun succ(n: CfgNode): Collection<CfgNode> = n.nextNodes
internal fun pred(n: CfgNode): Collection<CfgNode> = n.previousNodes

private fun def(n: CfgNode): Set<String> = n.assigned.toSet()
private fun ref(n: CfgNode): Set<String> = n.referenced.toSet()

private fun live(n: CfgNode): Collection<String> = n.currentLive

internal fun <T> aggUnion(sets: Iterable<Collection<T>>): Collection<T> {
    var result = setOf<T>()
    sets.forEach { result = result.union(it) }
    return result
}
