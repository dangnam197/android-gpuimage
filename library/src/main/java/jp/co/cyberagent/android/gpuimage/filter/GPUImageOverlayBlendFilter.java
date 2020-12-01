/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

public class GPUImageOverlayBlendFilter extends GPUImageTwoInputFilter {
    public static final String OVERLAY_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform lowp float overlayBlend;\n" +
            " uniform lowp float multiplyBlend;\n" +
            " uniform lowp float alphaBlend;\n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     mediump float ra;\n" +
            "     if (2.0 * base.r < base.a) {\n" +
            "           ra = base.r*(1.0-alphaBlend)*overlay.a + (overlayBlend*2.0 * overlay.r * base.r + overlay.r * base.r*base.a*multiplyBlend)*alphaBlend + min(overlay.r * (1.0 - base.a*multiplyBlend),overlay.r * (1.0 - base.a*overlayBlend))*alphaBlend + base.r * (1.0 - overlay.a);\n" +
//            "         ra = overlay.r*(1.0-overlayBlend) + overlayBlend*2.0 * overlay.r * base.r + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
            "     } else {\n" +
            "           ra = base.r*(1.0-alphaBlend)*overlay.a + (overlayBlend*(overlay.a * base.a - 2.0 * (base.a - base.r) * (overlay.a - overlay.r)) + overlay.r * base.r*base.a*multiplyBlend)*alphaBlend + min(overlay.r * (1.0 - base.a*multiplyBlend),overlay.r * (1.0 - base.a*overlayBlend))*alphaBlend + base.r * (1.0 - overlay.a);\n" +
           // "         ra = overlay.r*(1.0-overlayBlend) + overlayBlend*(overlay.a * base.a - 2.0 * (base.a - base.r) * (overlay.a - overlay.r)) + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
            "     }\n" +
            "     \n" +
            "     mediump float ga;\n" +
            "     if (2.0 * base.g < base.a) {\n" +
            "           ga = base.g*(1.0-alphaBlend)*overlay.a + (overlayBlend*2.0 * overlay.g * base.g + overlay.g * base.g*base.a*multiplyBlend)*alphaBlend + min(overlay.g * (1.0 - base.a*multiplyBlend),overlay.g * (1.0 - base.a*overlayBlend))*alphaBlend + base.g * (1.0 - overlay.a);\n" +
            "     } else {\n" +
            "           ga = base.g*(1.0-alphaBlend)*overlay.a + (overlayBlend*(overlay.a * base.a - 2.0 * (base.a - base.g) * (overlay.a - overlay.g)) + overlay.g * base.g*base.a*multiplyBlend)*alphaBlend + min(overlay.g * (1.0 - base.a*multiplyBlend),overlay.g * (1.0 - base.a*overlayBlend))*alphaBlend + base.g * (1.0 - overlay.a);\n" +
            "     }\n" +
            "     \n" +
            "     mediump float ba;\n" +
            "     if (2.0 * base.b < base.a) {\n" +
            "           ba = base.b*(1.0-alphaBlend)*overlay.a + (overlayBlend*2.0 * overlay.b * base.b + overlay.b * base.b*base.a*multiplyBlend)*alphaBlend + min(overlay.b * (1.0 - base.a*multiplyBlend),overlay.b * (1.0 - base.a*overlayBlend))*alphaBlend + base.b * (1.0 - overlay.a);\n" +
            "     } else {\n" +
            "           ba = base.b*(1.0-alphaBlend)*overlay.a + (overlayBlend*(overlay.a * base.a - 2.0 * (base.a - base.b) * (overlay.a - overlay.b)) + overlay.b * base.b*base.a*multiplyBlend)*alphaBlend + min(overlay.b * (1.0 - base.a*multiplyBlend),overlay.b * (1.0 - base.a*overlayBlend))*alphaBlend + base.b * (1.0 - overlay.a);\n" +
            "     }\n" +
            "     \n" +
            "     gl_FragColor = vec4(ra, ga, ba, 1.0);\n" +
            " }";
    private int overlayBlendLocation;
    private float overlayBlend;
    private int multiplyBlendLocation;
    private float multiplyBlend;
    private int alphaBlendLocation;
    private float alphaBlend;
    public GPUImageOverlayBlendFilter() {
        super(OVERLAY_BLEND_FRAGMENT_SHADER);
        overlayBlend = 0f;
        multiplyBlend = 0f;
        alphaBlend = 1f;
    }
    @Override
    public void onInit() {
        super.onInit();
        overlayBlendLocation = GLES20.glGetUniformLocation(getProgram(), "overlayBlend");
        multiplyBlendLocation = GLES20.glGetUniformLocation(getProgram(), "multiplyBlend");
        alphaBlendLocation = GLES20.glGetUniformLocation(getProgram(), "alphaBlend");

    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setOverlay(overlayBlend);
        setMultiply(multiplyBlend);
        setAlpha(alphaBlend);
    }

    /**
     * @param multiply ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    public void setOverlay(final float multiply) {
        this.overlayBlend = multiply;
        setFloat(overlayBlendLocation, this.overlayBlend);
    }
    public void setMultiply(final float multiply) {
        this.multiplyBlend = multiply;
        setFloat(multiplyBlendLocation, this.multiplyBlend);
    }
    public void setAlpha(final float alpha) {
        this.alphaBlend = alpha;
        setFloat(alphaBlendLocation, this.alphaBlend);
    }

}
