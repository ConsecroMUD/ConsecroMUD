package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prop_ClosedSeason extends Property
{
	@Override public String ID() { return "Prop_ClosedSeason"; }
	@Override public String name(){ return "Contingent Visibility";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS|Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	protected Vector closedV=null;
	boolean doneToday=false;
	private Area exitArea=null;

	@Override
	public String accountForYourself()
	{ return "";	}

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void setMiscText(String text)
	{
		super.setMiscText(text);
		closedV=CMParms.parse(text.toUpperCase());
	}

	@Override
	public void executeMsg(Environmental E, CMMsg msg)
	{
		super.executeMsg(E,msg);
		if(exitArea!=null) return;
		if(!(affected instanceof Exit)) return;
		if(msg.source().location()!=null)
			exitArea=msg.source().location().getArea();
	}

	protected boolean closed(Area A)
	{
		if(A==null) return false;

		for(final Room.VariationCode code : Room.VariationCode.values())
		{
			if(closedV.contains(code.toString()))
				switch(code.c)
				{
				case 'W':
					if(A.getClimateObj().weatherType(null)==code.num)
						return true;
					break;
				case 'C':
					if(A.getTimeObj().getTODCode().ordinal()==code.num)
						return true;
					break;
				case 'S':
					if(A.getTimeObj().getSeasonCode().ordinal()==code.num)
						return true;
					break;
				}
		}
		return false;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(affected==null) return;
		if((affected instanceof MOB)||(affected instanceof Item))
		{
			final Room R=CMLib.map().roomLocation(affected);
			if((R!=null)
			&&(closed(R.getArea()))
			&&((!(affected instanceof MOB))||(!((MOB)affected).isInCombat())))
			{
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_NOT_SEEN);
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SEE);
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_MOVE);
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SPEAK);
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_HEAR);
			}
		}
		else
		if((affected instanceof Room)&&(closed(((Room)affected).getArea())))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_DARK);
		else
		if(affected instanceof Exit)
		{
			if(closed(exitArea==null?CMLib.map().getFirstArea():exitArea))
			{
				if(!doneToday)
				{
					doneToday=true;
					final Exit e=((Exit)affected);
					e.setDoorsNLocks(e.hasADoor(),false,e.defaultsClosed(),e.hasALock(),e.hasALock(),e.defaultsLocked());
				}
			}
			else
			{
				if(doneToday)
				{
					doneToday=false;
					final Exit e=((Exit)affected);
					e.setDoorsNLocks(e.hasADoor(),!e.defaultsClosed(),e.defaultsClosed(),e.hasALock(),e.defaultsLocked(),e.defaultsLocked());
				}
			}
		}

	}
}
