package com.bamaying.instafilter.insta;

import android.content.Context;

import android.graphics.BitmapFactory;

import com.bamaying.instafilter.R;

public class IFHudsonFilter extends InstaFilter {

    @Override
    public String filterName() {
        return "胶片";
    }

    public static String FilterName() {
        return "胶片";
    }

    public static final String SHADER = "precision lowp float;\n" +
        " varying highp vec2 textureCoordinate;\n" +
        " \n" +
        " uniform sampler2D inputImageTexture;\n" +
        " uniform sampler2D inputImageTexture2; //blowout;\n" +
        " uniform sampler2D inputImageTexture3; //overlay;\n" +
        " uniform sampler2D inputImageTexture4; //map\n" +
        " \n" +
        " void main()\n" +
        " {\n" +
        "     \n" +
        "     vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
        "     \n" +
        "     vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
        "     \n" +
        "     texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
        "     texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
        "     texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
        "     \n" +
        "     vec4 mapped;\n" +
        "     mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n" +
        "     mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n" +
        "     mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n" +
        "     mapped.a = 1.0;\n" +
        "     gl_FragColor = mapped;\n" +
        " }";

    public IFHudsonFilter(Context context) {
        super(SHADER, 3);
        bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hudson_background);
        bitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.overlay_map);
        bitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hudson_map);
    }

}
