package org.edx.mobile.util.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class TopAnchorFillWidthTransformation extends BitmapTransformation {
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public TopAnchorFillWidthTransformation(Context context) {
        super(context);
    }

    @Override
    public String getId() {
        return "TOP_ANCHOR_FILL_WIDTH";
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                               int outWidth, int outHeight) {
        final int width = toTransform.getWidth();
        final float widthRatio = outWidth / (float) width;

        /**
         * Implementation Details:
         Following if-condition is an optimization to just match the aspect ratio of the View where
         the Bitmap has a smaller width, and not artificially scale up the Bitmap (as ImageView
         will automatically scale it up to match it's coordinates unless explicitly set not to do
         so). However, Glide uses a TransitionDrawable to transition from the placeholder to the
         actual image, and as we can't guarantee that the placeholder would also have a matching
         aspect ratio, this can cause the scaling to not be performed properly to fill the View.

         If we're always using a scaleType of fitXY or centerCrop in all our ImageView items, then
         we should not encounter any issues. However, if we might use fitCenter (the default),
         center, or centerInside scale types, then we need to get rid of this optimization. (Done
         by removing the following if-section).

         Since, we're using fitXY as our scaleType wherever we are applying this transformation, so
         the following condition gives us memory benefits.
         */
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
}
