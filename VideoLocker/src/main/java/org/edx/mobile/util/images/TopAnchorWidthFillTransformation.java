package org.edx.mobile.util.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by miankhalid on 9/9/15.
 */
public class TopAnchorWidthFillTransformation extends BitmapTransformation {
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public TopAnchorWidthFillTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                               int outWidth, int outHeight) {
        final int width = toTransform.getWidth();
        final float widthRatio = outWidth / (float) width;
        if (outWidth > width) {
            outWidth = width;
            outHeight = Math.round(outHeight / widthRatio);
        }
        Bitmap newBitmap = Bitmap.createBitmap(
                outWidth, outHeight, toTransform.getConfig());
        newBitmap.setDensity(toTransform.getDensity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newBitmap.setPremultiplied(true);
        }
        Canvas canvas = new Canvas(newBitmap);
        if (outWidth < width) {
            canvas.scale(widthRatio, widthRatio);
        }
        canvas.drawBitmap(toTransform, 0, 0, paint);
        return newBitmap;
    }

    @Override
    public String getId() {
        return "TOP_ANCHOR_WIDTH_FILL";
    }
}
