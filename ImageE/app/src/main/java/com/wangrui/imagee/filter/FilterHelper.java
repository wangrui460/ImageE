package com.wangrui.imagee.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.bamaying.instafilter.insta.IF1977Filter;
import com.bamaying.instafilter.insta.IFAmaroFilter;
import com.bamaying.instafilter.insta.IFBrannanFilter;
import com.bamaying.instafilter.insta.IFEarlybirdFilter;
import com.bamaying.instafilter.insta.IFHefeFilter;
import com.bamaying.instafilter.insta.IFHudsonFilter;
import com.bamaying.instafilter.insta.IFInkwellFilter;
import com.bamaying.instafilter.insta.IFLomofiFilter;
import com.bamaying.instafilter.insta.IFLordKelvinFilter;
import com.bamaying.instafilter.insta.IFNashvilleFilter;
import com.bamaying.instafilter.insta.IFNormalFilter;
import com.bamaying.instafilter.insta.IFRiseFilter;
import com.bamaying.instafilter.insta.IFSierraFilter;
import com.bamaying.instafilter.insta.IFSutroFilter;
import com.bamaying.instafilter.insta.IFToasterFilter;
import com.bamaying.instafilter.insta.IFValenciaFilter;
import com.bamaying.instafilter.insta.IFWaldenFilter;
import com.bamaying.instafilter.insta.IFXproIIFilter;
import com.bamaying.instafilter.insta.InstaFilter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class FilterHelper extends GPUImageFilter {

    public FilterHelper() {}

    private static List<InstaFilter> filters = new ArrayList<>();

    public static final String[] FILTERS = {
        IFNormalFilter.FilterName(),
        IFAmaroFilter.FilterName(),
        IFRiseFilter.FilterName(),
        IFHudsonFilter.FilterName(),
        IFXproIIFilter.FilterName(),
        IFSierraFilter.FilterName(),
        IFLomofiFilter.FilterName(),
        IFEarlybirdFilter.FilterName(),
        IFSutroFilter.FilterName(),
        IFToasterFilter.FilterName(),
        IFBrannanFilter.FilterName(),
        IFInkwellFilter.FilterName(),
        IFWaldenFilter.FilterName(),
        IFHefeFilter.FilterName(),
        IFValenciaFilter.FilterName(),
        IFNashvilleFilter.FilterName(),
        IF1977Filter.FilterName(),
        IFLordKelvinFilter.FilterName(),
    };

    public static InstaFilter getFilter(Context context, String key) {
            switch (key){
                case "原图":
                    return new IFNormalFilter(context);
                case "流年":
                    return new IFAmaroFilter(context);
                case "森系":
                    return new IFRiseFilter(context);
                case "胶片":
                    return new IFHudsonFilter(context);
                case "新潮":
                    return new IFXproIIFilter(context);
                case "清新":
                    return new IFSierraFilter(context);
                case "个性":
                    return new IFLomofiFilter(context);
                case "怡尚":
                    return new IFEarlybirdFilter(context);
                case "摩登":
                    return new IFSutroFilter(context);
                case "绚丽":
                    return new IFToasterFilter(context);
                case "淡雅":
                    return new IFBrannanFilter(context);
                case "黑白":
                    return new IFInkwellFilter(context);
                case "日系":
                    return new IFWaldenFilter(context);
                case "优格":
                    return new IFHefeFilter(context);
                case "优雅":
                    return new IFValenciaFilter(context);
                case "复古":
                    return new IFNashvilleFilter(context);
                case "创新":
                    return new IF1977Filter(context);
                case "回忆":
                    return new IFLordKelvinFilter(context);
                default: return new IFNormalFilter(context);
            }
    }

    public void destroyFilters() {
        if (filters != null) {
            for (InstaFilter filter : filters) {
                if (filter != null) {
                    filter.destroy();
                }
            }
        }
    }

    public Bitmap filterPhoto(Context context, Bitmap bitmap, String key) {
        if (bitmap == null) {
            return null;
        }

        InstaFilter filter = getFilter(context, key);
        filters.add(filter);

        GPUImage gpuImage = new GPUImage(context);
        gpuImage.setFilter(filter);
        bitmap = gpuImage.getBitmapWithFilterApplied(bitmap);
        gpuImage.deleteImage();

        return bitmap;
    }

}
