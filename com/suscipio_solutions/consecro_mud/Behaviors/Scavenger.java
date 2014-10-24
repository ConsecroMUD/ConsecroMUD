package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.ArrayList;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Scavenger extends ActiveTicker
{
	@Override public String ID(){return "Scavenger";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	int origItems=-1;

	public Scavenger()
	{
		super();
		minTicks=1; maxTicks=10; chance=99;
		origItems=-1;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "refuse scavenging";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			final MOB mob=(MOB)ticking;
			final Room thisRoom=mob.location();
			if(origItems<0) origItems=mob.numItems();
			if((mob.phyStats().weight()>=(int)Math.round(CMath.mul(mob.maxCarry(),0.9)))
			||(mob.numItems()>=mob.maxItems()))
			{
				if(CMLib.flags().isATrackingMonster(mob)) return true;
				final String trashRoomID=CMParms.getParmStr(getParms(),"TRASH","");
				if(trashRoomID.equalsIgnoreCase("NO"))
					return true;
				final Room R=CMLib.map().getRoom(trashRoomID);
				if(mob.location()==R)
				{
					Container C=null;
					int maxCapacity=0;
					for(int i=0;i<R.numItems();i++)
					{
						final Item I=R.getItem(i);
						if((I instanceof Container)&&(I.container()==null)&&(!CMLib.flags().isGettable(I)))
						{
							if(((Container)I).capacity()>maxCapacity)
							{
								C=(Container)I;
								maxCapacity=((Container)I).capacity();
							}
						}
					}
					if(C!=null)
						mob.doCommand(new XVector<String>("PUT","ALL",C.Name()),Command.METAFLAG_FORCED);
					else
						mob.doCommand(new XVector<String>("DROP","ALL"),Command.METAFLAG_FORCED);
					CMLib.tracking().wanderAway(mob,false,true);
				}
				else
				if(R!=null)
				{
					final Ability A=CMLib.flags().isTracking(mob) ? null : CMClass.getAbility("Skill_Track");
					if(A!=null)
						A.invoke(mob,CMParms.parse("\""+CMLib.map().getExtendedRoomID(R)+"\""),R,true,0);
				}
				else
				if((origItems>=0)&&(mob.numItems()>origItems))
				{
					while((origItems>=0)&&(mob.numItems()>origItems))
					{
						final Item I=mob.getItem(origItems);
						if(I==null)
						{
							if(origItems>0)
								origItems--;
							break;
						}
						if(I.owner()==null) I.setOwner(mob);
						I.destroy();
					}
					mob.recoverPhyStats();
					mob.recoverCharStats();
					mob.recoverMaxState();
				}
			}
			if((thisRoom==null)||(thisRoom.numItems()==0))
				return true;
			if(thisRoom.numPCInhabitants()>0)
				return true;
			List<Item> choices=new ArrayList<Item>(thisRoom.numItems()<1000?thisRoom.numItems():1000);
			for(int i=0;(i<thisRoom.numItems())&&(choices.size()<1000);i++)
			{
				final Item thisItem=thisRoom.getItem(i);
				if((thisItem!=null)
				&&(thisItem.container()==null)
				&&(CMLib.flags().isGettable(thisItem))
				&&(CMLib.flags().canBeSeenBy(thisItem, mob))
				&&(!(thisItem instanceof DeadBody)))
					choices.add(thisItem);
			}
			if(choices.size()==0) return true;
			final Item I=choices.get(CMLib.dice().roll(1,choices.size(),-1));
			if(I!=null)
				mob.doCommand(new XVector<String>("GET",I.Name()),Command.METAFLAG_FORCED);
			choices.clear();
			choices=null;
		}
		return true;
	}
}
