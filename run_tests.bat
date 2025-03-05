@echo off
echo Running Distributed Rate Limiter Tests...
echo ----------------------------------------

REM Run Maven tests
call mvn clean test

REM Check if tests passed
if %ERRORLEVEL% EQU 0 (
    echo Tests passed successfully!
) else (
    echo Tests failed. Please check the output above for details.
    exit /b 1
) 