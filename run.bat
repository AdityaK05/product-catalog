@echo off
echo Compiling ProductCatalog...
if not exist out mkdir out
javac --release 8 -cp "lib\*" ProductCatalog.java -d out 2>&1
if errorlevel 1 ( echo Compile FAILED & pause & exit /b 1 )
echo Starting HoldIt Store at http://localhost:8080 ...
java -cp "out;lib\*" ProductCatalog
