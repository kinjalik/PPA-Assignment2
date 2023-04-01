package org.example.cfg

import WhilelangBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval

class CfgBuildVisitor : WhilelangBaseVisitor<List<CfgNode>>() {

    private val lines = mutableMapOf<Int, CfgNode>()
    private fun getLineNode(lineNum: Int, lineText: String = ""): CfgNode {
        if (lineNum !in lines) {
            val cur = CfgNode(lineText, lineNum)
            lines[lineNum] = cur

            // Link to previous
            if (lineNum != 0 && lineNum - 1 in lines) {
                val prev = lines[lineNum - 1]!!
                cur.previousNodes += prev
                prev.nextNodes += cur
            }
        }
        val ret = lines[lineNum]!!
        if (ret.lineText == "") {
            ret.lineText = lineText
        }
        return ret
    }

    override fun defaultResult(): List<CfgNode> = lines.values.toList()

    private var insideExpression = false
    override fun visitAttrib(ctx: WhilelangParser.AttribContext?): List<CfgNode> {
        val lineText = sourceTextForContext(ctx!!)
        val lineNum = ctx.start.line
        val idName = ctx.ID().toString()

        val node = getLineNode(lineNum, lineText)
        node.assigned += idName

        insideExpression = true
        val ret = super.visitAttrib(ctx)
        insideExpression = false
        return ret
    }

    override fun visitId(ctx: WhilelangParser.IdContext?): List<CfgNode> {
        val lineText = sourceTextForContext(ctx!!)
        val lineNum = ctx.start.line
        val idName = ctx.ID().toString()

        val node = getLineNode(lineNum, lineText)
        if (insideExpression) {
            node.referenced += idName
        }

        return super.visitId(ctx)
    }

    var currentBlockCtx: CfgNode? = null
    override fun visitIf(ctx: WhilelangParser.IfContext?): List<CfgNode> {
        val lineText = "if (" + sourceTextForContext(ctx!!.bool()) + ")"
        val lineNum = ctx.start.line
        val node = getLineNode(lineNum, lineText)

        val oldIfCtx = currentBlockCtx
        currentBlockCtx = node
        insideExpression = true
        super.visit(ctx!!.bool())
        insideExpression = false
        currentBlockCtx = oldIfCtx

        super.visit(ctx.statement(0))
        super.visit(ctx.statement(1))
        return defaultResult()
    }

    override fun visitWrite(ctx: WhilelangParser.WriteContext?): List<CfgNode> {
        val lineText = sourceTextForContext(ctx!!)
        val lineNum = ctx.start.line
        val node = getLineNode(lineNum, lineText)

        insideExpression = true
        super.visit(ctx.expression())
        insideExpression = false
        return defaultResult()
    }

    override fun visitSkip(ctx: WhilelangParser.SkipContext?) = genericLineText(ctx!!)

    private fun genericLineText(ctx: ParserRuleContext): List<CfgNode> {
        val lineText = sourceTextForContext(ctx)
        val lineNum = ctx.start.line
        val node = getLineNode(lineNum, lineText)
        return defaultResult()
    }

    // Inspired by https://stackoverflow.com/questions/50443728/context-gettext-excludes-spaces-in-antlr4
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