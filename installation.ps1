# Set script to exit on error
$ErrorActionPreference = "Stop"

# A script to automate the installation of sjy-build-logic in a Gradle project.
# This script will add the submodule, update settings.gradle[.kts], create a
# version catalog file, and apply convention plugins to relevant modules.

# --- Helper Functions for Logging ---

function Log-Info {
    param ([string]$message)
    Write-Host "[INFO] $message" -ForegroundColor Green
}

function Log-Warn {
    param ([string]$message)
    Write-Host "[WARN] $message" -ForegroundColor Yellow
}

function Log-Error {
    param ([string]$message)
    Write-Host "[ERROR] $message" -ForegroundColor Red
}

# --- Main Logic Functions ---

function Update-SettingsGradle {
    param ([string]$settingsFile)
    Log-Info "Updating $settingsFile..."
    Copy-Item -Path $settingsFile -Destination "$settingsFile.bak" -Force
    Log-Info "Backup of $settingsFile created at $settingsFile.bak"

    $content = Get-Content $settingsFile -Raw

    if ($content -match 'includeBuild\("sjy-build-logic"\)') {
        Log-Info "sjy-build-logic is already included in pluginManagement."
    } else {
        if ($content -match "pluginManagement {") {
            Log-Info "Adding includeBuild to existing pluginManagement block."
            $content = $content -replace "(pluginManagement\s*\{)", "`$1`n    includeBuild(\""sjy-build-logic\"")"
        } else {
            Log-Info "No pluginManagement block found. Adding a new one."
            $content += "`npluginManagement {`n    includeBuild(\""sjy-build-logic\"")`n}"
        }
        Log-Info "Successfully added includeBuild for sjy-build-logic."
    }
    
    $content | Set-Content $settingsFile
}

function Update-LibsVersionsToml {
    $tomlFile = "gradle/libs.versions.toml"
    Log-Info "Updating $tomlFile..."
    
    if (-not (Test-Path $tomlFile)) {
        Log-Error "$tomlFile not found. Please ensure your project has a version catalog."
        exit 1
    }
    
    Copy-Item -Path $tomlFile -Destination "$tomlFile.bak" -Force
    Log-Info "Backup created: $tomlFile.bak"
    
    $content = Get-Content $tomlFile -Raw
    $needsVersions = $false
    $needsPlugins = $false
    
    if ($content -notmatch "compile-sdk.*=") {
        $needsVersions = $true
    }
    
    if ($content -notmatch "sjy-detekt.*=") {
        $needsPlugins = $true
    }
    
    if ($needsVersions) {
        Log-Info "Adding compile-sdk and min-sdk versions..."
        if ($content -match "^\[versions\]") {
            $content = $content -replace "(^\[versions\])", "`$1`ncompile-sdk = \""35\""`nmin-sdk = \""24\"""
        } else {
            $content = "[versions]`ncompile-sdk = \""35\""`nmin-sdk = \""24\""`n`n" + $content
        }
    }
    
    if ($needsPlugins) {
        Log-Info "Adding sjy plugin definitions..."
        $sjyPlugins = @"
sjy-detekt = { id = "com.sanjaya.buildlogic.detekt" }
sjy-lib = { id = "com.sanjaya.buildlogic.lib" }
sjy-compose = { id = "com.sanjaya.buildlogic.compose" }
sjy-app = { id = "com.sanjaya.buildlogic.app" }
sjy-firebase = { id = "com.sanjaya.buildlogic.firebase" }
"@
        if ($content -match "^\[plugins\]") {
            $content = $content -replace "(^\[plugins\])", "`$1`n$sjyPlugins"
        } else {
            $content += "`n[plugins]`n$sjyPlugins"
        }
    }
    
    $content | Set-Content $tomlFile
    Log-Info "libs.versions.toml updated successfully."
}

function Update-RootBuildGradle {
    $buildFile = ""
    if (Test-Path "build.gradle.kts") {
        $buildFile = "build.gradle.kts"
    } elseif (Test-Path "build.gradle") {
        $buildFile = "build.gradle"
    } else {
        Log-Warn "No root build.gradle(.kts) found. Skipping root plugin configuration."
        return
    }
    
    Log-Info "Updating root $buildFile..."
    Copy-Item -Path $buildFile -Destination "$buildFile.bak" -Force
    Log-Info "Backup created: $buildFile.bak"
    
    $content = Get-Content $buildFile -Raw
    
    if ($content -match "alias\(libs\.plugins\.sjy\.detekt\)") {
        Log-Info "Sjy plugin aliases already present in root build file."
        return
    }
    
    $pluginAliases = @"
    alias(sjy.plugins.android.application) apply false
    alias(sjy.plugins.android.library) apply false
    alias(sjy.plugins.kotlin.android) apply false
    alias(sjy.plugins.kotlin.compose) apply false
    alias(sjy.plugins.ksp) apply false
    alias(sjy.plugins.detekt) apply true
    alias(sjy.plugins.ktorfit) apply false
    alias(sjy.plugins.gms.services) apply false
    alias(sjy.plugins.crashlytics) apply false
    alias(sjy.plugins.lumo) apply false
    alias(libs.plugins.sjy.detekt) apply true
"@
    
    if ($content -match "plugins \{") {
        $content = $content -replace "(plugins\s*\{)", "`$1`n$pluginAliases"
        Log-Info "Added sjy plugin aliases to existing plugins block."
    } else {
        $content = "plugins {`n$pluginAliases`n}`n`n" + $content
        Log-Info "Created plugins block with sjy plugin aliases."
    }
    
    $content | Set-Content $buildFile
}

function Apply-PluginsToModules {
    param ([string]$settingsFile)
    Log-Info "Scanning for modules in $settingsFile..."

    $modules = Get-Content $settingsFile |
        Select-String -Pattern "include[(]'.+'[)]|include[\"'].+[\"']" |
        ForEach-Object {
            $_.ToString().Trim() -replace "include[(]*['\"]:", "" -replace "['\")]", "" -replace ":", "/"
        }

    if (-not $modules) {
        Log-Warn "No modules found in $settingsFile. Skipping plugin application."
        return
    }

    Log-Info "Found modules:"
    $modules | ForEach-Object { Write-Host $_ }

    foreach ($modulePath in $modules) {
        Log-Info "Processing module: $modulePath"
        $buildFile = ""
        if (Test-Path "$modulePath\build.gradle.kts") {
            $buildFile = "$modulePath\build.gradle.kts"
        } elseif (Test-Path "$modulePath\build.gradle") {
            $buildFile = "$modulePath\build.gradle"
        }

        if (-not $buildFile) {
            Log-Warn "No build.gradle.kts or build.gradle found for module $modulePath. Skipping."
            continue
        }

        $buildFileContent = Get-Content $buildFile
        $pluginAlias = ""
        $rawText = [string]::Join("`n", $buildFileContent)

        if ($rawText -match "com\.android\.application") {
            $pluginAlias = "alias(libs.plugins.sjy.app)"
        } elseif ($rawText -match "com\.android\.library") {
            $pluginAlias = "alias(libs.plugins.sjy.lib)"
        } else {
            Log-Warn "Module $modulePath is not an Android application or library. Skipping."
            continue
        }

        if ($rawText -match "alias\(libs\.plugins\.sjy") {
            Log-Info "Sjy plugin alias already applied in $buildFile."
            continue
        }

        Log-Info "Adding sjy plugin alias to $buildFile"
        Copy-Item -Path $buildFile -Destination "$buildFile.bak" -Force

        $content = Get-Content $buildFile -Raw

        if ($content -match "plugins \{") {
            $content = $content -replace "(plugins\s*\{)", "`$1`n    $pluginAlias"
            Log-Info "Added $pluginAlias to existing plugins block."
        } else {
            $content = "plugins {`n    $pluginAlias`n}`n`n" + $content
            Log-Info "Created plugins block with $pluginAlias."
        }

        $content | Set-Content $buildFile
    }
}

# --- Main script execution ---

function Main {
    Log-Info "Starting sjy-build-logic installation..."

    $settingsFile = ""
    if (Test-Path "settings.gradle.kts") {
        $settingsFile = "settings.gradle.kts"
    } elseif (Test-Path "settings.gradle") {
        $settingsFile = "settings.gradle"
    }

    if (-not $settingsFile) {
        Log-Error "Could not find settings.gradle.kts or settings.gradle in the current directory."
        exit 1
    }
    Log-Info "Found settings file: $settingsFile"
    
    Update-SettingsGradle -settingsFile $settingsFile
    Update-LibsVersionsToml
    Update-RootBuildGradle
    Apply-PluginsToModules -settingsFile $settingsFile
    
    Log-Info "Installation complete. Please review the changes and run a Gradle sync."
}

Main