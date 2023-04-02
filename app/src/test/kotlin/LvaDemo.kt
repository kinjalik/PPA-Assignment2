import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.example.cfg.CfgBuildVisitor2
import org.example.cfg.CfgNode
import org.example.cfg.draw
import org.example.lva.liveVariableAnalysis
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.nio.file.Files
import java.nio.file.Path

class LvaDemo {
    @ParameterizedTest
    @ArgumentsSource(value = ExampleProgramArgumentProvider::class)
    fun `step-by-step`(filePath: String) {
        val text = Files.readString(Path.of(filePath))

        val charStream = CharStreams.fromString(text);
        val lexer = WhilelangLexer(charStream);
        val tokens = CommonTokenStream(lexer)
        val parser = WhilelangParser(tokens)
        val ret = CfgBuildVisitor2().visit(parser.program())

//        liveVariableAnalysis(ret[0]).toList()
//        ret.forEach(CfgNode::printVerbose)

        print("#### Initial CFG\n")

        print("```graphviz\n")
        print(draw(ret[0], showLive = true).dot())
        print("```\n")

        val iter = liveVariableAnalysis(ret[0])
        iter.forEachIndexed { i, _ ->
            print("#### Iteration $i\n")
            print("```graphviz\n")
            print(draw(ret[0], showLive = true).dot())
            print("```\n")
        }
    }
}