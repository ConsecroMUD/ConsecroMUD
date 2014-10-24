package com.suscipio_solutions.consecro_mud.Behaviors;
import java.lang.ref.WeakReference;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class MOBReSave extends ActiveTicker
{
	@Override public String ID(){return "MOBReSave";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	@Override public long flags(){return 0;}
	protected static HashSet roomsReset=new HashSet();
	protected boolean noRecurse=false;
	protected CharStats startStats=null;
	protected WeakReference host=null;

	public MOBReSave()
	{
		super();
		minTicks=140; maxTicks=140; chance=100;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "persisting";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		startStats=(CharStats)CMClass.getCommon("DefaultCharStats");
		for(final int c: CharStats.CODES.ALLCODES())
			startStats.setStat(c,CMParms.getParmInt(parms,CharStats.CODES.ABBR(c),-1));
	}

	@Override
	public String getParms()
	{
		if(host==null) return super.getParms();
		final MOB M=(MOB)host.get();
		if(M==null) return super.getParms();
		final StringBuffer rebuiltParms=new StringBuffer(super.rebuildParms());
		for(final int c: CharStats.CODES.ALLCODES())
			rebuiltParms.append(" "+CharStats.CODES.ABBR(c)+"="+M.baseCharStats().getStat(c));
		return rebuiltParms.toString();
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((ticking instanceof MOB)
		&&(tickID==Tickable.TICKID_MOB)
		&&(!((MOB)ticking).amDead())
		&&(!noRecurse)
		&&(CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
		&&(((MOB)ticking).getStartRoom()!=null)
		&&(((MOB)ticking).getStartRoom().roomID().length()>0)
		&&(((MOB)ticking).databaseID().length()>0))
		{
			final MOB mob=(MOB)ticking;
			if((host==null)||(host.get()==null))
				host=new WeakReference(mob);
			noRecurse=true;
			if(startStats != null)
			{
				synchronized(startStats)
				{
					for(final int c: CharStats.CODES.ALLCODES())
						if(startStats.getStat(c)>0)
							mob.baseCharStats().setStat(c,startStats.getStat(c));
					startStats=null;
				}
			}
			if(canAct(ticking,tickID))
			{
				final Room R=mob.getStartRoom();
				CMLib.database().DBUpdateMOB(R.roomID(),mob);
			}
		}
		noRecurse=false;
		return true;
	}


}
