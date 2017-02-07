package com.example.appulse;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

public class LevelMenu
{
	private Bitmap bitmap;
	private Bitmap earthBitmap;
	private Bitmap returnBitmap, padlockBitmap;
	private Rect returnRect;
	private boolean goBack = false, goBackPressed = false;
	private LinePoints[] levelPts;
	private float[] levelXs;
	private Rect[] boundingBoxes;
	private boolean[] moonTouched;
	private int level = 0;
	private int width, height;
	private int levelSelection;
	private Paint paint;
	private boolean playLevel = false;
	private int interLevel;
	private float startX;
	private boolean isTouching = false;
	private int movement;
	private String[] moonPhases;
	private int paintTransparency = 0;
	private Paint moonPaint;
	private String month;
	private int levelsComplete;
	
	public LevelMenu(Bitmap bitmap, Bitmap earthBitmap, Bitmap returnBitmap, Bitmap padlockBitmap, float scale, int width, int height, 
			int levelSelection, Typeface tf, String month, int levelsComplete)
	{
		this.width = width;
		this.height = height;
		this.levelSelection = levelSelection;
		this.bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width/2.5), (int)(width/2.5), false);
		this.earthBitmap = Bitmap.createScaledBitmap(earthBitmap, (int)(width/2.5), (int)(width/2.5), false);
		this.returnBitmap = Bitmap.createScaledBitmap(returnBitmap, width/6,width/6, false);
		returnRect = new Rect((int)(width/5 - this.returnBitmap.getWidth()/2), (int)(height - height/7),
				(int)(width/5 - this.returnBitmap.getWidth()/2) + this.returnBitmap.getWidth(),
				(int)(height - height/7) + this.returnBitmap.getHeight());
		this.padlockBitmap = Bitmap.createScaledBitmap(padlockBitmap, width/4, width/4, false);
		levelPts = new LinePoints[8];
		levelXs = new float[8];
		boundingBoxes = new Rect[8];
		moonTouched = new boolean[8];
		for (int i = 0; i < levelPts.length; i++)
		{
			levelPts[i] = new LinePoints(((i) * (width + width/2)) - this.bitmap.getWidth()/2 + width/2,
					height/2, 0, 0);
			boundingBoxes[i] = new Rect(levelPts[i].getX1(), levelPts[i].getY1(), 
					levelPts[i].getX1() + this.bitmap.getWidth(), levelPts[i].getY1() + this.bitmap.getHeight());
			moonTouched[i] = false;
		}
		this.levelsComplete = levelsComplete;
		
		moonPaint = new Paint();
		moonPaint.setColor(Color.WHITE);
		moonPaint.setTextSize(width/(8.33f));
		moonPaint.setTextAlign(Align.CENTER);
		moonPaint.setTypeface(tf);
		moonPaint.setAntiAlias(true);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(width/(15));
		paint.setTextAlign(Align.CENTER);
		paint.setTypeface(tf);
		paint.setAntiAlias(true);
		
		moonPhases = new String[8];
		moonPhases[0] = "New Moon";
		moonPhases[1] = "Waxing Crescent";
		moonPhases[2] = "First Quarter";
		moonPhases[3] = "Waxing Gibbous";
		moonPhases[4] = "Full Moon";
		moonPhases[5] = "Waning Gibbous";
		moonPhases[6] = "Last Quarter";
		moonPhases[7] = "Waning Crescent";
		
		this.month = month;
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
			for(int i = 0; i < levelXs.length; i++)
			{
				levelXs[i] = levelPts[i].getX1();
			}
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			for(int i = 0; i < levelPts.length; i++)
				levelPts[i].x1 = (int)((levelXs[i] + (3*event.getRawX()) - (3*startX)));
		}
		for(int i = 0; i < levelPts.length; i++)
		{
			if(i < levelsComplete%8 || levelSelection <= levelsComplete/8)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN &&
					boundingBoxes[i].contains((int)event.getRawX(),(int)event.getRawY()))
					moonTouched[i] = true;
			}		
		}
		for(int i = 0; i < levelPts.length; i++)
		{
			if(moonTouched[i] && event.getAction() == MotionEvent.ACTION_UP &&
								boundingBoxes[i].contains((int)event.getRawX(),(int)event.getRawY()))
			{	
				playLevel = true;
				interLevel = (i + 1);
				level = ((levelPts.length) * (levelSelection - 1)) + interLevel;
			}
			else if (event.getAction() == MotionEvent.ACTION_UP &&
				boundingBoxes[i].contains((int)event.getRawX(),(int)event.getRawY()))
				moonTouched[i] = false;
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
				for(int i = 0; i < levelPts.length; i++)
					levelPts[i].x1 -= movement/10;
			if(paintTransparency < 240)
			{
				paintTransparency += 10;
			}
		}
		else if(paintTransparency > 10)
		{
			paintTransparency -= 10;
		}
		for(int i = 0; i < levelPts.length; i++)
			boundingBoxes[i] = new Rect(levelPts[i].getX1(), levelPts[i].getY1(), 
				levelPts[i].getX1() + this.bitmap.getWidth(), levelPts[i].getY1() + this.bitmap.getHeight());
	}
	public int findClosest()
	{
		int x = width/2 - this.earthBitmap.getWidth()/2;
		int closestDist = 999999;
		for(int i = levelPts.length - 1; i >= 0; i--)
		{
			if(Math.abs(levelPts[i].getX1() - x) < closestDist)
			{
				closestDist = levelPts[i].getX1() - x;
			}
		}
		if(Math.abs(closestDist) > 1)
			return closestDist;
		return 0;
	}
	public void setMonth(String str)
	{
		month = str;
	}
	public int getLevelPtX()
	{
		return levelPts[0].getX1();
	}
	public int getMoonWidth()
	{
		return bitmap.getWidth();
	}
	public int getLevel()
	{
		return level;
	}
	public void setLevel(int lvl)
	{
		level = lvl;
	}
	public boolean getPlayLevel()
	{
		return playLevel;
	}
	public void setPlayLevel(boolean tf)
	{
		playLevel = tf;
	}
	public boolean getBack()
	{
		return goBack;
	}
	public void setBack(boolean tf)
	{
		goBack = tf;
	}
	public int getStage()
	{
		return levelSelection;
	}
	public int getInterLevel()
	{
		return interLevel;
	}
	public void goNextLevel()
	{
		if(level < 96)
		{
			if(interLevel == 8)
			{
				levelSelection += 1;
				interLevel = 1;
				for (int i = 0; i < levelPts.length; i++)
					levelPts[i].x1 += 7*(width+width/2);
			}
			else 
			{
				interLevel += 1;
				for(int i = 0; i < levelPts.length; i++)
					levelPts[i].x1 -= width + width/2;
			}
			level += 1;		
		}
	}
	public void setLevelsComplete(int lc)
	{
		levelsComplete = lc;
	}
	public void draw(Canvas canvas)
	{
		moonPaint.setAlpha(paintTransparency);
		paint.setAlpha(paintTransparency);
		canvas.drawBitmap(earthBitmap, width/2 - (earthBitmap.getWidth()/2), height/3, null);
		for(int i = 0; i < levelPts.length; i++)
		{
			canvas.drawBitmap(bitmap, levelPts[i].getX1(), levelPts[i].getY1(), null);
			if(!(i < levelsComplete%8) && levelSelection > levelsComplete/8)
			{
				canvas.drawBitmap(padlockBitmap, levelPts[i].getX1() + (bitmap.getWidth()/2) - (padlockBitmap.getWidth()/2),
						levelPts[i].getY1() + (bitmap.getHeight()/2) - (padlockBitmap.getHeight()/2), null);
			}
			//canvas.drawText(Integer.toString(i + 1), levelPts[i].getX1(),
					//levelPts[i].getY1(), paint);
			canvas.drawText(moonPhases[i], levelPts[i].getX1() + bitmap.getWidth()/2, height/7, moonPaint);
			canvas.drawText(month, levelPts[i].getX1() + bitmap.getWidth()/2, height/10 ,paint);
		}
		canvas.drawBitmap(returnBitmap, width/5 - returnBitmap.getWidth()/2, (height - height/7), null);
	}
}
