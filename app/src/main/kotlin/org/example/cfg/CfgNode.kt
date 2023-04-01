package org.example.cfg

class CfgNode(
    var lineText: String,
    val lineNum: Int,

    val assigned: MutableList<String> = mutableListOf(),
    val referenced: MutableList<String> = mutableListOf(),

    val nextNodes: MutableList<CfgNode> = mutableListOf(),
    val previousNodes: MutableList<CfgNode> = mutableListOf()
) {
    override fun toString() = String.format("<%s of line %d>", this.javaClass.simpleName, lineNum)

    private fun toVerboseString(): String {
        var ret: String = String.format("%d: %s\n    Assigned value: ", lineNum, lineText)
        assigned.forEach { ret += String.format("%s ", it) }
        ret += "\n    Referenced values: "
        referenced.forEach { ret += String.format("%s ", it) }
        ret += "\n    Previous lines: "
        previousNodes.forEach { ret += String.format("%d ", it.lineNum) }
        ret += "\n    Next lines: "
        nextNodes.forEach { ret += String.format("%d ", it.lineNum) }
        return ret
    }

    fun toDotString(): String {
        var ret = lineText.replace("\"", "\\\"")
        if (assigned.isNotEmpty()) {
            ret += "\nAssigned: "
            assigned.forEach { ret += String.format("%s ", it) }
        }
        if (referenced.isNotEmpty()) {
            ret += "\nReferenced: "
            referenced.forEach { ret += String.format("%s ", it) }
        }
        return ret
    }

    fun printVerbose() = print(toVerboseString() + "\n")

    enum class TYPES {
        REGULAR,
        WHILE,
        IF
    }
}
