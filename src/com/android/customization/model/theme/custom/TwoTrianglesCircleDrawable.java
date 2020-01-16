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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class TwoTrianglesCircleDrawable extends ShapeDrawable {

    private int mActivePrimary;
    private int mSecondPrimary;

    public TwoTrianglesCircleDrawable(int active, int alternative){
        super();
        mActivePrimary = active;
        mSecondPrimary = alternative;
        setShape(new CircleTwoTrianglesShape());
    }

    private class CircleTwoTrianglesShape extends OvalShape {

        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            Path cPath = new Path();
            cPath.setFillType(Path.FillType.EVEN_ODD);
            float radius = getWidth() > getHeight() ? getWidth() : getHeight();
            cPath.addCircle(getWidth() / 2, getHeight() / 2, radius / 2
                    , Path.Direction.CCW);
            canvas.clipPath(cPath);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            Path path1 = new Path();
            path1.setFillType(Path.FillType.EVEN_ODD);

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
