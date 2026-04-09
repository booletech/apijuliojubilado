@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0run-stress-test.ps1" %*
