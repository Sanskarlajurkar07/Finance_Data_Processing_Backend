@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home (optional)
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF "%MAVEN_PROJECTBASEDIR%"=="" (
set MAVEN_PROJECTBASEDIR=%CD%
)

set WRAPPER_PROPERTIES_PATH=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties
if exist "%WRAPPER_PROPERTIES_PATH%" (
    for /f "usebackq delims=" %%A in ("%WRAPPER_PROPERTIES_PATH%") do (
        if "%%A" not equ "" (
            setlocal enabledelayedexpansion
            set "propLine=%%A"
            if defined propLine (
                for /f "tokens=1,* delims==" %%B in ("!propLine!") do (
                    set "propName=%%B"
                    set "propValue=!propLine:*=!"
                    if "!propName!"=="distributionUrl" (
                        set "DISTRIBUTION_URL=!propValue!"
                    )
                    if "!propName!"=="wrapperUrl" (
                        set "WRAPPER_URL=!propValue!"
                    )
                )
            )
            endlocal
        )
    )
)

if "%WRAPPER_URL%"=="" (
    set "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
)

if "%DISTRIBUTION_URL%"=="" (
    set "DISTRIBUTION_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.5/apache-maven-3.9.5-bin.zip"
)

set WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL=%DISTRIBUTION_URL%
set WRAPPER_JAR_LOCATION=%WRAPPER_JAR%

powershell -Command "&{"^
    "$webclient = new-object System.Net.WebClient;"^
    "if (-not (Test-Path '%WRAPPER_JAR%')) {"^
        "Write-Host 'Downloading from: ' '%DOWNLOAD_URL%'"^
        "$webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR_LOCATION%')"^
        "Write-Host 'Downloaded: ' '%WRAPPER_JAR%'"^
    "}"^
"}"
if "%ERRORLEVEL%" equ "0" goto execute

echo Couldn't download %DOWNLOAD_URL%, retrying ...
powershell -Command "&{"^
    "$webclient = new-object System.Net.WebClient;"^
    "$webclient.Proxy = [System.Net.GlobalProxySelection]::GetEmptyWebProxy();"^
    "if (-not (Test-Path '%WRAPPER_JAR%')) {"^
        "Write-Host 'Downloading from: ' '%DOWNLOAD_URL%'"^
        "$webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR_LOCATION%')"^
        "Write-Host 'Downloaded: ' '%WRAPPER_JAR%'"^
    "}"^
"}"
if "%ERRORLEVEL%" equ "0" goto execute

echo Download failed, exiting batch script.
exit /b 1

:execute
@REM Couldn't find .exe, so try to launch with Java if available
if exist "%JAVA_HOME%\bin\java.exe" (
    "%JAVA_HOME%\bin\java.exe" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
    if "%ERRORLEVEL%" neq "0" goto error
    goto end
)

echo.
echo Error: JAVA_HOME is not defined correctly for Maven to run.  >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, e.g. hooks, .mavenrc or something similar may have been specified in .mavenrc
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"
:skipRcPost

@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRc
@REM check for pre script, e.g. hooks, .mavenrc or something similar may have been specified in .mavenrc
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"
:skipRc

exit /b %ERROR_CODE%
