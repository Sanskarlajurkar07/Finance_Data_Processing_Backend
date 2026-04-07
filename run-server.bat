@echo off
setlocal

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting Spring Boot Application...
echo.

"%JAVA_HOME%\bin\java.exe" -jar .mvn\wrapper\maven-wrapper.jar -Dmaven.multiModuleProjectDirectory="%CD%" org.apache.maven.wrapper.MavenWrapperMain spring-boot:run

endlocal
