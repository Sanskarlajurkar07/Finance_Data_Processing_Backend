$ErrorActionPreference = "Stop"

$JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$MAVEN_WRAPPER_JAR = ".mvn\wrapper\maven-wrapper.jar"
$MAVEN_WRAPPER_PROPERTIES = ".mvn\wrapper\maven-wrapper.properties"

Write-Host "Starting Spring Boot Application..." -ForegroundColor Green
Write-Host ""

# Check if wrapper jar exists, if not download it
if (-not (Test-Path $MAVEN_WRAPPER_JAR)) {
    Write-Host "Downloading Maven Wrapper..." -ForegroundColor Yellow
    $wrapperUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $MAVEN_WRAPPER_JAR
}

# Run Maven with Spring Boot
& "$JAVA_HOME\bin\java.exe" `
    -classpath $MAVEN_WRAPPER_JAR `
    "-Dmaven.multiModuleProjectDirectory=$PWD" `
    org.apache.maven.wrapper.MavenWrapperMain `
    spring-boot:run
