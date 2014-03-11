@echo off

set PACKAGE_PATH=%CD%

echo cleaning ...

if exist %PACKAGE_PATH%\bin		rd /Q /S %PACKAGE_PATH%\bin
if exist %PACKAGE_PATH%\gen			rd /Q /S %PACKAGE_PATH%\gen
if exist %PACKAGE_PATH%\out			rd /Q /S %PACKAGE_PATH%\out
if exist %PACKAGE_PATH%\obj			rd /Q /S %PACKAGE_PATH%\obj
pause

	



