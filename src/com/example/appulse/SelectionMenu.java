package com.example.appulse;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

public class SelectionMenu
{
	private Bitmap earthBitmap, sunBitmap, returnBitmap, padlockBitmap;
	private Rect returnRect;
	private int width, height;
	private int movement;
	private LinePoints[] earthLocs = new LinePoints[12];
	private Rect[] earthBoxes = new Rect[12];
	private boolean[] earthTouched = new boolean[12];
	private float[] earthXs = new float[12];
	private int selectedLevels = 0;
	private boolean goBack = false;
	private boolean goBackPressed = false;
	private boolean isTouching = false;
	private float startX;
	private Paint paint;
	private Paint monthPaint;
	private int paintTransparency = 0;
	private String[] months;
	private boolean playStage = false;
	private int levelsComplete;
	
	public SelectionMenu(Bitmap earthBitmap, Bitmap sunBitmap, Bitmap returnBitmap, Bitmap padlockBitmap,
			int width, int height, Typeface tf, int levelsComplete)
	{
		this.width = width;
		this.height = height;
		this.earthBitmap = Bitmap.createScaledBitmap(earthBitmap, (int)(width/2.5), (int)(width/2.5), false);
		this.sunBitmap = Bitmap.createScaledBitmap(sunBitmap, (int)(width/2.5), (int)(width/2.5), false);
		this.returnBitmap = Bitmap.createScaledBitmap(returnBitmap, width/6, width/6, false);
		this.padlockBitmap = Bitmap.createScaledBitmap(padlockBitmap, width/4, width/4, false);
		returnRect = new Rect((int)(width/5 - this.returnBitmap.getWidth()/2), (int)(height - height/7),
				(int)(width/5 - this.returnBitmap.getWidth()/2) + this.returnBitmap.getWidth(),
				(int)(height - height/7) + this.returnBitmap.getHeight());
		for (int i = 0; i < earthLocs.length; i++)
		{
			earthLocs[i] = new LinePoints(((i) * (width + width/2)) - this.earthBitmap.getWidth()/2 + width/2,
					height/2, 0, 0);
			earthBoxes[i] = new Rect(earthLocs[i].getX1(), earthLocs[i].getY1(), 
				earthLocs[i].getX1() + this.earthBitmap.getWidth(), earthLocs[i].getY1() + this.earthBitmap.getHeight());
			earthTouched[i] = false;
		}
		this.levelsComplete = levelsComplete;
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(width/(20));
		
		monthPaint = new Paint();
		monthPaint.setColor(Color.WHITE);
		monthPaint.setTextSize(width/(8.33f));
		monthPaint.setTextAlign(Align.CENTER);
		monthPaint.setTypeface(tf);
		monthPaint.setAntiAlias(true);
		
		months = new String[12];
		months[0] = "January";
		months[1] = "February";
		months[2] = "March";
		months[3] = "April";
		months[4] = "May";
		months[5] = "June";
		months[6] = "July";
		months[7] = "August";
		months[8] = "September";
		months[9] = "October";
		months[10] = "November";
		months[11] = "December";
	}
	public void onTouchEvent(MotionEvent event)
	{	
		if(event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_MOVE)
			isTouching = true;
		else isTouching = false;
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startX = event.getRawX();
			for(int i = 0; i < earthXs.length; i++)
			{
				earthXs[i] = earthLocs[i].getX1();
			}
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			for(int i = 0; i < earthLocs.length; i++)
				earthLocs[i].x1 = (int) ((earthXs[i] + (4*event.getRawX()) - (4*startX)));
		}
		for(int i = 0; i < earthBoxes.length; i++)
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN &&
				earthBoxes[i].contains((int)event.getRawX(),(int)event.getRawY()))
			{
				if(i <= (levelsComplete / 8))
					earthTouched[i] = true;
			}
		}
		for(int i = 0; i < earthBoxes.length; i++)
		{
			if(earthTouched[i] && event.getAction() == MotionEvent.ACTION_UP &&
				earthBoxes[i].contains((int)event.getRawX(),(int)event.getRawY()))
			{
				playStage = true;
				selectedLevels = (i + 1);
			}
			else if(earthTouched[i] && event.getAction() == MotionEvent.ACTION_UP)
			{
				earthTouched[i] = false;
			}
		}	
		if(event.getAction() == MotionEvent.ACTION_DOWN &&
				returnRect.contains((int)event.getRawX(),(int)event.getRawY()))
			goBackPressed = true;
		if(goBackPressed && event.getAction() == MotionEvent.ACTION_UP &&
				returnRect.contains((int)event.getRawX(),(int)event.getRawY()))
			goBack = true;
		if(event.getAction() == MotionEvent.ACTION_UP)
			goBackPressed = false;
	}
	public void update()
	{
		movement =  findClosest();
		if(!isTouching)
		{		
			if(movement != 0)
			{
				for(int i = 0; i < earthLocs.length; i++)
					earthLocs[i].x1 -= movement/10;
			}
			if(paintTransparency < 240)
			{
				paintTransparency += 10;
			}
		}
		else if(paintTransparency > 10)
		{
			paintTransparency -= 10;
		}
		for(int i = 0; i < earthLocs.length; i++)
			earthBoxes[i] = new Rect(earthLocs[i].getX1(), earthLocs[i].getY1(), 
				earthLocs[i].getX1() + this.earthBitmap.getWidth(),
				earthLocs[i].getY1() + this.earthBitmap.getHeight());
	}
	public int findClosest()
	{
		int x = width/2 - this.earthBitmap.getWidth()/2;
		int closestDist = 999999;
		for(int i = earthLocs.length - 1; i >= 0; i--)
		{
			if(Math.abs(earthLocs[i].getX1() - x) < closestDist)
			{
				closestDist = earthLocs[i].getX1() - x;
			}
		}
		if(Math.abs(closestDist) > 1)
			return closestDist;
		return 0;
	}
	public int getEarthLocX()
	{
		return earthLocs[0].getX1();
	}
	public int getEarthBitmapWidth()
	{
		return earthBitmap.getWidth();
	}
	public int getSelectedLevels()
	{
		return selectedLevels;
	}
	public void setSelectedLevels(int n)
	{
		if((int)(n/8) + 1 > selectedLevels && (int)(n/8) + 1 < 13)
		{
			for(int i = 0; i < earthLocs.length; i++)
				earthLocs[i].x1 -= (width + width/2);
			selectedLevels = (int)(n/8) + 1;
		}
	}
	public String getMonth()
	{
		if(selectedLevels - 1 >= 0)
			return months[selectedLevels - 1];
		return "";
	}
	public boolean getBack()
	{
		return goBack;
	}
	public void setBack(boolean tf)
	{
		goBack = tf;
	}
	public boolean getPlayStage()
	{
		return playStage;
	}
	public void setPlayStage(boolean tf)
	{
		playStage = tf;
	}
	public void setLevelsComplete(int lc)
	{
		levelsComplete = lc;
	}
	public void draw(Canvas canvas)
	{
		monthPaint.setAlpha(paintTransparency);
		canvas.drawBitmap(sunBitmap, width/2 - (sunBitmap.getWidth()/2), height/3, null);
		for(int i = 0; i < earthLocs.length; i++)
		{
			canvas.drawBitmap(earthBitmap, earthLocs[i].getX1(), earthLocs[i].getY1(), null);
			if(!(i <= (levelsComplete / 8)))
			{
			canvas.drawBitmap(padlockBitmap, earthLocs[i].getX1() + (earthBitmap.getWidth()/2) - (padlockBitmap.getWidth()/2),
					earthLocs[i].getY1() + (earthBitmap.getHeight()/2) - (padlockBitmap.getHeight()/2), null);
			}
			//canvas.drawText(Integer.toString(i + 1), earthLocs[i].getX1(), earthLocs[i].getY1(), paint);
			canvas.drawText(months[i], earthLocs[i].getX1() + earthBitmap.getWidth()/2, height/10, monthPaint);
		}
		canvas.drawBitmap(returnBitmap, width/5 - returnBitmap.getWidth()/2, (height - height/7), null);
	}
}
