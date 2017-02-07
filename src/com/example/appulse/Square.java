package com.example.appulse;

import android.graphics.*;
import android.view.MotionEvent;

public class Square
{
	private Bitmap bitmap;
	private Rect boundingBox;
	private LinePoints[][] boxCenters;
	private boolean checkForRelease = false;
	private boolean yDown = false, yUp = false, xRight = false, xLeft = false;
	private int xBox, yBox, boxMax;
	private float x = -1000, y = -1000;
	private float initialX = 0, initialY = 0, deltaX = 0, deltaY = 0;
	private int type;
	private boolean finishedLevel = false;
	private boolean isMoving = false;
	
	public Square(Bitmap bitmap, Grid grid, float scale,int xBox, int yBox, int type)
	{
		this.bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * scale),
				(int)(bitmap.getHeight() * scale), false);
		boxCenters = grid.getBoxCenters();
		boxMax = grid.getSize() - 1;
		boundingBox = new Rect((int)x, (int)y, (int)x+this.bitmap.getWidth(),
				(int)y+this.bitmap.getHeight());
		this.xBox = xBox;
		this.yBox = yBox;
		this.type = type;
	}	

	public void onTouchEvent(MotionEvent event)
	{
		if(!isMoving && event.getAction() == MotionEvent.ACTION_DOWN &&
				boundingBox.contains((int)event.getRawX(),(int)event.getRawY()))
		{
			//reset deltaX and deltaY
			deltaX = deltaY = 0;

			//get initial positions
			initialX = event.getRawX();
			initialY = event.getRawY();
			checkForRelease = true;
		}
		
		//when screen is released
		if(checkForRelease && event.getAction() == MotionEvent.ACTION_UP)
		{	
			deltaX = event.getRawX() - initialX;
			deltaY = event.getRawY() - initialY;

			//swiped up
			if(deltaY > 0 && deltaY >= Math.abs(deltaX))
			{	
				//yUp = true;
				xRight = true;
			}
			else if(deltaY < 0 && Math.abs(deltaY) >= Math.abs(deltaX))
			{
				//yDown = true;
				xLeft = true;
			}
			else if(deltaX > 0 && deltaX > Math.abs(deltaY))
			{
				//xRight = true;
				yUp = true;
			}
			else if(deltaX < 0 && Math.abs(deltaX) > Math.abs(deltaY))
			{
				//xLeft = true;
				yDown = true;
			}
			isMoving = true;
			checkForRelease = false;
		}
	}
	
	public int getXBox()
	{
		return xBox;
	}
	public int getYBox()
	{
		return yBox;
	}
	public int getDirection()
	{
		if(yUp) return 1;
		if(yDown) return 2;
		if(xRight) return 3;
		if(xLeft) return 4;
		return 0;
	}
	public void setDirection(int n)
	{
		yUp = false; yDown = false; xRight = false; xLeft = false;
		if(n == 1) yUp = true;
		else if(n == 2) yDown = true;
		else if(n == 3) xRight = true;
		else xLeft = true;
	}
	public boolean isLevelFinished()
	{
		return finishedLevel;
	}
	
	public void update(int width, int height, int moveDist, int goalX, int goalY)
	{	
		if(moveDist != 0)
		{
			if(yBox >= boxMax) yUp = false;
			else if(yUp) yBox += moveDist;
			if(yBox <= 0) yDown = false;
			else if(yDown) yBox -= moveDist;
			if(xBox >= boxMax) xRight = false;
			else if(xRight) xBox += moveDist;
			if(xBox <= 0) xLeft = false;
			else if(xLeft) xBox -= moveDist;
			
		}
		if(type == 1)
			if(yBox == goalY && xBox == goalX
			&& (x == boxCenters[xBox][yBox].getX1() - bitmap.getWidth()/2)
			&& (y == boxCenters[xBox][yBox].getY1() - bitmap.getHeight()/2))
				finishedLevel = true;	
		yUp = false; yDown = false; xRight = false; xLeft = false;
		if(isMoving)
		{
			x += (boxCenters[xBox][yBox].getX1() - bitmap.getWidth()/2 - x)/5;
			y += (boxCenters[xBox][yBox].getY1() - bitmap.getHeight()/2 - y)/5;
		}
		else
		{
			x = boxCenters[xBox][yBox].getX1() - bitmap.getWidth()/2;
			y = boxCenters[xBox][yBox].getY1() - bitmap.getHeight()/2;
		}
		if(Math.abs(x - (boxCenters[xBox][yBox].getX1() - bitmap.getWidth()/2)) < 4 &&
				Math.abs(y - (boxCenters[xBox][yBox].getY1() - bitmap.getHeight()/2)) < 4)
		{
			isMoving = false;
		}
		boundingBox.set((int)x, (int)y, (int)x+bitmap.getWidth(), (int)y+bitmap.getHeight());
	}
	
	public void draw(Canvas canvas)
	{
		if(type == 2)
			canvas.drawBitmap(bitmap, x, y, null);
		else if(type == 1)
		{
			canvas.drawBitmap(bitmap, x, y, null);
		}
		else 
		{
			Paint paint = new Paint();
			paint.setARGB(200,200,10,10);
			canvas.drawBitmap(bitmap, x, y, paint);
			canvas.drawRect(boundingBox, paint);
		}
			
	}
}
