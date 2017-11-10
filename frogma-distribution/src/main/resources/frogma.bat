set JAVA_OPTIONS=-Dsun.java2d.dpiaware=false
set JAVA_CLASSPATH=-cp ${frogma.classpath}
java %JAVA_OPTIONS% %JAVA_CLASSPATH% frogma.GameEngineImpl
pause
