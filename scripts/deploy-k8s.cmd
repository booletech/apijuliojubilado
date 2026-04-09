@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0deploy-k8s.ps1" %*
