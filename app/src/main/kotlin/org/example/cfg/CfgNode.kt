package org.example.cfg

data class CfgNode(
    var lineText: String,
    val lineNum: Int,
) {
    val assigned: MutableList<String> = mutableListOf()
    val referenced: MutableList<String> = mutableListOf()

    val nextNodes: MutableList<CfgNode> = mutableListOf()
    val previousNodes: MutableList<CfgNode> = mutableListOf()

    var currentLive = setOf<String>()
    override fun toString() = String.format("line = %d; %s", lineNum, lineText)

    private fun toVerboseString(): String {
        var ret: String = String.format("%d: %s\n    Assigned value: ", lineNum, lineText)
        assigned.forEach { ret += String.format("%s ", it) }
        ret += "\n    Referenced values: "
        referenced.forEach { ret += String.format("%s ", it) }
        ret += "\n    Previous lines: "
        previousNodes.forEach { ret += String.format("%d ", it.lineNum) }
        ret += "\n    Next lines: "
        nextNodes.forEach { ret += String.format("%d ", it.lineNum) }
        ret += "\n    Live: "
        currentLive.forEach { ret += String.format("%s ", it) }
        return ret
    }

    fun toDotString(showAssigned: Boolean, showReferenced: Boolean, showLive: Boolean): String {
        var ret = lineText.replace("\"", "\\\"")
        if (showAssigned && assigned.isNotEmpty()) {
            ret += "\nAssigned: "
            assigned.forEach { ret += String.format("%s ", it) }
        }
        if (showReferenced && referenced.isNotEmpty()) {
            ret += "\nReferenced: "
            referenced.forEach { ret += String.format("%s ", it) }
        }
        if (showLive && currentLive.isNotEmpty()) {
            ret += "\nLive: "
            currentLive.forEach { ret += String.format("%s ", it) }
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
