package com.example.prm_project.ui.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.example.prm_project.R;
import com.example.prm_project.data.model.MonthlyRevenue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleLineChartView extends View {
    
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private Path linePath;
    private List<MonthlyRevenue> data;
    private float maxRevenue;
    private int padding = 60;
    
    public SimpleLineChartView(Context context) {
        super(context);
        init();
    }
    
    public SimpleLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public SimpleLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.admin_primary, null));
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        
        pointPaint = new Paint();
        pointPaint.setColor(getResources().getColor(R.color.admin_primary, null));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        
        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.admin_text_secondary, null));
        textPaint.setTextSize(32f);
        textPaint.setAntiAlias(true);
        
        gridPaint = new Paint();
        gridPaint.setColor(getResources().getColor(R.color.admin_text_secondary, null));
        gridPaint.setStrokeWidth(1f);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAlpha(100);
        
        linePath = new Path();
    }
    
    public void setData(List<MonthlyRevenue> monthlyRevenues) {
        this.data = monthlyRevenues;
        if (data != null && !data.isEmpty()) {
            maxRevenue = 0;
            for (MonthlyRevenue revenue : data) {
                if (revenue.getRevenue() > maxRevenue) {
                    maxRevenue = revenue.getRevenue();
                }
            }
            // Add some padding to max value
            maxRevenue = maxRevenue * 1.1f;
        }
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (data == null || data.isEmpty()) {
            canvas.drawText("No data available", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }
        
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;
        
        // Draw grid lines
        for (int i = 0; i <= 5; i++) {
            float y = padding + (height * i / 5f);
            canvas.drawLine(padding, y, padding + width, y, gridPaint);
        }
        
        // Draw data line and points
        linePath.reset();
        
        for (int i = 0; i < data.size(); i++) {
            MonthlyRevenue revenue = data.get(i);
            float x = padding + (width * i / (float)(data.size() - 1));
            float y = padding + height - (height * revenue.getRevenue() / maxRevenue);
            
            if (i == 0) {
                linePath.moveTo(x, y);
            } else {
                linePath.lineTo(x, y);
            }
            
            // Draw point
            canvas.drawCircle(x, y, 8f, pointPaint);
            
            // Draw month label
            String monthLabel = formatMonthLabel(revenue.getMonth());
            canvas.drawText(monthLabel, x - 30, padding + height + 40, textPaint);
            
            // Draw value
            canvas.drawText(String.valueOf((int)revenue.getRevenue()), x - 40, y - 20, textPaint);
        }
        
        // Draw the line
        canvas.drawPath(linePath, linePaint);
        
        // Draw Y-axis labels
        for (int i = 0; i <= 5; i++) {
            float y = padding + (height * i / 5f);
            float value = maxRevenue - (maxRevenue * i / 5f);
            canvas.drawText(String.valueOf((int)value), 10, y + 5, textPaint);
        }
    }
    
    private String formatMonthLabel(String monthString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            Date date = inputFormat.parse(monthString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return monthString;
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = 300;
        int minHeight = 250;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int width = Math.max(minWidth, widthSize);
        int height = Math.max(minHeight, heightSize);
        
        setMeasuredDimension(width, height);
    }
}
