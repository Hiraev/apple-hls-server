package args

import org.kohsuke.args4j.CmdLineParser

object ArgsParser {

    fun parse(args: Array<String>) = Args().also {
        CmdLineParser(it).parseArgument(*args)
    }

}
