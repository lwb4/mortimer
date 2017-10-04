# mortimer

This is a tool that can trace every function call in a Clojure file. It is the spiritual successor to [rage](https://github.com/lincoln-b/rage), and another small step in the general direction of building better debugging tools for the Clojure ecosystem.

Simply put, you give mortimer a file, and it runs it with a trace in every function call of every form, including inner forms. It's sort of similar to clojure.tools.trace but much more pervasive.

Mortimer is useful when a) you're trying to solve programming problems on 4clojure.com, b) you're trying to understand your code in greater depth, or c) you're trying to understand someone else's code by tracing it's execution.

Special thanks to the many people who answered my questions on Clojurians slack, as well as Bill Piel's [Sayid](https://github.com/bpiel/sayid) which was the initial inspiration for this tool.

## Usage

Mortimer works better as a standalone tool than a library.

    wget https://github.com/lincoln-b/mortimer/raw/master/target/mortimer-0.1.0-SNAPSHOT-standalone.jar
    java -cp :mortimer-0.1.0-SNAPSHOT-standalone.jar mortimer.main your-file.clj

If your file uses external dependencies, you need to include them in the classpath.

    export LEINCP=`lein classpath`
    java -cp $LEINCP:mortimer.jar mortimer.main your-file.clj

For example, if your-file.clj contains this code:

    (#(reduce * (drop 1 (range (inc %)))) 5)

The output will look like this:

    Running file test.clj ...
    TRACED:  (. clojure.lang.Numbers (inc p1__1#)) => 6
    TRACED:  (range (. clojure.lang.Numbers (inc p1__1#))) => (0 1 2 3 4 5)
    TRACED:  (drop 1 (range (. clojure.lang.Numbers (inc p1__1#)))) => clojure.lang.LazySeq@1c3e4a2
    TRACED:  (reduce * (drop 1 (range (. clojure.lang.Numbers (inc p1__1#))))) => 120

## How It Works

Mortimer analyzes your file using `read-string` and ztellman/riddley's `macroexpand-all`. Then, it walks the source tree with a custom clojure.walk/postwalk extension and adds custom tracing macros around every form in which the first element is a function. After inserting the tracing macros, it simply runs the code using `eval`. 

If and when you find bugs, please report them in the Issues tab and I will try to fix them.

## License

Copyright © 2017 Lincoln-B

Distributed under the Eclipse Public License version 1.0.
