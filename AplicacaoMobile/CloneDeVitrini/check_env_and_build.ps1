# Check environment and attempt to build Android debug APK
# Usage: powershell -ExecutionPolicy Bypass -File .\check_env_and_build.ps1

$projectRoot = (Get-Location).Path
Write-Host "Project root: $projectRoot"

# Check JAVA_HOME
if ($env:JAVA_HOME) {
    Write-Host "JAVA_HOME = $env:JAVA_HOME"
    $javaCmd = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (Test-Path $javaCmd) {
        Write-Host "Found java at $javaCmd"
        & $javaCmd -version 2>&1 | ForEach-Object { Write-Host $_ }
        $javaFound = $true
    } else {
        Write-Host "JAVA_HOME set but java executable not found at $javaCmd"
        $javaFound = $false
    }
} else {
    Write-Host "JAVA_HOME is not set. Checking PATH for java..."
    try { $javaVersion = & java -version 2>&1; $javaFound = $true; $javaVersion | ForEach-Object { Write-Host $_ } } catch { $javaFound = $false }
}

# Check Android SDK
if ($env:ANDROID_SDK_ROOT) { Write-Host "ANDROID_SDK_ROOT = $env:ANDROID_SDK_ROOT" } elseif ($env:ANDROID_HOME) { Write-Host "ANDROID_HOME = $env:ANDROID_HOME" } else { Write-Host "Android SDK not configured (ANDROID_SDK_ROOT/ANDROID_HOME not set)." }

# Locate gradlew
$gradlew = Join-Path $projectRoot "gradlew.bat"
if (Test-Path $gradlew) { Write-Host "Found gradlew: $gradlew" } else { Write-Host "gradlew.bat not found in project root." }

# Quick fail checks
if (-not $javaFound) {
    Write-Host "\nERROR: Java not found. Install JDK and set JAVA_HOME to your JDK path, then re-open the terminal." -ForegroundColor Red
    exit 2
}

if (-not (Test-Path $gradlew) -and -not (Get-Command gradle -ErrorAction SilentlyContinue)) {
    Write-Host "\nERROR: Neither gradlew.bat nor gradle command found. Ensure project contains gradlew or install Gradle." -ForegroundColor Red
    exit 3
}

# Run build
if (Test-Path $gradlew) {
    Write-Host "\nRunning gradlew assembleDebug... (this may take a while)" -ForegroundColor Cyan
    & $gradlew assembleDebug --no-daemon --console=plain
    $rc = $LASTEXITCODE
} else {
    Write-Host "\nRunning gradle assembleDebug..." -ForegroundColor Cyan
    gradle assembleDebug --console=plain
    $rc = $LASTEXITCODE
}

Write-Host "\nBuild finished with exit code $rc"
exit $rc
