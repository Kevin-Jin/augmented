@ECHO off
set CLASSPATH=.;bin;lib/*
java -Djava.library.path=lib/native op_lando.Launch
pause
