package com.example.studentmanagement.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransformation implements Transformation {
    private static final int DESIRED_SIZE = 200;

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(squaredBitmap, DESIRED_SIZE, DESIRED_SIZE, true);

        Bitmap bitmap = Bitmap.createBitmap(DESIRED_SIZE, DESIRED_SIZE, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(resizedBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = DESIRED_SIZE / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        resizedBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}