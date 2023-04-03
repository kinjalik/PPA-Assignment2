package org.example.cfg

import WhilelangBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.TerminalNode

class CfgBuildVisitor : WhilelangBaseVisitor<List<CfgNode>>(){
    private val lines = mutableMapOf<Int, CfgNode>()
    private fun getLineNode(lineNum: Int, lineText: String = ""): CfgNode {
        if (lineNum !in lines) {
            val cur = CfgNode(lineText, lineNum)
            lines[lineNum] = cur
        }
        val ret = lines[lineNum]!!
        if (ret.lineText == "") {
            ret.lineText = lineText
        }
        return ret
    }

    override fun defaultResult(): List<CfgNode> = lines.values.toList()
    override fun visitProgram(ctx: WhilelangParser.ProgramContext?): List<CfgNode> {
        val mainSeq = ctx!!.seqStatement()
        visitSeqStatement(mainSeq, listOf())
        return defaultResult()
    }

    var currentNode: CfgNode? = null
    override fun visitId(ctx: WhilelangParser.IdContext?): List<CfgNode> {
        if (currentNode != null)
            currentNode!!.referenced.add(ctx!!.ID().toString())
        return super.visitId(ctx)
    }

    override fun visitAttrib(ctx: WhilelangParser.AttribContext?): List<CfgNode> {
        if (currentNode != null)
            currentNode!!.assigned.add(ctx!!.ID().toString())
        return super.visitAttrib(ctx)
    }

    var lastLine = 1
    fun visitSeqStatement(ctx: WhilelangParser.SeqStatementContext?, prevNodes: List<Int>): List<Int> {
        var prevNodeNums: List<Int> = prevNodes
        ctx!!.children.forEach {
            if (it is TerminalNode)
                return@forEach

            if (it is WhilelangParser.IfContext) {
                prevNodeNums = visitIf(it, prevNodeNums)
                return@forEach
            }

            if (it is WhilelangParser.WhileContext) {
                prevNodeNums = visitWhile(it, prevNodeNums)
                return@forEach
            }

            val text = it.text
            val lineNum = lastLine++
            val node = getLineNode(lineNum, text)
            prevNodeNums!!.forEach {
                val curPrevNode = getLineNode(it)
                node.previousNodes += curPrevNode!!
                curPrevNode!!.nextNodes += node
            }
            prevNodeNums = listOf(node.lineNum)

            currentNode = node
            visit(it)

        }
        return prevNodeNums
    }

    private fun visitWhile(ctx: WhilelangParser.WhileContext?, prevLines: List<Int>): List<Int> {
        val lineText = "while (" + sourceTextForContext(ctx!!.bool()) + ")"
        val codeLineNum = lastLine++
        val node = getLineNode(codeLineNum, lineText)
        prevLines.forEach {
            val prev = getLineNode(it)
            node.previousNodes += prev
            prev.nextNodes += node
        }
        val iterEnd = visitSeqStatement(ctx.statement().children[1] as WhilelangParser.SeqStatementContext?,
                      listOf(codeLineNum))
        iterEnd.forEach {
            val endNode = getLineNode(it)
            endNode.nextNodes += node
        }

        currentNode = node
        visit(ctx.bool())

        return listOf(node.lineNum)
    }

    private fun visitIf(ctx: WhilelangParser.IfContext?, prevLines: List<Int>): List<Int> {
        val lineText = "if (" + sourceTextForContext(ctx!!.bool()) + ")"
        val codeLineNum = lastLine++
        val node = getLineNode(codeLineNum, lineText)
        prevLines.forEach {
            val prev = getLineNode(it)
            node.previousNodes += prev
            prev.nextNodes += node
        }
        currentNode = node
        visit(ctx.bool())

        val prevFromTrue = visitSeqStatement(ctx.statement(0).children[1] as WhilelangParser.SeqStatementContext?,
                                             listOf(codeLineNum))
        val prevFromFalse = visitSeqStatement(ctx.statement(1).children[1] as WhilelangParser.SeqStatementContext?,
                                              listOf(codeLineNum))

        return prevFromTrue + prevFromFalse
    }

    private fun sourceTextForContext(context: ParserRuleContext): String {
        val startToken = context.start
        val line = startToken.line
        val stopToken = context.stop

        val cs = startToken.tokenSource.inputStream;
        val startIndex = startToken.startIndex
        val stopIndex = stopToken?.stopIndex ?: -1;
        return cs.getText(Interval(startIndex, stopIndex)) ?: "NO LINE"
    }
}