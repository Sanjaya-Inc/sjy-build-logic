#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

update_settings_gradle() {
    local settings_file=$1
    log_info "Updating $settings_file..."
    cp "$settings_file" "$settings_file.bak"
    log_info "Backup created: $settings_file.bak"

    if ! grep -q 'includeBuild("sjy-build-logic")' "$settings_file"; then
        if grep -q "pluginManagement {" "$settings_file"; then
            sed -i '' '/pluginManagement {/a\
    includeBuild("sjy-build-logic")
' "$settings_file"
        else
            echo -e "\npluginManagement {\n    includeBuild(\"sjy-build-logic\")\n}" >> "$settings_file"
        fi
        log_info "includeBuild(\"sjy-build-logic\") added to pluginManagement."
    else
        log_info "includeBuild already present in pluginManagement."
    fi
}

update_libs_versions_toml() {
    local toml_file="gradle/libs.versions.toml"
    log_info "Updating $toml_file..."
    
    if [ ! -f "$toml_file" ]; then
        log_error "$toml_file not found. Please ensure your project has a version catalog."
        exit 1
    fi
    
    cp "$toml_file" "$toml_file.bak"
    log_info "Backup created: $toml_file.bak"
    
    local needs_versions=false
    local needs_plugins=false
    
    if ! grep -q "compile-sdk.*=" "$toml_file"; then
        needs_versions=true
    fi
    
    if ! grep -q "sjy-detekt.*=" "$toml_file"; then
        needs_plugins=true
    fi
    
    if [ "$needs_versions" = true ]; then
        log_info "Adding compile-sdk and min-sdk versions..."
        if grep -q "^\[versions\]" "$toml_file"; then
            sed -i '' '/^\[versions\]/a\
compile-sdk = "35"\
min-sdk = "24"
' "$toml_file"
        else
            sed -i '' '1i\
[versions]\
compile-sdk = "35"\
min-sdk = "24"\

' "$toml_file"
        fi
    fi
    
    if [ "$needs_plugins" = true ]; then
        log_info "Adding sjy plugin definitions..."
        if grep -q "^\[plugins\]" "$toml_file"; then
            sed -i '' '/^\[plugins\]/a\
sjy-detekt = { id = "com.sanjaya.buildlogic.detekt" }\
sjy-lib = { id = "com.sanjaya.buildlogic.lib" }\
sjy-compose = { id = "com.sanjaya.buildlogic.compose" }\
sjy-app = { id = "com.sanjaya.buildlogic.app" }\
sjy-firebase = { id = "com.sanjaya.buildlogic.firebase" }
' "$toml_file"
        else
            echo -e "\n[plugins]" >> "$toml_file"
            echo "sjy-detekt = { id = \"com.sanjaya.buildlogic.detekt\" }" >> "$toml_file"
            echo "sjy-lib = { id = \"com.sanjaya.buildlogic.lib\" }" >> "$toml_file"
            echo "sjy-compose = { id = \"com.sanjaya.buildlogic.compose\" }" >> "$toml_file"
            echo "sjy-app = { id = \"com.sanjaya.buildlogic.app\" }" >> "$toml_file"
            echo "sjy-firebase = { id = \"com.sanjaya.buildlogic.firebase\" }" >> "$toml_file"
        fi
    fi
    
    log_info "libs.versions.toml updated successfully."
}

update_root_build_gradle() {
    local build_file=""
    if [ -f "build.gradle.kts" ]; then
        build_file="build.gradle.kts"
    elif [ -f "build.gradle" ]; then
        build_file="build.gradle"
    else
        log_warn "No root build.gradle(.kts) found. Skipping root plugin configuration."
        return
    fi
    
    log_info "Updating root $build_file..."
    cp "$build_file" "$build_file.bak"
    log_info "Backup created: $build_file.bak"
    
    if grep -q "alias(libs.plugins.sjy.detekt)" "$build_file"; then
        log_info "Sjy plugin aliases already present in root build file."
        return
    fi
    
    local plugin_aliases="    alias(sjy.plugins.android.application) apply false
    alias(sjy.plugins.android.library) apply false
    alias(sjy.plugins.kotlin.android) apply false
    alias(sjy.plugins.kotlin.compose) apply false
    alias(sjy.plugins.ksp) apply false
    alias(sjy.plugins.detekt) apply true
    alias(sjy.plugins.ktorfit) apply false
    alias(sjy.plugins.gms.services) apply false
    alias(sjy.plugins.crashlytics) apply false
    alias(sjy.plugins.lumo) apply false
    alias(libs.plugins.sjy.detekt) apply true"
    
    if grep -q "plugins {" "$build_file"; then
        sed -i '' "/plugins {/a\\
$plugin_aliases
" "$build_file"
        log_info "Added sjy plugin aliases to existing plugins block."
    else
        sed -i '' "1i\\
plugins {\\
$plugin_aliases\\
}\\
\\
" "$build_file"
        log_info "Created plugins block with sjy plugin aliases."
    fi
}
apply_plugins_to_modules() {
    local settings_file=$1
    log_info "Scanning modules in $settings_file..."

    local modules=$(grep -E '^include\((.*)\)' "$settings_file" | \
        sed -E 's/include\((.*)\)/\1/' | \
        tr -d '"' | tr -d "'" | tr ':' '\n' | awk NF)

    if [ -z "$modules" ]; then
        log_warn "No modules found."
        return
    fi

    echo -e "${GREEN}Found modules:${NC}"
    echo "$modules"

    for module in $modules; do
        local path="$module"
        log_info "Processing module: $path"

        local build_file=""
        if [ -f "$path/build.gradle.kts" ]; then
            build_file="$path/build.gradle.kts"
        elif [ -f "$path/build.gradle" ]; then
            build_file="$path/build.gradle"
        else
            log_warn "No build file found in $path. Skipping."
            continue
        fi

        local plugin_alias=""
        if grep -q -E "com.android.application|alias\\(.*android\\.application\\)" "$build_file"; then
            plugin_alias="alias(libs.plugins.sjy.app)"
        elif grep -q -E "com.android.library|alias\\(.*android\\.library\\)" "$build_file"; then
            plugin_alias="alias(libs.plugins.sjy.lib)"
        else
            log_warn "Module $path is not an Android app/library. Skipping."
            continue
        fi

        if grep -q "alias(libs.plugins.sjy" "$build_file"; then
            log_info "Sjy plugin alias already applied in $build_file."
            continue
        fi

        log_info "Adding sjy plugin alias to $build_file"
        cp "$build_file" "$build_file.bak"

        if grep -q "plugins {" "$build_file"; then
            sed -i '' "/plugins {/a\\
    $plugin_alias
" "$build_file"
            log_info "Added $plugin_alias to existing plugins block."
        else
            sed -i '' "1i\\
plugins {\\
    $plugin_alias\\
}\\
\\
" "$build_file"
            log_info "Created plugins block with $plugin_alias."
        fi
    done
}

main() {
    log_info "Starting sjy-build-logic installation..."

    local settings_file=""
    if [ -f "settings.gradle.kts" ]; then
        settings_file="settings.gradle.kts"
    elif [ -f "settings.gradle" ]; then
        settings_file="settings.gradle"
    else
        log_error "No settings.gradle(.kts) found."
        exit 1
    fi

    log_info "Using $settings_file"
    update_settings_gradle "$settings_file"
    update_libs_versions_toml
    update_root_build_gradle
    apply_plugins_to_modules "$settings_file"
    log_info "Installation complete. Please sync Gradle."
}

main
