package com.suscipio_solutions.siplet.support;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;


public class MSPplayer extends Thread
{
	public String key=null;
	public int volume=100;
	public int repeats=1;
	public int iterations=0;
	public int priority=50;
	public int continueValue=1;
	public AudioClip clip=null;
	public String url=null;
	public boolean playing=false;
	public boolean orderedStopped=false;
	public String tag="soundplayer";
	private Object applet=null;

	public MSPplayer(Object theApplet)
	{
		super();
		applet=theApplet;
	}

	public String stopPlaying(String playerName, boolean useExternal)
	{
		if(playing)
		{
			orderedStopped=true;
			if(useExternal)
				return "StopSound('"+key+"','"+playerName+"');\n\r";
			if(clip!=null) clip.stop();
			try{Thread.sleep(50);}catch(final Exception e){}
			if(playing)
				interrupt();
		}
		return "";
	}

	@Override
	public void run()
	{
		playing=true;
		orderedStopped=false;
		try
		{
			if((clip !=null ) && (applet instanceof Applet))
			{
				if(url==null)
					clip=((Applet)applet).getAudioClip(((Applet)applet).getCodeBase(),key);
				else
					clip=((Applet)applet).getAudioClip(new URL(url+key));
			}
		}
		catch(final MalformedURLException m)
		{
			clip=null;
			playing=false;
			return;
		}
		if(clip!=null)
		{
			// dunno how to set volume, but that should go here.
			while((!orderedStopped)&&(iterations<repeats))
			{
				iterations++;
				clip.play();
			}
		}
		playing=false;
	}
	public String startPlaying(String playerName, boolean useExternal)
	{
		if(useExternal)
		{
			this.run();
			return "PlaySound('"+key+"','"+playerName+"','"+url+"',"+repeats+","+volume+","+priority+");\n\r";
		}
		else
		{
			this.start();
			return "";
		}
	}

}
