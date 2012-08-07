@ECHO off
set CLASSPATH=.;bin;lib/*
java -Djava.library.path=lib/native amplified.Launch
pause
