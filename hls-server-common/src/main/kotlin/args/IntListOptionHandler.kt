package args

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.OptionDef
import org.kohsuke.args4j.spi.OptionHandler
import org.kohsuke.args4j.spi.Parameters
import org.kohsuke.args4j.spi.Setter

class IntListOptionHandler(
        parser: CmdLineParser,
        option: OptionDef,
        setter: Setter<Int>
) : OptionHandler<Int>(parser, option, setter) {

    override fun parseArguments(params: Parameters): Int {
        var counter = 0
        while (counter < params.size()) {
            val param = params.getParameter(counter)
            if (param.startsWith("-")) {
                break
            }
            param.split(":").map(Integer::valueOf).forEach(setter::addValue)
            counter++
        }

        return counter
    }

    override fun getDefaultMetaVariable() = ""

}
