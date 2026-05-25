package core.presentation.glass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassUIShowcase() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Preset Configurations") {
            PresetConfigurationsShowcase()
        }

        ShowcaseSection("Glass Buttons") {
            GlassButtonsShowcase()
        }

        ShowcaseSection("Glass Cards") {
            GlassCardsShowcase()
        }

        ShowcaseSection("Glass Surfaces") {
            GlassSurfacesShowcase()
        }

        ShowcaseSection("Custom Configurations") {
            CustomConfigurationsShowcase()
        }

        ShowcaseSection("Interactive Components") {
            InteractiveComponentsShowcase()
        }

        ShowcaseSection("Shadow Variants") {
            ShadowVariantsShowcase()
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()
    }
}

@Composable
private fun PresetConfigurationsShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassCard(
            config = GlassConfig.Light,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }

        GlassCard(
            config = GlassConfig.Default,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }

        GlassCard(
            config = GlassConfig.Heavy,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }

        GlassCard(
            config = GlassConfig.Frosted,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }

        GlassCard(
            config = GlassConfig.Dark,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }
    }
}

@Composable
private fun GlassButtonsShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassButton(
            onClick = {},
            config = GlassConfig.Default
        ) {
        }

        GlassButtonWithShadow(
            onClick = {},
            config = GlassConfig.Heavy
        ) {
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassIconButton(
                onClick = {},
                config = GlassConfig.Default
            ) {
            }

            GlassIconButton(
                onClick = {},
                config = GlassConfig.Frosted
            ) {
            }

            GlassIconButton(
                onClick = {},
                config = GlassConfig.Heavy.copy(tintColor = Color(0xFF6200EE))
            ) {
            }
        }

        GlassButton(
            onClick = {},
            enabled = false
        ) {
        }
    }
}

@Composable
private fun GlassCardsShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassCard(
            config = GlassConfig.Light
        ) {
        }

        GlassCardWithShadow(
            config = GlassConfig.Default
        ) {
        }

        GlassElevatedCard(
            config = GlassConfig.Heavy
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        GlassClickableCard(
            onClick = {},
            config = GlassConfig.Frosted
        ) {
        }
    }
}

@Composable
private fun GlassSurfacesShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassAppBar(
            config = GlassConfig.Light
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(onClick = {}) {
                }
            }
        }

        GlassNavigationBar(
            config = GlassConfig.Light
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) {
                    GlassIconButton(onClick = {}) {
                    }
                }
            }
        }

        GlassDialog(
            config = GlassConfig.Heavy
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CustomConfigurationsShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassCard(
            config = GlassConfig(
                tintColor = Color(0xFF00D9C5),
                tintAlpha = GlassConfig.AlphaGradient(0.5f, 0.4f, 0.3f)
            )
        ) {
        }

        GlassCard(
            config = GlassConfig(
                tintColor = Color(0xFF7C4DFF),
                tintAlpha = GlassConfig.AlphaGradient(0.85f, 0.80f, 0.75f),
                enableNoise = true,
                noiseIntensity = 0.2f
            )
        ) {
        }

        GlassCard(
            config = GlassConfig(
                tintColor = Color(0xFFFF6B6B),
                tintAlpha = GlassConfig.AlphaGradient(0.7f, 0.5f, 0.3f),
                borderAlpha = GlassConfig.AlphaGradient(0.5f, 0.3f, 0.1f),
                glossAlpha = 0.2f
            )
        ) {
        }
    }
}

@Composable
private fun InteractiveComponentsShowcase() {
    var toggleState by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassToggleButton(
            checked = toggleState,
            onCheckedChange = { toggleState = it },
            checkedConfig = GlassConfig.Heavy.copy(tintColor = Color(0xFF6200EE)),
            uncheckedConfig = GlassConfig.Light
        ) { checked ->
        }

        GlassButton(
            onClick = {},
            config = GlassConfig.Default
        ) {
        }

        GlassClickableCard(
            onClick = {},
            config = GlassConfig.Frosted
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            }
        }
    }
}

@Composable
private fun ShadowVariantsShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCardWithShadow(
            config = GlassConfig.Default,
            shadowConfig = ShadowConfig.Subtle
        ) {
        }

        GlassCardWithShadow(
            config = GlassConfig.Default,
            shadowConfig = ShadowConfig.Default
        ) {
        }

        GlassCardWithShadow(
            config = GlassConfig.Heavy,
            shadowConfig = ShadowConfig.Elevated
        ) {
        }

        GlassElevatedCard(
            config = GlassConfig.Frosted,
            dropShadowConfig = ShadowConfig.Elevated,
            innerShadowConfig = InnerShadowConfig.Prominent
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ThemedGlassShowcase() {
    val customTheme = GlassTheme(
        defaultConfig = GlassConfig.Light,
        primaryConfig = GlassConfig.Heavy.copy(tintColor = Color(0xFF6200EE)),
        surfaceConfig = GlassConfig.Default,
        errorConfig = GlassConfig.Heavy.copy(tintColor = Color(0xFFB00020))
    )

    ProvideGlassTheme(theme = customTheme) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassButton(onClick = {}) {
            }

            GlassButton(
                onClick = {},
                config = LocalGlassTheme.current.primaryConfig
            ) {
            }

            GlassCard {
            }
        }
    }
}

@Composable
fun AdvancedGlassCompositions() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassButtonWithShadow(
            onClick = {},
            config = GlassConfig.Heavy.copy(tintColor = Color(0xFF03DAC6)),
            shape = CircleShape,
            shadowConfig = ShadowConfig.Elevated,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
        }

        GlassElevatedCard(
            config = GlassConfig.Frosted.copy(tintColor = Color(0xFF6200EE)),
            dropShadowConfig = ShadowConfig.Elevated
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        GlassCard(
            config = GlassConfig.Light
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    config = GlassConfig.Heavy.copy(tintColor = Color(0xFF7C4DFF))
                ) {
                }
            }
        }
    }
}
