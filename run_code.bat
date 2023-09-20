@echo off
rmdir /s /q "out"
javac "@options"
java "@runargs"
pause