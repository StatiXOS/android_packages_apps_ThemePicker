/*
 * Copyright (C) 2020 CarbonROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.customization.model.theme.custom;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class TwoTrianglesFramedDrawable extends ShapeDrawable {

    private int mActivePrimary;
    private int mSecondPrimary;
    private int mBorderColor;
    private int mCornerRadius;
    private int mBorderThickness;

    public TwoTrianglesFramedDrawable(int active, int alternative, int bordercolor,
                                 int radius, int boarderThickness){
        super();
        mActivePrimary = active;
        mSecondPrimary = alternative;
        mBorderColor = bordercolor;
        mCornerRadius = radius;
        mBorderThickness = 2 * boarderThickness;// 2 * as we cut away half of it
        setShape(new TwoTrianglesFramedShape());
    }

    private class TwoTrianglesFramedShape extends Shape {

        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStrokeWidth(mBorderThickness);
            paint.setAntiAlias(true);

            Path cPath = new Path();
            cPath.setFillType(Path.FillType.EVEN_ODD);

            RectF clip = new RectF(getBounds());
            clip.inset(mBorderThickness, mBorderThickness);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mBorderColor);
            canvas.drawRoundRect(clip, mCornerRadius, mCornerRadius, paint);

            cPath.addRoundRect(clip,
                    mCornerRadius, mCornerRadius, Path.Direction.CCW);
            canvas.clipPath(cPath);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            Path path1 = new Path();
            path1.setFillType(Path.FillType.EVEN_ODD);

            paint.setStyle(Paint.Style.FILL);

            paint.setColor(mActivePrimary);

            Point a = new Point((int)getWidth(),0);
            Point b = new Point(0, 0);
            Point c = new Point(0, (int) getHeight());

            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();
            canvas.drawPath(path, paint);

            paint.setColor(mSecondPrimary);

            Point a1 = new Point((int)getWidth(),0);
            Point b1 = new Point((int)getWidth(), (int)getHeight());
            Point c1 = new Point(0, (int) getHeight());

            path1.moveTo(a1.x, a1.y);
            path1.lineTo(b1.x, b1.y);
            path1.lineTo(c1.x, c1.y);
            path1.close();
            canvas.drawPath(path1, paint);
        }
    }
}
