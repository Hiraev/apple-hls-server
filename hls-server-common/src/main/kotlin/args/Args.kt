package args

import org.kohsuke.args4j.Option

class Args {

    @Option(name = "-r", required = true)
    lateinit var root: String

    @Option(name = "-p", required = true)
    var port: Int = 8080

    @Option(name = "-b", required = true, handler = IntListOptionHandler::class)
    lateinit var bitrateList: IntArray

}
