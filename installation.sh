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

    # Add includeBuild
    if ! grep -q 'includeBuild("sjy-build-logic")' "$settings_file"; then
        if grep -q "pluginManagement {" "$settings_file"; then
            sed -i '' '/pluginManagement {/a\
    includeBuild("sjy-build-logic")
' "$settings_file"
        else
            echo -e "\npluginManagement {\n    includeBuild(\"sjy-build-logic\")\n}" >> "$settings_file"
        fi
        log_info "includeBuild(\"sjy-build-logic\") added."
    else
        log_info "includeBuild already present."
    fi

    # Add versionCatalogs block safely
    if grep -q 'create("sjy")' "$settings_file"; then
        log_info "Version catalog 'sjy' already exists."
        return
    fi

    log_info "Adding version catalog 'sjy'..."
    sjy_catalog_block=$(
        cat <<EOF
        create("sjy") {
            from(files("sjy-build-logic/gradle/libs.versions.toml"))
        }
EOF
    )

    if ! grep -q "dependencyResolutionManagement {" "$settings_file"; then
        cat <<EOF >> "$settings_file"

dependencyResolutionManagement {
    versionCatalogs {
$sjy_catalog_block
    }
}
EOF
        log_info "dependencyResolutionManagement + versionCatalogs block added."
    elif ! grep -q "versionCatalogs {" "$settings_file"; then
        tmpfile=$(mktemp)
        while IFS= read -r line; do
            echo "$line" >> "$tmpfile"
            if [[ "$line" =~ dependencyResolutionManagement[\ ]*\{ ]]; then
                echo "    versionCatalogs {" >> "$tmpfile"
                echo "$sjy_catalog_block" >> "$tmpfile"
                echo "    }" >> "$tmpfile"
            fi
        done < "$settings_file"
        mv "$tmpfile" "$settings_file"
        log_info "versionCatalogs block inserted."
    else
        tmpfile=$(mktemp)
        while IFS= read -r line; do
            echo "$line" >> "$tmpfile"
            if [[ "$line" =~ versionCatalogs[\ ]*\{ ]]; then
                echo "$sjy_catalog_block" >> "$tmpfile"
            fi
        done < "$settings_file"
        mv "$tmpfile" "$settings_file"
        log_info "Inserted create(\"sjy\") into versionCatalogs block."
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

        local plugin_id=""
        if grep -q -E "com.android.application|alias\\(.*android\\.application\\)" "$build_file"; then
            plugin_id="com.sanjaya.buildlogic.app"
        elif grep -q -E "com.android.library|alias\\(.*android\\.library\\)" "$build_file"; then
            plugin_id="com.sanjaya.buildlogic.lib"
        else
            log_warn "Module $path is not an Android app/library. Skipping."
            continue
        fi

        if grep -q "$plugin_id" "$build_file"; then
            log_info "$plugin_id already applied."
            continue
        fi

        log_info "Replacing plugins block with: $plugin_id"
        cp "$build_file" "$build_file.bak"

        # Use awk to remove entire plugins block and save to temp
        awk '
        BEGIN { in_plugins = 0; brace_level = 0 }
        /^\s*plugins\s*\{/ {
            in_plugins = 1
            brace_level = 1
            next
        }
        in_plugins {
            brace_level += gsub(/\{/, "{")
            brace_level -= gsub(/\}/, "}")
            if (brace_level == 0) {
                in_plugins = 0
            }
            next
        }
        !in_plugins { print }
        ' "$build_file.bak" > "$build_file.cleaned"

        # Inject the correct plugins block at the top
        {
            echo "plugins {"
            echo "    id(\"$plugin_id\")"
            echo "}"
            echo ""
            cat "$build_file.cleaned"
        } > "$build_file"

        rm "$build_file.cleaned"
        log_info "Plugin block replaced in $build_file"
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
    apply_plugins_to_modules "$settings_file"
    log_info "Installation complete. Please sync Gradle."
}

main
