package args

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.OptionDef
import org.kohsuke.args4j.spi.OneArgumentOptionHandler
import org.kohsuke.args4j.spi.Setter

class IntListOptionHandler(
        parser: CmdLineParser,
        option: OptionDef,
        setter: Setter<List<Int>>
) : OneArgumentOptionHandler<List<Int>>(parser, option, setter) {

    override fun parse(argument: String): List<Int> = argument.split(":").map(Integer::valueOf)

}
