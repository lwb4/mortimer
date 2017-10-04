# mortimer

This is a tool that can trace every function call in a Clojure file. It is the spiritual successor to [rage](https://github.com/lincoln-b/rage), and another small step in the general direction of building better debugging tools for the Clojure ecosystem.

Simply put, you give mortimer a file, and it runs it with a trace in every function call of every form, including inner forms. It's sort of similar to clojure.tools.trace but much more pervasive.

Special thanks to the many people who answered my questions on Clojurians slack, as well as Bill Piel's [Sayid](https://github.com/bpiel/sayid) which was the initial inspiration for this tool.

## Usage

Mortimer works better as a standalone tool than a library.

    wget https://github.com/lincoln-b/mortimer/raw/master/target/mortimer-0.1.0-SNAPSHOT-standalone.jar
    java -cp :mortimer-0.1.0-SNAPSHOT-standalone.jar mortimer.main your-file.clj

If your file uses external dependencies, you need to include them in the classpath.

    export LEINCP=`lein classpath`
    java -cp $LEINCP:mortimer.jar mortimer.main your-file.clj

## How It Works

Mortimer analyzes your file using `read-string` and ztellman/riddley's `macroexpand-all`. Then, it walks the source tree with a custom clojure.walk/postwalk extension and adds custom tracing macros around every form in which the first element is a function. After inserting the tracing macros, it simply runs the code using `eval`. 

If and when you find bugs, please report them in the Issues tab and I will try to fix them.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
