package com.example.antoine.walkrun;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;


public class SummaryView extends View {
    Paint orange, line_black, text_black;
    private ArrayList<Long> time_list;

    public SummaryView(Context context) {
        super(context);
        init();
    }

    public SummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        text_black = new Paint(Paint.ANTI_ALIAS_FLAG);
        text_black.setColor(Color.BLACK);
        text_black.setTextSize(50);

        orange = new Paint(Paint.ANTI_ALIAS_FLAG);
        orange.setColor(Color.rgb(253,75,1));

        line_black = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_black.setStyle(Paint.Style.STROKE);
        line_black.setStrokeWidth(6);
        line_black.setColor(Color.BLACK);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int originX = 3;
        int originY = canvas.getHeight()-3;
        canvas.drawLine(0,0,0,canvas.getHeight(),line_black);
        canvas.drawLine(0,canvas.getHeight(),canvas.getWidth(),canvas.getHeight(),line_black);
        int List_size = time_list.size();
        if(List_size>0) {
            Log.i("view",time_list.toString());
            int av = average(time_list);
            int bar_width = canvas.getWidth() / List_size;
            int max_height = av*2;
            for (int i = 0; i < List_size; i++) {
                long bar_height = canvas.getHeight()-(time_list.get(i) * canvas.getHeight() / max_height);
                canvas.drawRect(originX + (bar_width * i), bar_height, bar_width * (i + 1), originY, orange);
            }
            float mid = canvas.getHeight()/2;
            float first_quarter = 3*canvas.getHeight()/4;
            float third_quarter = canvas.getHeight()/4;
            canvas.drawLine(0, mid, 15, mid, line_black);
            canvas.drawText(""+av/1000+"s",20,mid,text_black);

            canvas.drawLine(0, first_quarter, 15, first_quarter, line_black);
            canvas.drawText(""+(av/1000)/2+"s",20,first_quarter,text_black);

            canvas.drawLine(0, third_quarter, 15, third_quarter, line_black);
            canvas.drawText(""+3*(av/1000)/2+"s",20,third_quarter,text_black);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setList(ArrayList<Long> time_list){
        this.time_list = time_list;
    }

    private int average(ArrayList<Long> list){
        int count = 0;
        for (long i :
                list) {
            count += i;
        }
        return count/list.size();
    }
}
