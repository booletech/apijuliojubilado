@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0collect-k8s-evidence.ps1" %*
