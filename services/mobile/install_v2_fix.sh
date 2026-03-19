#!/bin/bash
set -euo pipefail

# CRITICAL FIX: Ensure we are building the Desktop version, not the Documents version
# The user is working in "Desktop/SIGA/SIGA-APP"
# We define the path explicitly or assume we are executing from the correct dir
# But to be safe, we print where we are.

echo "📂 Working Directory: $(pwd)"
echo "🚀 Starting Clean Install for SIGA v2.0 (Desktop Version)"

export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
export ANDROID_HOME="/c/Users/hdagu/AppData/Local/Android/Sdk"
export PATH="$JAVA_HOME/bin:$PATH"

# Force permissions on gradlew just in case
chmod +x gradlew

# Run CLEAN + UNINSTALL + INSTALL
# adding stacktrace to see errors if any
./gradlew clean uninstallDebug installDebug --stacktrace

echo "✅ App Installed! Please check your phone."
