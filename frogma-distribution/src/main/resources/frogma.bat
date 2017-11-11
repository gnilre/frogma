set JAVA_OPTIONS=-Dsun.java2d.dpiaware=false -Dsun.java2d.translaccel=true -Dsun.java2d.accthreshold=0
set JAVA_CLASSPATH=-cp ${frogma.classpath}
java %JAVA_OPTIONS% %JAVA_CLASSPATH% frogma.GameEngineImpl
pause
