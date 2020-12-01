package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class GPUWobbleFilter extends GPUImageFilter {
    public static final String TEST = "uniform sampler2D inputImageTexture;\n" +
            "uniform float offset;\n\n" +
            "varying vec2 textureCoordinate;\n\n" +
            "void main()\n{\n    " +
                "vec2 texcoord = textureCoordinate;\n    " +
                "texcoord.x += sin(texcoord.y * 4.0 * 2.0 * 3.14159 +offset) / 100.0;\n    " +
                "gl_FragColor = texture2D(inputImageTexture, texcoord);\n" +
            "}";
    public static final String AAA ="varying vec2 textureCoordinate;        // holds the Vertex position <-1,+1> !!!\n" +
            "uniform sampler2D inputImageTexture;    // used texture unit\n" +
            "uniform float tx,ty;            // x,y waves phase\n" +
            "\n" +
            "vec2 SineWave( vec2 p )\n" +
            "    {\n" +
            "    // convert Vertex position <-1,+1> to texture coordinate <0,1> and some shrinking so the effect dont overlap screen\n" +
            "    p.x=( 0.55*p.x)+0.5;\n" +
            "    p.y=(-0.55*p.y)+0.5;\n" +
            "    // wave distortion\n" +
            "    float x = sin( 25.0*p.y + 30.0*p.x + 6.28*tx) * 0.05;\n" +
            "    float y = sin( 25.0*p.y + 30.0*p.x + 6.28*ty) * 0.05;\n" +
            "    return vec2(p.x+x, p.y+y);\n" +
            "    }\n" +
            "\n" +
            "void main()\n" +
            "    {\n" +
            "    gl_FragColor = texture2D(inputImageTexture,SineWave(textureCoordinate));\n" +
            "    }";
    private String UNIFORM_OFFSET = "offset";
    private float mOffset;
    private int mOffsetHandler;
    private long mStartTime ;
    public GPUWobbleFilter(){
        super(NO_FILTER_VERTEX_SHADER,TEST);
    }

    @Override
    public void onInit() {
        this.mStartTime = System.currentTimeMillis();
        super.onInit();
        mOffsetHandler = GLES20.glGetUniformLocation(getProgram(), "offset");

    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        getOff();

    }

    public void getOff(){
        long currentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        if (currentTimeMillis > 20000) {
            this.mStartTime = System.currentTimeMillis();
        }
        this.mOffset = (float) currentTimeMillis / 1000.0f * 2.0f * 3.14159f * 0.75f;
        setFloat(mOffsetHandler, mOffset);
    }
}
