package com.example.appulse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
	private MainThread thread;
	private MainMenu mainMenu;
	private SelectionMenu selectionMenu;
	private LevelMenu levelMenu;
	private Tutorial tutorial;
	private Level level;
	private Bitmap background, earth, sun, return1, fullmoon, square,
		eclipse, restart, undo, next, padlock, credit;
	private boolean acceptTouch = false;
	private float backgroundX = 0;
	private float backgroundX2 = 0;
	private int firstX = 1, secondX = 2;
	private float backgroundSaved = 0;
	private Typeface tf;
	private MediaPlayer mp, mp2, mp3;
	private int levelsComplete = 1;
	
	private int gameState = 1;
	
	public MainGamePanel(Context context)
	{
		super(context);
		getHolder().addCallback(this);
		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		setFocusable(true);			
	}
	public void setLevelsCompleted(String lc)
	{
		int temp = Integer.parseInt(lc);
		if(temp > 0 && temp < 97)
			levelsComplete = temp;
		else levelsComplete = 1;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.setRunning(true);
		thread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		try
		{
			gameState = 0;
			thread.setRunning(false);
			thread.join();
			((Activity)getContext()).finish();
			System.exit(0);
		}
		catch (Exception e){}
	}
	
	public void initialize()
	{
		background = BitmapFactory.decodeResource(getResources(),R.drawable.starfield4);	
		background = Bitmap.createScaledBitmap(background, (int)(getHeight()*(background.getWidth()/background.getHeight())),
				getHeight(), false);
		
		sun = BitmapFactory.decodeResource(getResources(),R.drawable.sun256);
		fullmoon = BitmapFactory.decodeResource(getResources(),R.drawable.fullmoon256);
		return1 = BitmapFactory.decodeResource(getResources(),R.drawable.returnbtn256);
		padlock = BitmapFactory.decodeResource(getResources(),R.drawable.padlock256);
		credit = BitmapFactory.decodeResource(getResources(),R.drawable.credits128);
		tf = Typeface.createFromAsset(getContext().getAssets(), "font/motorwerkoblique.ttf");
		mainMenu = new MainMenu(credit, 1f, getWidth(), getHeight(), tf);
		earth = BitmapFactory.decodeResource(getResources(),R.drawable.earth256);
		selectionMenu = new SelectionMenu(earth, sun, return1, padlock, getWidth(), getHeight(), tf, levelsComplete);
		levelMenu = new LevelMenu(fullmoon, earth, return1, padlock, 1f, getWidth(), getHeight(), 1, tf,
				selectionMenu.getMonth(), levelsComplete);
		tutorial = new Tutorial(1, getWidth(),getHeight(), tf);
		mp3 = MediaPlayer.create(getContext(), R.raw.g3);
		mp = MediaPlayer.create(getContext(), R.raw.electroacid);
		mp.setLooping(true);
		mp.start();
		mp2 = MediaPlayer.create(getContext(), R.raw.eiland);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(acceptTouch)
		{
			synchronized (event)
			{
				try
				{
					//Waits 16ms.
					event.wait(16);
					switch (gameState)
					{
						case 0://quit
							thread.setRunning(false);
							thread.join();
							((Activity)getContext()).finish();
							break;
						case 1://menu screen
							mainMenu.onTouchEvent(event);
							break;
						case 2: //selection menu
							selectionMenu.onTouchEvent(event);
							break;
						case 3://level menu
							levelMenu.onTouchEvent(event);
							break;
						case 4://level screen
							level.onTouchEvent(event);
							break;
						case 5://tutorial/story
							tutorial.onTouchEvent(event);
							break;
					}				
				}
				catch (InterruptedException e)
				{
					return true;
				}
			}
		}
		return true;
	}
	
	public void update()
	{	
		switch (gameState)
		{
			case 1:
				if(mainMenu.getPlay() == true)
				{
					gameState = 2;
					mainMenu.setPlay(false);
					mp2.start();				
				}
				else if(mainMenu.getQuit() == true)
					gameState = 0;
				break;
			case 2:
				selectionMenu.update();
				selectionMenu.setLevelsComplete(levelsComplete);
				if(selectionMenu.getBack() == true)
				{
					gameState = 1;
					selectionMenu.setBack(false);
					mp2.start();
				}
				else if(selectionMenu.getPlayStage())
				{
					gameState = 3;
					backgroundSaved = backgroundX2;
					levelMenu = new LevelMenu(fullmoon,earth, return1, padlock, 1f, getWidth(), getHeight(),
							selectionMenu.getSelectedLevels(), tf,selectionMenu.getMonth(), levelsComplete);
					selectionMenu.setPlayStage(false);
					mp2.start();
				}	
				backgroundX = - selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12;
				break;
			case 3:
				levelMenu.update();
				levelMenu.setLevelsComplete(levelsComplete);
				if(levelMenu.getPlayLevel() && levelMenu.getLevel() == 1)
				{
					gameState = 5;
					levelMenu.setPlayLevel(false);
					mp2.start();
					break;
				}
				else if(levelMenu.getPlayLevel()) 
				{					
					earth = null;
					square = BitmapFactory.decodeResource(getResources(),R.drawable.device2);
					eclipse = BitmapFactory.decodeResource(getResources(),R.drawable.eclipse256);
					restart = BitmapFactory.decodeResource(getResources(),R.drawable.restartbtn256);
					undo = BitmapFactory.decodeResource(getResources(),R.drawable.undobtn256);
					next = BitmapFactory.decodeResource(getResources(),R.drawable.nextbtn256);
					level = new Level(square, fullmoon,sun,eclipse, return1,restart,undo,next, mp3,
							1f, getWidth(), getHeight(),tf, levelMenu.getLevel(),
							levelMenu.getStage(), levelMenu.getInterLevel(), levelsComplete);
					levelMenu.setPlayLevel(false);
					mp2.start();
					gameState = 4;
				}
				if(levelMenu.getBack())
				{
					gameState = 2;
					levelMenu.setBack(false);
					mp2.start();
				}
				backgroundX2 = backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12;
				break;
			case 4://level screen
				level.update();
				levelsComplete = level.getLevelsComplete();
				if(level.getBack() == true)
				{
					gameState = 3;
					levelMenu.setMonth(selectionMenu.getMonth());
					level.setBack(false);
					mp2.start();
					square = null;
					eclipse = null;
					undo = null;
					restart = null;
					next = null;
					earth = BitmapFactory.decodeResource(getResources(),R.drawable.earth256);
				}
				if(level.getRestart() == true)
				{
					level = new Level(square, fullmoon,sun,eclipse, return1,restart,undo,next, mp3,
							1f, getWidth(), getHeight(),tf, levelMenu.getLevel(),
							levelMenu.getStage(), levelMenu.getInterLevel(), levelsComplete);
					level.setRestart(false);
					mp2.start();
				}
				if(level.getNext() == true)
				{	
					selectionMenu.setSelectedLevels(level.getOverallLevel());				
					levelMenu.goNextLevel();
					level = new Level(square, fullmoon,sun,eclipse, return1,restart,undo,next, mp3,
							1f, getWidth(), getHeight(),tf, levelMenu.getLevel(),
							levelMenu.getStage(), levelMenu.getInterLevel(), levelsComplete);
					level.setNext(false);				
					mp2.start();
				}
				if(Math.abs(backgroundX2 - (backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12)) > 1)
				{
					if(backgroundX2 < backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12)
						backgroundX2 += Math.abs(backgroundX2 - (backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12))/20;
					else if(backgroundX2 > backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12)
						backgroundX2 -= Math.abs(backgroundX2 - (backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12))/20;
				}
				else backgroundX2 = backgroundSaved - levelMenu.getLevelPtX()/9 + levelMenu.getMoonWidth()/12;
				if(Math.abs(backgroundX -(- selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12)) > 1)
				{
					if(backgroundX < - selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12)
						backgroundX += Math.abs(backgroundX -(- selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12))/20;
					else if(backgroundX > - selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12)
						backgroundX -= Math.abs(backgroundX -(- selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12))/20;
				}
				else backgroundX = - selectionMenu.getEarthLocX()/9 + selectionMenu.getEarthBitmapWidth()/12;
				break;
			case 5:
				if(!tutorial.isShowing())
				{				
					square = BitmapFactory.decodeResource(getResources(),R.drawable.device2);
					eclipse = BitmapFactory.decodeResource(getResources(),R.drawable.eclipse256);
					restart = BitmapFactory.decodeResource(getResources(),R.drawable.restartbtn256);
					undo = BitmapFactory.decodeResource(getResources(),R.drawable.undobtn256);
					next = BitmapFactory.decodeResource(getResources(),R.drawable.nextbtn256);
					level = new Level(square, fullmoon,sun,eclipse, return1, restart,undo,next, mp3,
							1f, getWidth(), getHeight(),tf, levelMenu.getLevel(),
							levelMenu.getStage(), levelMenu.getInterLevel(), levelsComplete);
					tutorial.setShowing(true);
					mp2.start();
					gameState = 4;
				}
				break;
		}
		if(secondX > firstX && (int)backgroundX + backgroundX2
				- (firstX*background.getWidth()) + getWidth() + (getWidth()/2) > (getWidth()*2))
			firstX += 2;
		else if(firstX > secondX && (int)backgroundX + backgroundX2
				- (secondX*background.getWidth()) + getWidth() + (getWidth()/2) > (getWidth()*2))
			secondX += 2;
		else if(secondX > firstX && (int)backgroundX + backgroundX2
				- (secondX*background.getWidth()) + getWidth() + (getWidth()/2) < (int)(-background.getWidth()*1.5))
			secondX -= 2;
		else if(firstX > secondX && (int)backgroundX + backgroundX2
				- (firstX*background.getWidth()) + getWidth() + (getWidth()/2) < (int)(-background.getWidth()*1.5))
			firstX -= 2;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(thread.getRunning())
		{
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(background, (int)backgroundX + backgroundX2
					- (firstX*background.getWidth()) + getWidth() + (getWidth()/2),0,null);
			canvas.drawBitmap(background, (int)backgroundX + backgroundX2
					- (secondX*background.getWidth())+ getWidth()+(getWidth()/2),0,null);
			switch(gameState)
			{
				case 1://menu screen
					mainMenu.draw(canvas);
					break;
				case 2: //selection menu
					selectionMenu.draw(canvas);
					break;
				case 3://level menu
					levelMenu.draw(canvas);
					break;
				case 4://level screen
					level.draw(canvas);
					break;
				case 5://tutorial
					tutorial.draw(canvas);
					break;
			}
			if(!acceptTouch) acceptTouch = true;
		}
	}	
	public void pause()
	{
		mp.pause();
	}
	public void resume()
	{
		boolean retry = true;
		while(retry)
		{
			try
			{
				mp.start();			
			}
			catch(Exception e){}
			retry = false;			
		}	
	}
	public String getLevelsCompleted()
	{
		return Integer.toString(levelsComplete);
	}
}
