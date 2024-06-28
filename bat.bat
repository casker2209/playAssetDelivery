@echo off

rem Set your variables
set GRADLEW_PATH=.\gradlew
set BUNDLETOOL_PATH=.\bundletool.jar
set BUNDLE_FILE=app\build\outputs\bundle\debug\app-debug.aab
set OUTPUT_APKS=apk_debug.apks
set KEYSTORE_FILE=key.jks
set KEYSTORE_PASS=123456
set KEY_ALIAS=key0
set KEY_PASS=123456

rem Step 0: Delete existing APK set if it exists
if exist %OUTPUT_APKS% (
    echo Deleting existing APK set...
    del %OUTPUT_APKS%
)

rem Step 1: Build the debug bundle using Gradle
echo Building debug bundle...
start cmd.exe /c "%GRADLEW_PATH% app:bundleDebug"

rem Wait for the build to complete before proceeding
timeout /t 10

rem Step 2: Build the universal APK set using bundletool
echo Building APK set...
java -jar %BUNDLETOOL_PATH% build-apks ^
    --bundle=%BUNDLE_FILE% ^
    --output=%OUTPUT_APKS% ^
    --ks=%KEYSTORE_FILE% ^
    --ks-pass=pass:%KEYSTORE_PASS% ^
    --ks-key-alias=%KEY_ALIAS% ^
    --key-pass=pass:%KEY_PASS% ^
    --local-testing

rem Wait for the build-apks process to complete
timeout /t 10

rem Step 3: Install the generated APK set on connected device/emulator
echo Installing APK set...
java -jar %BUNDLETOOL_PATH% install-apks --apks=%OUTPUT_APKS%

pause