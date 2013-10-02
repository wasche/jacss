# jacss [![Build Status](https://travis-ci.org/kurakin/jacss.png)](https://travis-ci.org/kurakin/jacss) [![Still Maintained](http://stillmaintained.com/kurakin/jacss.png)](http://stillmaintained.com/kurakin/jacss)

Java port of [ncss].

[ncss]: http://github.com/kurakin/ncss

# Testing

## Running a Single Test

```java -cp build:lib/args4j-2.0.17.jar:lib/guava-15.0.jar com.wickedspiral.jacss.JACSS -O -d < tests/string-in-comment.css```

Will output the test result to the console, along with debugging information.