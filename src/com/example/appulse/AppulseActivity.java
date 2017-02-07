package com.example.appulse;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.*;

public class AppulseActivity extends Activity 
{
	private MainGamePanel gamePanel;
	private FileInputStream fis;
	private FileOutputStream fos;
	private String levelsCompleted = "1";
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try 
        {
			fis = openFileInput("saved");
			byte[] dataArray = new byte[fis.available()];
			while(fis.read(dataArray) != -1)
			{
				levelsCompleted = new String(dataArray);
			}
			fis.close();
		} 
        catch (Exception e) {}
        gamePanel = new MainGamePanel(this);
        gamePanel.setLevelsCompleted(levelsCompleted);
        setContentView(gamePanel);
        
    }
    @Override
    protected void onPause()
    {	
    	super.onPause();
    	levelsCompleted = gamePanel.getLevelsCompleted();
    	try
    	{
    		fos = openFileOutput("saved", Context.MODE_PRIVATE);
    		fos.write(levelsCompleted.getBytes());
    		fos.close();
    	}
    	catch(Exception e){}
    	gamePanel.pause();
    }
    
    @Override
    protected void onStop()
    {
    	super.onStop();
    	System.exit(0);
    }
    @Override
    protected void onResume()
    {
    	super.onResume();
    	gamePanel.resume();
    }
}
