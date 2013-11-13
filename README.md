# jacss [![Build Status](https://travis-ci.org/wasche/jacss.png)](https://travis-ci.org/wasche/jacss) [![Still Maintained](http://stillmaintained.com/wasche/jacss.png)](http://stillmaintained.com/wasche/jacss)

Java port of [ncss]. It is a streaming compressor for CSS, but unlike other CSS
minifiers it works on a stream and is optimized for speed.

[ncss]: http://github.com/wasche/ncss

# Usage

```
Usage: java -jar JACSS-<version>.jar [options] [files]

Options:

 --compat-yui242             : Match compatibility with YUI 2.4.2
 --help                      : Show this help text.
 --keep-trailing-semicolons  : Do not strip semicolons on last style of a rule
 --no-collapse-none          : Do not collapse none to 0
 --no-collapse-zeroes        : Do not drop leading zero in floats less than 1
 --version                   : Show version information.
 -O (--stdout)               : Print to stdout instead of to file
 -d (--debug)                : Print additional debugging information
 -f (--force)                : Force re-compression
 -j (--threads) THREADS      : Number of threads to use (default: 1)
 -r (--regex-from) REGEXFROM : Regex to replace with REGEXTO in new file names
                               (default: -gen.css$)
 -t (--regex-to) REGEXTO     : Regex to replace REGEXFROM with, uses Java's
                               Matcher.replace (default: -c.css)
 -v (--verbose)              : Print debugging information
```

# Running Tests

To run the test suite, simply use the ant target:

```ant test```

## Running a Single Test

```java -cp build:lib/args4j-2.0.17.jar:lib/guava-15.0.jar com.wickedspiral.jacss.JACSS -O -d < tests/string-in-comment.css```

Will output the test result to the console, along with debugging information.


# License

(The MIT License)

Copyright (c) 20011-2013 Wil Asche <wil@asche.us>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
