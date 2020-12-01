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

public class GPUImageMultiplyBlendFilter extends GPUImageTwoInputFilter {
    public static final String MULTIPLY_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform lowp float multiplyBlend;\n" +
            " uniform lowp float alphaBlend;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp vec4 overlayer = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "          \n" +
            "     gl_FragColor =base*(1.0-alphaBlend)*overlayer.a+(overlayer * base*(base.a*multiplyBlend) + overlayer * (1.0 - base.a*multiplyBlend))*alphaBlend + base * (1.0 - overlayer.a);\n" +
            " }";
    private int multiplyBlendLocation;
    private float multiplyBlend;
    private int alphaBlendLocation;
    private float alphaBlend;
    public GPUImageMultiplyBlendFilter() {

        super(MULTIPLY_BLEND_FRAGMENT_SHADER);
        multiplyBlend = 1f;
        alphaBlend = 1f;
    }
    @Override
    public void onInit() {
        super.onInit();
        multiplyBlendLocation = GLES20.glGetUniformLocation(getProgram(), "multiplyBlend");
        alphaBlendLocation = GLES20.glGetUniformLocation(getProgram(), "alphaBlend");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setMultiply(multiplyBlend);
        setAlpha(alphaBlend);
    }

    /**
     * @param multiply ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    public void setMultiply(final float multiply) {
        this.multiplyBlend = multiply;
        setFloat(multiplyBlendLocation, this.multiplyBlend);
    }
    public void setAlpha(final float alpha) {
        this.alphaBlend = alpha;
        setFloat(alphaBlendLocation, this.alphaBlend);
    }

}
