@ECHO off
set CLASSPATH=.;bin;lib\jogg-0.0.7.jar;lib\jorbis-0.0.15.jar;lib\lwjgl.jar;lib\lwjgl_util.jar;lib\slick-util.jar;lib\stax2-api-3.1.1.jar;lib\stax-api-1.0.1.jar;lib\woodstox-core-lgpl-4.1.5.jar
java -Djava.library.path=lib\native amplified.Launch
pause
