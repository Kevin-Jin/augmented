@ECHO off
set CLASSPATH=.;bin;lib\jogg-0.0.7.jar;lib\jorbis-0.0.15.jar;lib\lwjgl.jar;lib\lwjgl_util.jar;lib\slick-util.jar
java -Djava.library.path=lib\native amplified.Launch
pause
