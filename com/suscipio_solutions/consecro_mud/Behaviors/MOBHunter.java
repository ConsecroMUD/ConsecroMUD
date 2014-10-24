package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class MOBHunter extends ActiveTicker
{
	@Override public String ID(){return "MOBHunter";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	@Override public long flags(){return Behavior.FLAG_MOBILITY|Behavior.FLAG_POTENTIALLYAGGRESSIVE;}
	protected boolean debug=false;
	int radius=20;

	@Override
	public String accountForYourself()
	{
		if(getParms().length()>0)
			return "hunters of  "+CMLib.masking().maskDesc(getParms());
		else
			return "creature hunting";
	}

	public MOBHunter()
	{
		super();
		minTicks=600; maxTicks=1200; chance=100; radius=15;
		tickReset();
	}

	protected boolean isHunting(MOB mob)
	{
		final Ability A=mob.fetchEffect("Thief_Assasinate");
		if(A!=null) return true;
		return false;
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		radius=CMParms.getParmInt(newParms,"radius",radius);
	}
	protected MOB findPrey(MOB mob)
	{
		MOB prey=null;
		final Vector rooms=new Vector();
		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags()
				.plus(TrackingLibrary.TrackingFlag.OPENONLY)
				.plus(TrackingLibrary.TrackingFlag.AREAONLY)
				.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
				.plus(TrackingLibrary.TrackingFlag.NOAIR)
				.plus(TrackingLibrary.TrackingFlag.NOWATER);
		CMLib.tracking().getRadiantRooms(mob.location(),rooms,flags,null,radius,null);
		for(int r=0;r<rooms.size();r++)
		{
			final Room R=(Room)rooms.elementAt(r);
			for(int i=0;i<R.numInhabitants();i++)
			{
				final MOB M=R.fetchInhabitant(i);
				if(CMLib.masking().maskCheck(getParms(),M,false))
				{
					prey=M;
					break;
				}
			}
		}
		return prey;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			final MOB mob=(MOB)ticking;
			if(debug) Log.sysOut("ZAPHUNT", "Tick starting");
			if(!isHunting(mob))
			{
				if(debug) Log.sysOut("ZAPHUNT", "'"+mob.Name()+"' not hunting.");
				final MOB prey=findPrey(mob);
				if(prey!=null)
				{
					if(debug) Log.sysOut("ZAPHUNT", "'"+mob.Name()+"' found prey: '"+prey.Name()+"'");
					final Ability A=CMClass.getAbility("Thief_Assassinate");
					A.setProficiency(100);
					mob.curState().setMana(mob.maxState().getMana());
					mob.curState().setMovement(mob.maxState().getMovement());
					A.invoke(mob, new Vector(), prey, false,0);
				}
			}
		}
		return true;
	}
}
