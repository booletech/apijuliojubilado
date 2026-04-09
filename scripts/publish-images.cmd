@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0publish-images.ps1" %*
