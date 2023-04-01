/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.example

import WhilelangLexer
import WhilelangParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.example.cfg.CfgBuildVisitor
import org.example.cfg.CfgBuildVisitor2
import org.example.cfg.CfgNode
import org.example.cfg.draw

fun main() {
    val text = "print \"Test Nested If\";" +
            "if cmd <= 10 then {" +
            "    if cmd <= 20 then {" +
            "        c := 30" +
            "    } else {" +
            "        skip" +
            "    }" +
            "} else {" +
            "    skip" +
            "};" +
            "print a + b;" +
            "while b + a <= 10000 do {" +
            "    print b;" +
            "    b := a + b" +
            "};" +
            "print \"FINISH\";"+
            "print \"FINISH\";"+
            "print \"FINISH\";"+
            "print \"FINISH\""

    val charStream = CharStreams.fromString(text);
    val lexer = WhilelangLexer(charStream);
    val tokens = CommonTokenStream(lexer)
    val parser = WhilelangParser(tokens)
    val ret = CfgBuildVisitor2().visit(parser.program())

//    ret.forEach(CfgNode::printVerbose)

    val g = draw(ret[0])
    print(g.dot())
}