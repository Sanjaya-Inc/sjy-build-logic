#!/bin/bash

# A script to automate the installation of sjy-build-logic in a Gradle project.
# This script will add the submodule, update settings.gradle[.kts], create a
# version catalog file, and apply convention plugins to relevant modules.

# Define colors for better output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# --- Helper Functions ---

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# --- Main Logic Functions ---

update_settings_gradle() {
    local settings_file=$1
    log_info "Updating $settings_file..."
    cp "$settings_file" "$settings_file.bak"
    log_info "Backup of $settings_file created at $settings_file.bak"

    # Add includeBuild("sjy-build-logic") to pluginManagement
    if grep -q 'includeBuild("sjy-build-logic")' "$settings_file"; then
        log_info "sjy-build-logic is already included in pluginManagement."
    else
        if grep -q "pluginManagement {" "$settings_file"; then
            log_info "Adding includeBuild to existing pluginManagement block."
            sed -i '' '/pluginManagement {/a\
    includeBuild("sjy-build-logic")
' "$settings_file"
        else
            log_info "No pluginManagement block found. Adding a new one."
            echo "" >> "$settings_file"
            echo "pluginManagement {" >> "$settings_file"
            echo "    includeBuild(\"sjy-build-logic\")" >> "$settings_file"
            echo "}" >> "$settings_file"
        fi
        log_info "Successfully added includeBuild for sjy-build-logic."
    fi

    # Add sjy version catalog
    if grep -q 'create("sjy")' "$settings_file"; then
        log_info "sjy version catalog already exists."
    else
        log_info "Adding sjy version catalog."
        local sjy_catalog_text="        create(\"sjy\") {\n            from(files(\"sjy-build-logic/gradle/libs.versions.toml\"))\n        }"
        if ! grep -q "dependencyResolutionManagement {" "$settings_file"; then
            echo "" >> "$settings_file"
            echo "dependencyResolutionManagement {" >> "$settings_file"
            echo "    versionCatalogs {" >> "$settings_file"
            echo -e "$sjy_catalog_text" >> "$settings_file"
            echo "    }" >> "$settings_file"
            echo "}" >> "$settings_file"
        elif ! grep -q "versionCatalogs {" "$settings_file"; then
            sed -i '' "/dependencyResolutionManagement {/a\\
    versionCatalogs {\\
${sjy_catalog_text}\\
    }
" "$settings_file"
        else
            sed -i '' "/versionCatalogs {/a\\
${sjy_catalog_text}
" "$settings_file"
        fi
        log_info "Successfully added sjy version catalog."
    fi
}


apply_plugins_to_modules() {
    local settings_file=$1
    log_info "Scanning for modules in $settings_file..."
    
    local modules=$(grep -E "include[(]'.+'[)]|include[\"'].+[\"']" "$settings_file" | sed -E "s/include[(]*['\"]://g" | sed "s/['\")]//g" | sed "s/:/\//g")

    if [ -z "$modules" ]; then
        log_warn "No modules found in $settings_file. Skipping plugin application."
        return
    fi
    
    echo -e "${GREEN}Found modules:${NC}"
    echo "$modules"

    for module_path in $modules; do
        log_info "Processing module: $module_path"
        local build_file=""
        if [ -f "$module_path/build.gradle.kts" ]; then
            build_file="$module_path/build.gradle.kts"
        elif [ -f "$module_path/build.gradle" ]; then
            build_file="$module_path/build.gradle"
        fi

        if [ -z "$build_file" ]; then
            log_warn "No build.gradle.kts or build.gradle found for module $module_path. Skipping."
            continue
        fi

        local plugin_id=""
        if grep -q "com.android.application" "$build_file"; then
            plugin_id="com.sanjaya.buildlogic.app"
        elif grep -q "com.android.library" "$build_file"; then
            plugin_id="com.sanjaya.buildlogic.lib"
        else
            log_warn "Module $module_path is not an Android application or library. Skipping."
            continue
        fi

        log_info "Module $module_path is an Android module. Applying plugin: $plugin_id"

        if grep -q "$plugin_id" "$build_file"; then
            log_info "Plugin $plugin_id already applied in $build_file."
        else
            log_info "Applying $plugin_id to $build_file."
            cp "$build_file" "$build_file.bak"
            log_info "Backup created at $build_file.bak"

            if grep -q "plugins {" "$build_file"; then
                sed -i '' "/plugins {/a\\
    id(\"$plugin_id\")
" "$build_file"
            else
                log_warn "No 'plugins {}' block found in $build_file. Adding one at the top."
                local temp_file=$(mktemp)
                echo "plugins {" > "$temp_file"
                echo "    id(\"$plugin_id\")" >> "$temp_file"
                echo "}" >> "$temp_file"
                echo "" >> "$temp_file"
                cat "$build_file" >> "$temp_file"
                mv "$temp_file" "$build_file"
            fi
            log_info "Successfully applied $plugin_id."
        fi
    done
}


# --- Main script execution ---

main() {
    log_info "Starting sjy-build-logic installation..."

    local settings_file=""
    if [ -f "settings.gradle.kts" ]; then
        settings_file="settings.gradle.kts"
    elif [ -f "settings.gradle" ]; then
        settings_file="settings.gradle"
    fi

    if [ -z "$settings_file" ]; then
        log_error "Could not find settings.gradle.kts or settings.gradle in the current directory."
        exit 1
    fi
    log_info "Found settings file: $settings_file"

    update_settings_gradle "$settings_file"
    apply_plugins_to_modules "$settings_file"

    log_info "Installation complete. Please review the changes and run a Gradle sync."
}

main