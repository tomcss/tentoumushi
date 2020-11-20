rm Tentoumushi.class

echo COMPILES ALL TUTORIALS SOURCE CODES

javac -classpath "%CLASSPATH%;golden_0_2_3.jar;d:\tools\development\sun\appserver\jdk\jre\lib\plugin.jar;." *.java

cp -f *.class dist
cd dist
zip -r tentou.zip *
mv -f ./tentou.zip ../final

echo RUN TUTORIALS LAUNCHER

#REM java -classpath "%CLASSPATH%;golden_0_2_3.jar;." Tentoumushi

