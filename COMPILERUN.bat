del Tentoumushi.class

@echo off
ECHO COMPILES ALL TUTORIALS SOURCE CODES
@echo on

javac -classpath "%CLASSPATH%;golden_0_2_3.jar;c:\tools\java\lib\plugin.jar;." *.java

@echo off
ECHO '
ECHO RUN TUTORIALS LAUNCHER
@echo on

REM java -classpath "%CLASSPATH%;golden_0_2_3.jar;." Tentoumushi

pause
