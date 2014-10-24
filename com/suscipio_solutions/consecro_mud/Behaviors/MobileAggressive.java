package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class MobileAggressive extends Mobile
{
	@Override public String ID(){return "MobileAggressive";}
	protected int tickWait=0;
	@Override public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE|Behavior.FLAG_TROUBLEMAKING;}
	protected boolean mobkill=false;
	protected boolean misbehave=false;
	protected String attackMsg=null;
	protected int aggressiveTickDown=0;
	protected VeryAggressive veryA=new VeryAggressive();

	public MobileAggressive()
	{
		super();

		tickDown = 0;
		aggressiveTickDown = 0;
	}

	@Override
	public String accountForYourself()
	{
		if(getParms().trim().length()>0)
			return "wandering aggression against "+CMLib.masking().maskDesc(getParms(),true).toLowerCase();
		else
			return "wandering aggressiveness";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		attackMsg=CMParms.getParmStr(newParms,"MESSAGE",null);
		tickDown=tickWait;
		aggressiveTickDown=tickWait;
		final Vector<String> V=CMParms.parse(newParms.toUpperCase());
		mobkill=V.contains("MOBKILL");
		misbehave=V.contains("MISBEHAVE");
	}
	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		if(M==null) return true;
		return CMLib.masking().maskCheck(getParms(),M,false);
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting, msg);
		veryA.executeMsg(affecting, msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		tickStatus=Tickable.STATUS_MISC+0;
		super.tick(ticking,tickID);
		tickStatus=Tickable.STATUS_MISC+1;
		if(tickID!=Tickable.TICKID_MOB)
		{
			tickStatus=Tickable.STATUS_NOT;
			return true;
		}
		if((--aggressiveTickDown)<0)
		{
			aggressiveTickDown=tickWait;
			tickStatus=Tickable.STATUS_MISC+2;
			veryA.tickAggressively(ticking,tickID,mobkill,misbehave,getParms(),attackMsg);
			tickStatus=Tickable.STATUS_MISC+3;
			veryA.tickVeryAggressively(ticking,tickID,wander,mobkill,misbehave,getParms(),attackMsg);
		}
		tickStatus=Tickable.STATUS_NOT;
		return true;
	}
}
