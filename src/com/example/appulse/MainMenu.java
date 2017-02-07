package com.example.appulse;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.view.*;

public class MainMenu
{
	private Bitmap creditBitmap;
	private Rect creditRect;
	private Rect playBoundingBox;
	private Rect quitBoundingBox;
	private int width, height;
	private float buttonX, playY, quitY;
	private boolean play = false, playPressed = false, quit = false, showCredits = false;
	private Paint paint, titlePaint, blackPaint, creditPaint;
	private String[] message = new String[13];
	
	public MainMenu(Bitmap creditBitmap, float scale, int width, int height, Typeface tf)
	{
		this.creditBitmap = Bitmap.createScaledBitmap(creditBitmap, (width/10), (width/10), false);
		creditRect = new Rect(width - width/9, height - width/9, width - width/9 + creditBitmap.getWidth(),
				height - width/9 + creditBitmap.getHeight());
		this.width = width;
		this.height = height;
		buttonX = width/2 - width/6;
		playY = height/4 + width/8;
		quitY = height/1.5f + width/8;
		playBoundingBox = new Rect((int)buttonX, (int)playY, (int)buttonX+width/3,
				(int)playY+width/6);
		quitBoundingBox = new Rect((int)buttonX, (int)quitY, (int)buttonX+width/3,
				(int)quitY+width/6);
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTypeface(tf);
		paint.setTextSize(width/(8.5f));
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		titlePaint = new Paint();
		titlePaint.setColor(Color.WHITE);
		titlePaint.setTypeface(tf);
		titlePaint.setTextSize(width/(4));
		titlePaint.setAntiAlias(true);
		titlePaint.setTextAlign(Align.CENTER);
		
		blackPaint = new Paint();
		blackPaint.setColor(Color.BLACK);
		blackPaint.setTypeface(tf);
		blackPaint.setTextSize(width/(4));
		blackPaint.setAntiAlias(true);
		blackPaint.setTextAlign(Align.CENTER);
		
		creditPaint = new Paint();
		creditPaint.setColor(Color.WHITE);
		creditPaint.setTextSize(width/(24));
		creditPaint.setAntiAlias(true);
		creditPaint.setTextAlign(Align.CENTER);
		
		message[0] = "A game by";
		message[1] = "Tariq Soliman";
		message[2] = "";
		message[3] = "Images";
		message[4] = "Sun and Earth by NASA on Wikimedia Commons";
		message[5] = "Moon by Gregory H. Revera on Wikimedia Commons";
		message[6] = "";
		message[7] = "Music";
		message[8] = "ElectroAcid by frankum on FreeSound";
		message[9] = "";
		message[10] = "";
		message[11] = "";
		message[12] = "<Tap Anywhere To Return>";
	}
	public void onTouchEvent(MotionEvent event)
	{
		if(!showCredits)
		{
			if(!playPressed && event.getAction() == MotionEvent.ACTION_DOWN &&
					playBoundingBox.contains((int)event.getRawX(),(int)event.getRawY()))
				playPressed = true;
			if(playPressed && event.getAction() == MotionEvent.ACTION_UP &&
					playBoundingBox.contains((int)event.getRawX(),(int)event.getRawY()))
			{
				play = true;
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN &&
					quitBoundingBox.contains((int)event.getRawX(),(int)event.getRawY()))
			{
				quit = true;
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN &&
					creditRect.contains((int)event.getRawX(),(int)event.getRawY()))
			{
				showCredits = true;
			}
		}
		else if(showCredits && event.getAction() == MotionEvent.ACTION_DOWN)
		{
			showCredits = false;
		}
	}
	public boolean getPlay()
	{
		return play;
	}
	public void setPlay(boolean tf)
	{
		play = tf;
	}
	public boolean getQuit()
	{
		return quit;
	}
	public void draw(Canvas canvas)
	{
		if(!showCredits)
		{
			canvas.drawText("Play", width/2, playY + width/8, paint);
			canvas.drawText("Quit", width/2, quitY + width/8, paint);
			canvas.drawBitmap(creditBitmap, width - width/9, height - width/9, null);
			canvas.drawText("APPULSE", width/2, height/8 + 5, blackPaint);
			canvas.drawText("APPULSE", width/2, height/8, titlePaint);
		}
		else if(showCredits)
		{
			for (int i = 0; i < message.length; i++)
				canvas.drawText(message[i], width/2,
						height/4 + (i * creditPaint.getTextSize()) + height/30, creditPaint);
		}
	}
}
