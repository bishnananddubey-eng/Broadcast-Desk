name: Build APK

on:
  push:
    branches: [ main, master ]
  workflow_dispatch: {}

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Check out code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Accept Android SDK licenses
        run: yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses || true

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew || true

      - name: Generate Gradle wrapper if missing
        run: |
          if [ ! -f gradlew ]; then
            sudo apt-get update && sudo apt-get install -y gradle
            gradle wrapper --gradle-version 8.7
            chmod +x gradlew
          fi

      - name: Build debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: BroadcastDesk-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
