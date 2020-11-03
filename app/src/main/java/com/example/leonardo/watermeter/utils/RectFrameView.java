package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class RectFrameView extends View {
    Rect rect;
    int centerY;
    int centerX;
    int progress=0;
    Canvas mCanvas;
    public RectFrameView(Context context, Rect rect) {
        super(context);
        this.rect=rect;
        this.centerY=rect.centerY();
        this.centerX=rect.centerX();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas=canvas;
        canvas.drawRect(rect,paint);
        DrawExpousePreogress();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void DrawExpousePreogress(){
        if(centerX<600){
            mCanvas.drawLine(centerX+110,centerY-145,centerX+110,centerY-25-progress,paint);
            mCanvas.drawLine(centerX+110,centerY+25-progress,centerX+110,centerY+145,paint);
            mCanvas.drawLine(centerX+95,centerY-5-progress,centerX+105,centerY-5-progress,paint);//画 “+”号
            mCanvas.drawLine(centerX+100,centerY-10-progress,centerX+100,centerY-progress,paint);
            mCanvas.drawCircle(centerX+110,centerY-progress,20,paint);//画空心圆
            //mCanvas.drawArc(centerX+90,centerY-20,centerX+130,centerY+20,-45,180,false,paint);//画空心弧
            paint.setStyle(Paint.Style.FILL);
            mCanvas.drawLine(centerX+115,centerY+5-progress,centerX+125,centerY+5-progress,paint);//画 “-”号
            mCanvas.drawArc(centerX+90,centerY-20-progress,centerX+130,centerY+20-progress,135,180,false,paint);//画实心弧
            paint.setStyle(Paint.Style.STROKE);
            //mCanvas.drawCircle(centerX+110,centerY-progress,20,paint);
        }else{
            paint.setStyle(Paint.Style.STROKE);
            mCanvas.drawLine(centerX-110,centerY-145,centerX-110,centerY-25-progress,paint);
            mCanvas.drawLine(centerX-110,centerY+25-progress,centerX-110,centerY+145,paint);
            mCanvas.drawLine(centerX-125,centerY-5-progress,centerX-115,centerY-5-progress,paint);//画“+”号
            mCanvas.drawLine(centerX-100,centerY-10-progress,centerX-100,centerY-progress,paint);
            mCanvas.drawCircle(centerX-110,centerY-progress,20,paint);
            //mCanvas.drawArc(centerX-130,centerY-20,centerX-90,centerY+20,-45,180,false,paint);//画空心弧
            paint.setStyle(Paint.Style.FILL);
            mCanvas.drawLine(centerX-105,centerY+5,centerX-115,centerY+5,paint);//画“-”号
            mCanvas.drawArc(centerX-130,centerY-20-progress,centerX-90,centerY+20-progress,135,180,false,paint);//画实心弧
            paint.setStyle(Paint.Style.STROKE);
            //mCanvas.drawCircle(centerX-110,centerY+progress,20,paint);
        }
    }
      public void updateProgress(int currentprogress){
        progress=currentprogress;
        invalidate();

      }

    Paint paint=new Paint();
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);//设置线宽
        paint.setAlpha(150);
    }

}
