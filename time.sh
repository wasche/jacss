#!/bin/bash
/usr/bin/time java -cp dist/lib/jacss.jar:dist/lib/args4j-2.0.17.jar com.wickedspiral.jacss.JACSS -f -j 4 $@
