package core.presentation.glass

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.intellij.lang.annotations.Language

@Stable
actual fun Modifier.glassNoiseEffect(
    intensity: Float,
    enabled: Boolean
): Modifier = if (!enabled) {
    this
} else {
    composed {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shader = remember { RuntimeShader(FROSTED_GLASS_SHADER) }

            graphicsLayer {
                shader.setFloatUniform("iResolution", size.width, size.height)
                shader.setFloatUniform("noiseIntensity", intensity)

                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(shader, "inputImage")
                    .asComposeRenderEffect()
            }
        } else {
            glassFallbackNoise(intensity = intensity)
        }
    }
}

@Language("AGSL")
private const val FROSTED_GLASS_SHADER = """
    uniform shader inputImage;
    uniform float2 iResolution;
    uniform float noiseIntensity;
    
    float random(float2 uv) {
        return fract(sin(dot(uv, float2(12.9898, 78.233))) * 43758.5453);
    }
    
    float noise(float2 uv) {
        float2 i = floor(uv);
        float2 f = fract(uv);
        
        f = f * f * (3.0 - 2.0 * f);
        
        float a = random(i);
        float b = random(i + float2(1.0, 0.0));
        float c = random(i + float2(0.0, 1.0));
        float d = random(i + float2(1.0, 1.0));
        
        return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
    }
    
    float fbm(float2 uv) {
        float value = 0.0;
        float amplitude = 0.5;
        float frequency = 3.0;
        
        for (int i = 0; i < 3; i++) {
            value += amplitude * noise(uv * frequency);
            frequency *= 2.0;
            amplitude *= 0.5;
        }
        
        return value;
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution;
        
        half4 color = inputImage.eval(fragCoord);
        
        float noiseValue = fbm(uv * 8.0);
        
        float noise = (noiseValue - 0.5) * noiseIntensity;
        
        half3 frosted = color.rgb + half3(noise, noise, noise);
        
        frosted = clamp(frosted, half3(0.0), half3(1.0));
        
        return half4(frosted * color.a, color.a);
    }
"""
