package com.example.appulse;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

public class Tutorial
{
	private String[] message;
	private int width, height;
	private Rect rect;
	private Paint rectPaint, textPaint;
	private boolean isShowing = true;
	private boolean nextPressed = false;
	
	public Tutorial(int n, int width, int height, Typeface tf)
	{
		message = getMessage(n);
		this.width = width;
		this.height = height;		
		rectPaint = new Paint();
		rectPaint.setARGB(120, 30, 30, 30);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(width/(24));
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Align.CENTER);
		rect = new Rect(0, height / 4, width,(int)(height / 4 + (message.length * textPaint.getTextSize()) + height/30));
	}
	public String[] getMessage(int n)
	{
		String[] theMessage = new String[18];
		switch(n)
		{
			case 1:
				theMessage[0] = "The year is 2239 AD; eight years after the";
				theMessage[1] = "Second Hemisphere War broke out. War still";
				theMessage[2] =	"rages on. You and an exclusive team of";			
				theMessage[3] =	"scientists have been endlessly working on";
				theMessage[4] = "a top secret military project known as";
				theMessage[5] = "Appulse. And the project is finally done. Various";
				theMessage[6] =	"spacecraft have been launched into space";
				theMessage[7] = "and are now following the Moon in orbit. They";
				theMessage[8] =	"are designed to push the Moon in front of the ";
				theMessage[9] = "Sun to keep the Eastern Hemisphere in total";
				theMessage[10] = "darkness. This weapon will cause crops to die,";
				theMessage[11] = "freezing temperatures and insomnia which will"; 
				theMessage[12] = "hopefully bring an end to the war. You are tasked";
				theMessage[13] = "with making sure the other side experiences a";
				theMessage[14] = "constant solar eclipse. What was once a phenomenal"; 
				theMessage[15] = "marvel will become their longest nightmare...";
				theMessage[16] = "";
				theMessage[17] = "<Tap anywhere to continue>";
				break;
		}
		return theMessage;
	}
	public void onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			nextPressed = true;
		if(nextPressed && event.getAction() == MotionEvent.ACTION_UP)
			isShowing = false;
		else if(event.getAction() == MotionEvent.ACTION_UP)
			nextPressed = false;
	}
	
	public boolean isShowing()
	{
		return isShowing;
	}
	public void setShowing(boolean tf)
	{
		isShowing = tf;
	}
	public void draw(Canvas canvas)
	{
		canvas.drawRect(rect, rectPaint);
		for (int i = 0; i < message.length; i++)
				canvas.drawText(message[i], width/2, rect.top + (i * textPaint.getTextSize()) + height/30, textPaint);
	}
}

