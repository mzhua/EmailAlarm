package im.mz.EmailAlarm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import im.mz.EmailAlarm.R;

/**
 * Created by mzhua_000 on 2014-12-11.
 */
public class CircleTime extends RelativeLayout {
    Context context;
    Paint paint = new Paint();

    int screenWidth, screenHeight;
    int strokeWidth = 8;

    int radius_circle_0 = 100;
    int radius_circle_1 = 50;
    int radius_circle_2 = 30;

    private RectF rectF2;

    private int refreshCounts = 360;
    private Handler handler;

    public CircleTime(Context context) {
        super(context);
        this.context = context;
        initParams();
    }

    private void initParams() {
        refreshCounts = 360;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels/3;

        radius_circle_0 = screenWidth / 4;
        radius_circle_1 = 3 * radius_circle_0 / 5;
        radius_circle_2 = 3 * radius_circle_1 / 5;

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getResources().getColor(R.color.theme_0));
//        canvas.drawLine(0, screenHeight / 2, screenWidth, screenHeight / 2, paint);//中线
//        canvas.drawLine(screenWidth / 2, 0, screenWidth / 2, screenHeight, paint);
//        canvas.drawLine(0, screenHeight / 2 + radius_circle_0,screenWidth , screenHeight / 2 + radius_circle_0, paint);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF = new RectF(0, screenHeight / 2 - radius_circle_0, 2 * radius_circle_0, screenHeight / 2 + radius_circle_0);
        //第一个圆
        canvas.drawArc(rectF, 0, 360, false, paint);
        paint.setStrokeWidth(0);
        paint.setTextSize(30);
        canvas.drawText("10",radius_circle_0,screenHeight/2 ,paint);
        paint.setStrokeWidth(strokeWidth);
        //第二个圆
        paint.setColor(getResources().getColor(R.color.theme_1));
        RectF rectF1 = new RectF(2 * radius_circle_0,screenHeight / 2 + radius_circle_0 - 2 * radius_circle_1, 2 * radius_circle_0 + 2 * radius_circle_1, screenHeight / 2 + radius_circle_0 );
        canvas.drawArc(rectF1, 0, 360, false, paint);

        //第三个圆
        paint.setColor(getResources().getColor(R.color.theme_2));
        rectF2 = new RectF(2 * radius_circle_0 + 2 * radius_circle_1,screenHeight / 2 + radius_circle_0 - 2 * radius_circle_2, 2 * radius_circle_0 + 2 * radius_circle_1 + 2 * radius_circle_2, screenHeight / 2 + radius_circle_0);

        canvas.drawArc(rectF2, 0, refreshCounts - 360 + 1, false, paint);
        if (refreshCounts > 0) {
            refreshCounts -= 5;
            invalidate();
        } else {
            Toast.makeText(context, "Finish  screenWidth=" + screenWidth + "  screenHeight=" + screenHeight, Toast.LENGTH_SHORT).show();
            refreshCounts = 360;
        }

    }


}
