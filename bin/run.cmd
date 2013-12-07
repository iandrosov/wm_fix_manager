SET FIX_HOME=C:\igor\swtwmfixer
SET JAVA_HOME=C:\j2sdk1.4.2_05
SET SAVE_CLASSPATH=%CLASSPATH%
SET CLASSPATH=%JAVA_HOME%\lib;%FIX_HOME%\lib\swt.jar;%FIX_HOME%\lib\client.jar;%FIX_HOME%\lib\xerces.jar;%FIX_HOME%\lib\classes12.jar;%FIX_HOME%\WmFixManager_1.0.jar;.

rem ****** UI Command
rem %JAVA_HOME%\java -classpath %CLASSPATH% -Djava.library.path=%FIX_HOME% WmFixer -cnf %FIX_HOME%\ini.xml -logdir %FIX_HOME% -audit -logtofile
java -Djava.library.path=%FIX_HOME% -jar WmFixManager_1_0.jar -cnf %FIX_HOME%\ini.xml -logdir %FIX_HOME% -audit -logtofile

rem ****** Console command
rem %JAVA_HOME%\bin\java -classpath %CLASSPATH% WmFixer -console -cnf %FIX_HOME%\ini.xml -logdir %FIX_HOME% -audit -logtofile -profile "Test"
rem %JAVA_HOME%\bin\java -jar WmFixManager_1_0.jar -console -cnf %FIX_HOME%\ini.xml -logdir %FIX_HOME% -audit -logtofile -profile "Test"


SET CLASSPATH=%SAVE_CLASSPATH%