@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo ============================================================
echo   Padron Mascota - crear el programa .exe para Windows
echo ============================================================
echo.

rem --- 1. Buscar un JDK 21 (trae la herramienta jpackage) ---
rem     Orden: JAVA_HOME, los JDK que descarga IntelliJ (%USERPROFILE%\.jdks), Program Files
set "JDK="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\jpackage.exe" set "JDK=%JAVA_HOME%"
if not defined JDK for /d %%D in ("%USERPROFILE%\.jdks\*21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK for /d %%D in ("%ProgramFiles%\Java\jdk-21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK (
    echo [ERROR] No se encontro un JDK 21.
    echo En IntelliJ mira la carpeta en: File ^> Project Structure ^> SDK
    echo y ejecuta antes:  set JAVA_HOME=esa_carpeta
    pause
    exit /b 1
)
set "JAVA_HOME=%JDK%"
set "PATH=%JDK%\bin;%PATH%"
echo Usando el JDK: %JDK%
echo.

rem --- 2. Compilar y juntar las librerias (Maven se descarga solo) ---
echo [1/3] Compilando el proyecto...
call mvnw.cmd -q -B clean package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target\app
if errorlevel 1 (
    echo [ERROR] No se pudo compilar. Revisa los mensajes de arriba.
    pause
    exit /b 1
)
copy /y target\PadronMascota-1.0.jar target\app\ >nul

rem --- 3. Crear el programa con su propio Java incluido ---
echo [2/3] Creando PadronMascota.exe ^(tarda un par de minutos^)...
if exist dist rmdir /s /q dist
jpackage --type app-image ^
  --name PadronMascota ^
  --app-version 1.0 ^
  --vendor "Padron Mascota" ^
  --icon instalador\icono.ico ^
  --input target\app ^
  --main-jar PadronMascota-1.0.jar ^
  --main-class pe.edu.upeu.padronmascota.padronmascota.Main ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --dest dist
if errorlevel 1 (
    echo [ERROR] jpackage fallo. Revisa los mensajes de arriba.
    pause
    exit /b 1
)

rem --- 4. Copiar la base de datos y los idiomas junto al .exe ---
echo [3/3] Copiando base de datos e idiomas...
xcopy /e /i /y data dist\PadronMascota\data >nul
xcopy /e /i /y language dist\PadronMascota\language >nul

echo.
echo ============================================================
echo   LISTO
echo   Programa: dist\PadronMascota\PadronMascota.exe
echo   Para llevarlo a otra PC copia TODA la carpeta dist\PadronMascota
echo   (la otra PC no necesita tener Java instalado).
echo ============================================================
explorer dist\PadronMascota
pause
