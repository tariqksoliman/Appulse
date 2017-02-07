package com.example.appulse;

import java.util.ArrayList;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.view.MotionEvent;

public class Level
{
	private int[][] level;
	private LevelData levelData;
	private int size;
	private int tempType = 0;
	private int width, height;
	private Grid grid;
	private int sl;//grid square length
	private Paint paint = new Paint();
	private Paint eclipsePaint = new Paint();
	private Paint rectPaint;
	private Paint textPaint;
	private Paint fullPaint;
	private ArrayList<Square> squares = new ArrayList<Square>();
	private Square playerSquare;
	private Rect goalSquare;
	private LinePoints linePts;
	private int goalX, goalY;
	private boolean finishedLevel = false;
	private Bitmap bitmap;
	private Bitmap moonBitmap;
	private Bitmap sunBitmap;
	private Bitmap eclipseBitmap;
	private Rect fullRect;
	private Bitmap returnBitmap;
	private Rect returnRect;
	private Bitmap restartBitmap;
	private Rect restartRect;
	private Bitmap undoBitmap;
	private Rect undoRect;
	private Bitmap nextBitmap;
	private boolean restartLevel = false;
	private boolean goBack = false, goBackPressed = false;
	private boolean goToNextLevel = false;
	private MediaPlayer mp3;
	private ArrayList<String> undos;
	private int stage, interLevel;
	private int lvl;
	private Rect rect;
	private String[] text;
	private int levelsComplete;
	
	public Level(Bitmap bitmap,Bitmap moonBitmap, Bitmap sunBitmap, Bitmap eclipseBitmap,
			Bitmap returnBitmap, Bitmap restartBitmap, Bitmap undoBitmap, Bitmap nextBitmap, MediaPlayer mp3,
			float scale, int width, int height, Typeface tf, int lvl, int stage, int interLevel, int levelsComplete)
	{		
		this.width = width;
		this.height = height;
		this.levelsComplete = levelsComplete;
		levelData = new LevelData();
		this.lvl = lvl;
		if(lvl == 1)
		{
			text = new String[1];
			rect = new Rect(0, height/20, width, height/10);
			text[0] = "Slide the moon downward to blot out the sun";
		}
		else if(lvl == 2)
		{
			text = new String[7];
			rect = new Rect(0, height/20, width, height/4 - height/50);
			text[0] = "Now try sliding the moon over the sun. You can't.";
			text[1] = "This is because there is nothing to stop the moon";
			text[2] = "from floating off into space or worse from crashing";
			text[3] = "into Earth. Here's where our devices come into play.";
			text[4] = "Slide one of the devices towards the other. Now try";
			text[5] = "sliding the moon. Remember we don't want our";
			text[6] = "devices to drift off into space either.";
			
		}
		else if(lvl == 7)
		{
			text = new String[1];
			rect = new Rect(0, height/20, width, height/10);
			text[0] = "Order matters.";
		}
		else if(lvl == 9)
		{
			text = new String[2];
			rect = new Rect(0, height/20, width, height/9);
			text[0] = "Remember the sun is far away. Both the Moon";
			text[1] = "and our devices can pass over the Sun.";
		}
		else if(lvl == 11)
		{
			text = new String[2];
			rect = new Rect(0, height/20, width, height/9);
			text[0] = "Symmetry guarantees more than one way to";
			text[1] = "complete a level.";
		}
		else if(lvl == 17)
		{
			text = new String[2];
			rect = new Rect(0, height/20, width, height/9);
			text[0] = "Don't be fooled if you only have to";
			text[1] = "move the Moon...";
		}
		else if(lvl == 18)
		{
			text = new String[1];
			rect = new Rect(0, height/20, width, height/10);
			text[0] = "...or by the extra devices.";
		}
		level = levelData.getLevel(lvl);
		size = level[0].length;
		grid = new Grid(size, width, height);
		sl = grid.getSquareLength();
		this.stage = stage;
		this.interLevel = interLevel;
		this.bitmap = Bitmap.createScaledBitmap(bitmap, sl, sl, false);
		this.moonBitmap = Bitmap.createScaledBitmap(moonBitmap, sl, sl, false);
		this.sunBitmap = Bitmap.createScaledBitmap(sunBitmap,sl, sl, false);
		this.eclipseBitmap = Bitmap.createScaledBitmap(eclipseBitmap, width/2, width/2, false);
		fullRect = new Rect(0, 0, width, height);
		this.returnBitmap = Bitmap.createScaledBitmap(returnBitmap, width/6, width/6, false);
		returnRect = new Rect((int)(width/5) - this.returnBitmap.getWidth()/2, (int)(height - height/7),
				(int)(width/5) + this.returnBitmap.getWidth()/2,
				(int)(height - height/7) + this.returnBitmap.getHeight());
		this.restartBitmap = Bitmap.createScaledBitmap(restartBitmap, width/6,
				width/6, false);
		restartRect = new Rect((int)(width/2) - this.restartBitmap.getWidth()/2, (int)(height - height/7),
				(int)(width/2) + this.restartBitmap.getWidth()/2,
				(int)(height - height/9) + this.restartBitmap.getHeight());
		this.undoBitmap = Bitmap.createScaledBitmap(undoBitmap, width/6,width/6, false);
		undoRect = new Rect((int)(width - width/5) - this.undoBitmap.getWidth()/2, (int)(height - height/7),(
				int)(width - width/5) + this.undoBitmap.getWidth()/2,
				(int)(height - height/9) + this.undoBitmap.getHeight());
		this.nextBitmap = Bitmap.createScaledBitmap(nextBitmap, (int)width/6,width/6, false);
		for(int i = 0; i < size; i++)
		{
			for(int k = 0; k < size; k++)
			{
				tempType = level[i][k];
				if(tempType == 2)
					squares.add(new Square(this.bitmap, grid, 0.9f,i,k, tempType));
				else if(tempType == 1)
					playerSquare = new Square(this.moonBitmap, grid, 1,i,k, tempType);
				else if(tempType == -1)
				{
					goalX = i;
					goalY = k;
					level[i][k] = 0;
				}
			}
		}
		linePts = grid.getSpecificBoxCenter(goalX, goalY);
		int rectX = linePts.getX1() - grid.getSquareLength()/2;
		int rectY = linePts.getY1() - grid.getSquareLength()/2;
		goalSquare = new Rect(rectX, rectY, rectX + grid.getSquareLength(), rectY + grid.getSquareLength());
		undos = new ArrayList<String>();
		paint.setColor(Color.WHITE);
		paint.setTextSize(width/(15));
		paint.setTypeface(tf);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		eclipsePaint.setColor(Color.WHITE);
		eclipsePaint.setTextSize(width/(7.5f));
		eclipsePaint.setTypeface(tf);
		eclipsePaint.setAntiAlias(true);
		eclipsePaint.setTextAlign(Align.CENTER);
		
		rectPaint = new Paint();
		rectPaint.setARGB(120, 30, 30, 30);
		fullPaint = new Paint();
		fullPaint.setARGB(140, 0, 0, 0);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(width/(24));
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Align.CENTER);
		
		this.mp3 = mp3;
	}
	
	public Level(){}
	
	public void onTouchEvent(MotionEvent event)
	{
		playerSquare.onTouchEvent(event);
		for(int i = 0; i < squares.size(); i++)
			squares.get(i).onTouchEvent(event);
		if(!goBackPressed && event.getAction() == MotionEvent.ACTION_DOWN &&
				returnRect.contains((int)event.getRawX(),(int)event.getRawY()))
			goBackPressed = true;
		if(goBackPressed && event.getAction() == MotionEvent.ACTION_UP &&
				returnRect.contains((int)event.getRawX(),(int)event.getRawY()))
		{
			goBack = true;
			goBackPressed = false;
		}
		else if((goBackPressed && event.getAction() == MotionEvent.ACTION_UP))
				goBackPressed = false;
		if(event.getAction() == MotionEvent.ACTION_DOWN &&
				restartRect.contains((int)event.getRawX(),(int)event.getRawY()))
		{
			restartLevel = true;
		}
		if(!finishedLevel && event.getAction() == MotionEvent.ACTION_DOWN &&
				undoRect.contains((int)event.getRawX(),(int)event.getRawY()))
		{
			undo();
		}
		if(finishedLevel && event.getAction() == MotionEvent.ACTION_DOWN &&
				undoRect.contains((int)event.getRawX(),(int)event.getRawY()))
		{
			goToNextLevel = true;
		}
	}
	public void update()
	{
		if(!finishedLevel)
		{
			playerSquare.update(width, height, getMoveDist(playerSquare), goalX, goalY);
			for(int i = 0; i < squares.size(); i++)
				squares.get(i).update(width, height, getMoveDist(squares.get(i)), goalX, goalY);
			if(playerSquare.isLevelFinished())
			{
				finishedLevel = true;
				mp3.start();
				if(lvl == levelsComplete)
				{
					levelsComplete++;
				}
			}
		}
	}
	public int getMoveDist(Square square)
	{
		int direction = square.getDirection();
		int x = square.getXBox();
		int y = square.getYBox();
		int x2 = square.getXBox();
		int y2 = square.getYBox();
		int swapType;
		if(direction == 1 && y != size - 1)//Down
		{
			while(level[x][y + 1] == 0)
			{
				y++;
				if(y >= size - 1)
				{
					return 0;
				}
			}
			swapType = level[x2][y2];
			level[x2][y2] = 0;
			level[x][y] = swapType;
			undos.add(Integer.toString(x)+Integer.toString(y)+"2"+Integer.toString(y-y2));
			return y - y2;
		}	
		else if(direction == 2 && y != 0)//Up
		{
			while(level[x][y - 1] == 0)
			{
				y--;
				if(y <= 0)
				{
					return 0;
				}
			}
			swapType = level[x2][y2];
			level[x2][y2] = 0;
			level[x][y] = swapType;
			undos.add(Integer.toString(x)+Integer.toString(y)+"1"+Integer.toString(y2-y));
			return y2 - y;
		}
		else if(direction == 3 && x != size - 1)//Right x increasing
		{
			while(level[x + 1][y] == 0)
			{
				x++;
				if(x >= size - 1)
				{
					return 0;
				}
			}
			swapType = level[x2][y2];
			level[x2][y2] = 0;
			level[x][y] = swapType;
			undos.add(Integer.toString(x)+Integer.toString(y)+"4"+Integer.toString(x-x2));
			return x - x2;
		}		
		else if(direction == 4 && x != 0)//Left x decreasing
		{
			while(level[x - 1][y] == 0)
			{
				x--;
				if(x <= 0)
				{
					return 0;
				}
			}
			swapType = level[x2][y2];
			level[x2][y2] = 0;
			level[x][y] = swapType;
			undos.add(Integer.toString(x)+Integer.toString(y)+"3"+Integer.toString(x2-x));
			return x2 - x;
		}
		return 0;
	}
	public void undo()//x y d m
	{
		if(!finishedLevel && undos.size() > 0)
		{
			int swapType;
			String undoStr = undos.get(undos.size() - 1);
			undos.remove(undos.size() -1);
			int x = Integer.parseInt(undoStr.substring(0,1));
			int y = Integer.parseInt(undoStr.substring(1,2));
			int x2, y2;
			int direction = Integer.parseInt(undoStr.substring(2,3));
			int movement = Integer.parseInt(undoStr.substring(3));
			if(direction == 1)
			{
				y2 = y + movement;
				x2 = x;
			}
			else if(direction == 2)
			{
				y2 = y - movement;
				x2 = x;
			}
			else if(direction == 3)
			{
				x2 = x + movement;
				y2 = y;
			}
			else
			{
				x2 = x - movement;
				y2 = y;
			}
			swapType = level[x][y];
			level[x][y] = 0;
			level[x2][y2] = swapType;
			if(playerSquare.getXBox() == x && playerSquare.getYBox() == y)
			{
				playerSquare.setDirection(direction);
				playerSquare.update(width, height, movement, goalX, goalY);
				
			}
			else
			{
				for(int i = 0; i < squares.size(); i++)
				{
					if(squares.get(i).getXBox() == x && squares.get(i).getYBox() == y)
					{
						squares.get(i).setDirection(direction);
						squares.get(i).update(width, height, movement, goalX, goalY);
						break;
					}
				}
			}
		}
	}
	public boolean getBack()
	{
		return goBack;
	}
	public void setBack(boolean tf)
	{
		goBack = tf;
	}
	public boolean getNext()
	{
		return goToNextLevel;
	}
	public void setNext(boolean tf)
	{
		goToNextLevel = tf;
	}
	public boolean getRestart()
	{
		return restartLevel;
	}
	public void setRestart(boolean tf)
	{
		restartLevel = tf;
	}
	public int getOverallLevel()
	{
		return lvl;
	}
	public int getLevelsComplete()
	{
		return levelsComplete;
	}
	
	public void draw(Canvas canvas)
	{
		if(!finishedLevel)
		{
			canvas.drawBitmap(sunBitmap, goalSquare.left, goalSquare.top, null);
			//grid.draw(canvas);
			for(int i = 0; i < squares.size(); i++)
				squares.get(i).draw(canvas);
			playerSquare.draw(canvas);
			canvas.drawBitmap(undoBitmap, (width - width/5) - undoBitmap.getWidth()/2, (height - height/7), null);
			if(lvl == 1 || lvl == 7 || lvl == 18)
			{
				canvas.drawRect(rect, rectPaint);
				canvas.drawText(text[0], width/2,rect.top + height/30, textPaint);
			}
			else if(lvl == 2 || lvl == 9 || lvl == 11 || lvl == 17)
			{
				canvas.drawRect(rect, rectPaint);
				for (int i = 0; i < text.length; i++)
					canvas.drawText(text[i], width/2,
							rect.top + (i * textPaint.getTextSize()) + height/30, textPaint);
			}
		}
		if(finishedLevel)
		{		
			canvas.drawRect(fullRect, fullPaint);
			canvas.drawBitmap(eclipseBitmap, width/2 - eclipseBitmap.getWidth()/2, height/3, null);
			canvas.drawText("Total Eclipse", width/2, height/2, eclipsePaint);
			canvas.drawBitmap(nextBitmap, (width - width/5) - undoBitmap.getWidth()/2, (height - height/7), null);
		}
		canvas.drawBitmap(returnBitmap, width/5 - returnBitmap.getWidth()/2, (height - height/7), null);
		canvas.drawBitmap(restartBitmap, width/2 - restartBitmap.getWidth()/2, (height - height/7), null);
		canvas.drawText(Integer.toString(stage) + "-" + Integer.toString(interLevel),
				width/2, height/30 ,paint);
	}
}