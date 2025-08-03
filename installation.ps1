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

    # Add includeBuild("sjy-build-logic") to pluginManagement
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

    # Add sjy version catalog
    if ($content -match 'create\("sjy"\)') {
        Log-Info "sjy version catalog already exists."
    } else {
        Log-Info "Adding sjy version catalog."
        $sjyCatalogText = "        create(\""sjy\"") {`n            from(files(\""sjy-build-logic/gradle/libs.versions.toml\""))`n        }"
        if ($content -notmatch "dependencyResolutionManagement {") {
            $content += "`ndependencyResolutionManagement {`n    versionCatalogs {`n$sjyCatalogText`n    }`n}"
        } elseif ($content -notmatch "versionCatalogs {") {
             $content = $content -replace "(dependencyResolutionManagement\s*\{)", "`$1`n    versionCatalogs {`n$sjyCatalogText`n    }"
        } else {
            $content = $content -replace "(versionCatalogs\s*\{)", "`$1`n$sjyCatalogText"
        }
        Log-Info "Successfully added sjy version catalog."
    }
    
    $content | Set-Content $settingsFile
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
        $pluginId = ""
        $rawText = [string]::Join("`n", $buildFileContent)

        if ($rawText -match "com\.android\.application") {
            $pluginId = "com.sanjaya.buildlogic.app"
        } elseif ($rawText -match "com\.android\.library") {
            $pluginId = "com.sanjaya.buildlogic.lib"
        } else {
            Log-Warn "Module $modulePath is not an Android application or library. Skipping."
            continue
        }

        if ($rawText -match $pluginId) {
            Log-Info "Plugin $pluginId already applied in $buildFile."
            continue
        }

        Log-Info "Replacing plugins block in $buildFile..."
        Copy-Item -Path $buildFile -Destination "$buildFile.bak" -Force

        $newContent = @()
        $insidePlugins = $false
        $braceDepth = 0
        foreach ($line in $buildFileContent) {
            if (-not $insidePlugins -and $line -match '^\s*plugins\s*\{') {
                $insidePlugins = $true
                $braceDepth = 1
                continue
            }

            if ($insidePlugins) {
                $braceDepth += ($line -split '{').Length - 1
                $braceDepth -= ($line -split '}').Length - 1
                if ($braceDepth -le 0) {
                    $insidePlugins = $false
                }
                continue
            }

            $newContent += $line
        }

        $pluginBlock = @(
            "plugins {",
            "    id(\"$pluginId\")",
            "}",
            ""
        )

        $finalContent = $pluginBlock + $newContent
        $finalContent | Set-Content $buildFile
        Log-Info "Successfully replaced plugins block in $buildFile."
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
    Apply-PluginsToModules -settingsFile $settingsFile
    
    Log-Info "Installation complete. Please review the changes and run a Gradle sync."
}

Main