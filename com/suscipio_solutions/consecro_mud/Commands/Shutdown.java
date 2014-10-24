package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Shutdown extends StdCommand implements Tickable
{
	public Shutdown(){}

	private final String[] access=I(new String[]{"SHUTDOWN"});
	@Override public String[] getAccessWords(){return access;}
	protected MOB shuttingDownMob=null;
	protected long shuttingDownNextAnnounce=0;
	protected long shuttingDownCompletes=0;
	protected boolean keepItDown=true;
	protected String externalCommand=null;

	protected void showDisplayableShutdownTimeRemaining()
	{
		final long until = shuttingDownCompletes - System.currentTimeMillis();
		String tm = CMLib.time().date2EllapsedTime(until, TimeUnit.SECONDS, false);
		if((tm == null)||(tm.trim().length()==0))
			tm = " now";
		else
			tm=" in "+tm;
		for(final Session S : CMLib.sessions().allIterable())
		  S.colorOnlyPrintln(L("\n\r\n\r^Z@x1 will be @x2@x3^.^?\n\r",CMProps.getVar(CMProps.Str.MUDNAME),(keepItDown?"shutting down":"restarting"),tm));
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isMonster()) return false;
		boolean noPrompt=false;
		String externalCommand=null;
		boolean keepItDown=true;
		for(int i=commands.size()-1;i>=1;i--)
		{
			final String s=(String)commands.elementAt(i);
			if(s.equalsIgnoreCase("RESTART"))
			{ keepItDown=false; commands.removeElementAt(i);}
			else
			if(s.equalsIgnoreCase("NOPROMPT"))
			{ noPrompt=true; commands.removeElementAt(i); }
			else
			if(s.equalsIgnoreCase("CANCEL"))
			{
				if(shuttingDownMob==null)
				{
					mob.tell(L("Either no shutdown has been scheduled or is already underway and can't be cancelled."));
					return false;
				}
				shuttingDownMob=null;
				CMLib.threads().deleteTick(this, Tickable.TICKID_AREA);
			}
			else
			if((s.equalsIgnoreCase("IN"))&&(i==commands.size()-3))
			{
				noPrompt=true;
				commands.removeElementAt(i);
				final long wait=CMath.s_int((String)commands.get(i));
				commands.removeElementAt(i);
				final String multiplier=(String)commands.get(i);
				commands.removeElementAt(i);
				final long timeMultiplier=CMLib.english().getMillisMultiplierByName(multiplier);
				if((timeMultiplier<0)||(wait<=0))
				{
				   mob.tell(L("I don't know how to shutdown within the next @x1 @x2; try `5 minutes` or something similar.",""+wait,multiplier));
				   return false;
				}
				if((!mob.session().confirm(L("Shutdown @x1 in @x2 @x3 (y/N)?",CMProps.getVar(CMProps.Str.MUDNAME),""+wait,multiplier.toLowerCase()),L("N"))))
				   return false;
				shuttingDownCompletes=System.currentTimeMillis()+(wait * timeMultiplier)-1;
				shuttingDownNextAnnounce=System.currentTimeMillis() + ((wait * timeMultiplier)/2)-100;
				shuttingDownMob=mob;
				CMLib.threads().startTickDown(this, Tickable.TICKID_AREA, 1);
				showDisplayableShutdownTimeRemaining();
				return true;
			}
		}
		if((!keepItDown)&&(commands.size()>1))
			externalCommand=CMParms.combine(commands,1);

		if((!noPrompt)
		&&(!mob.session().confirm(L("Shutdown @x1 (y/N)?",CMProps.getVar(CMProps.Str.MUDNAME)),L("N"))))
			return false;
		shuttingDownMob=null;
		this.externalCommand=externalCommand;
		this.keepItDown=keepItDown;

		startShutdown(mob);
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.SHUTDOWN);}

	public void startShutdown(final MOB mob)
	{
		new Thread(Thread.currentThread().getThreadGroup(),"Shutdown"+Thread.currentThread().getThreadGroup().getName().charAt(0))
		{
			@Override
			public void run()
			{
				for(final Session S : CMLib.sessions().allIterable())
					S.colorOnlyPrintln(L("\n\r\n\r^Z@x1 is now @x2!^.^?\n\r",CMProps.getVar(CMProps.Str.MUDNAME),(keepItDown?"shutting down":"restarting")));
				if(keepItDown)
					Log.errOut("CommandProcessor",mob.Name()+" starts system shutdown...");
				else
				if(externalCommand!=null)
					Log.errOut("CommandProcessor",mob.Name()+" starts system restarting '"+externalCommand+"'...");
				else
					Log.errOut("CommandProcessor",mob.Name()+" starts system restart...");
				mob.tell(L("Starting @x1...",(keepItDown?"shutdown":"restart")));
				com.suscipio_solutions.consecro_mud.application.MUD.globalShutdown(mob.session(),keepItDown,externalCommand);
			}
		}.start();
	}

	@Override public int getTickStatus() { return Tickable.STATUS_ALIVE;}
	@Override public String name() { return super.ID(); }
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		final MOB mob=shuttingDownMob;
		if(mob==null) return false;
		if(System.currentTimeMillis() > shuttingDownCompletes)
		{
			startShutdown(mob);
			return false;
		}
		else
		if(System.currentTimeMillis() >= shuttingDownNextAnnounce)
		{
			shuttingDownNextAnnounce = System.currentTimeMillis() + ((shuttingDownCompletes - System.currentTimeMillis())/2)-100;
			showDisplayableShutdownTimeRemaining();
		}
		return true;
	}
}
