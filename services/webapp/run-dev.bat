@echo off
setlocal enabledelayedexpansion
set PATH=C:\Users\hdagu\.nodejs\node-v20.11.0-win-x64;%PATH%
cd /d "c:\Users\hdagu\Documents\SIGA_PROTOTIPO"
node.exe --version
npm run dev
