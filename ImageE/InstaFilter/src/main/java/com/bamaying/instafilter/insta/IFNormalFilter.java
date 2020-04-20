package com.bamaying.instafilter.insta;

import android.content.Context;


public class IFNormalFilter extends InstaFilter {

    @Override
    public String filterName() {
        return "原图";
    }

    public static String FilterName() {
        return "原图";
    }

    public static final String SHADER = "precision lowp float;\n" +
            " precision lowp float;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }";

    public IFNormalFilter(Context context) {
        super(SHADER, 0);
    }

}
