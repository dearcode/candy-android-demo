package net.dearcode.candy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 *  * Created by c-wind on 2016/9/22 15:08
 *  * mail：root@codecn.org
 *  
 */
public class ImageView extends android.widget.ImageView {
    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取控件需要重新绘制的区域
        Rect rect = canvas.getClipBounds();
        rect.bottom--;
        rect.right--;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(rect, paint);
    }

}
