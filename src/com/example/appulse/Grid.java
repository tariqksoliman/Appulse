package com.example.appulse;

import android.graphics.*;

public class Grid
{
	private int size, gridLength, points,
		squareLength, centerX, centerY, gridHalf,
		minX, maxX, minY,maxY;
	private LinePoints linePts[];
	private LinePoints[][] boxCenters;
	private Paint paint = new Paint();
	
	public Grid(int size, int width, int height)
	{
		this.size = size;
		centerX = width/2;
		centerY = height/2;
		points = (size * 4) + 4;
		gridLength = width - width/10;
		gridHalf = gridLength/2;
		minX = centerX - gridHalf;
		maxX = centerX + gridHalf;
		minY = centerY - gridHalf;
		maxY = centerY + gridHalf;
		squareLength = gridLength/size;
		linePts = new LinePoints[points/2];
		for(int i = 0; i < linePts.length/2; i++)
		{
			linePts[i] = new LinePoints((minX) + (i * squareLength), maxY,
					(minX) + (i * squareLength), minY);
		}
		for(int i = linePts.length/2; i < linePts.length; i++)
		{
			linePts[i] = new LinePoints(minX, (maxY) - ((i-linePts.length/2) * squareLength),
					maxX, (maxY) - ((i-linePts.length/2) * squareLength));
		}
		boxCenters = new LinePoints[size][size];
		for(int i = 0; i < size; i++)
		{
			for(int k = 0; k < size; k++)
			{
				boxCenters[i][k] = new LinePoints(minX + (k * squareLength) + (squareLength/2),
						minY + (i * squareLength) + (squareLength/2), 0, 0); 
			}
		}
		paint.setColor(Color.WHITE);
	}	
	
	public LinePoints[][] getBoxCenters()
	{
		return boxCenters;
	}
	public LinePoints getSpecificBoxCenter(int x, int y)
	{
		return boxCenters[x][y];
	}
	public int getSize()
	{
		return size;
	}
	public int getMinX()
	{
		return minX;
	}
	public int getMaxX()
	{
		return maxX;
	}
	public int getMinY()
	{
		return minY;
	}
	public int getMaxY()
	{
		return maxY;
	}
	public int getSquareLength()
	{
		return squareLength;
	}
	public void draw(Canvas canvas)
	{
		LinePoints l;
		for(int i = 0; i < linePts.length; i++)
		{
			l = linePts[i];
			canvas.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2(), paint);
		}
	}
}
