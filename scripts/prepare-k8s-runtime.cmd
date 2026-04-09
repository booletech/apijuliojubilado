@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0prepare-k8s-runtime.ps1" %*
