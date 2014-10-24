package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DoorKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class GateGuard extends StdBehavior
{
	@Override public String ID(){return "GateGuard";}

	protected int noticeTock=4;
	protected boolean heardKnock=false;
	protected boolean keepLocked=false;
	protected boolean allnight=false;

	@Override
	public String accountForYourself()
	{
		return "gate guarding";
	}

	@Override
	public void setParms(String parm)
	{
		super.setParms(parm);
		keepLocked=false;
		allnight=false;
		final Vector<String> V=CMParms.parse(parm);
		for(int v=0;v<V.size();v++)
		{
			if(V.elementAt(v).equalsIgnoreCase("keeplocked"))
			{
				keepLocked=true;
				V.removeElementAt(v);
				break;
			}
			else
			if(V.elementAt(v).equalsIgnoreCase("allnight"))
			{
				allnight=true;
				V.removeElementAt(v);
				break;
			}
		}
	}

	protected int findGate(MOB mob)
	{
		if(!CMLib.flags().isInTheGame(mob,false))
			return -1;
		final Room R=mob.location();
		if(R!=null)
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			if(R.getRoomInDir(d)!=null)
			{
				final Exit e=R.getExitInDir(d);
				if((e!=null)&&(e.hasADoor()))
					return d;
			}
		}
		return -1;
	}

	protected DoorKey getMyKeyTo(MOB mob, Exit e)
	{
		DoorKey key=null;
		final String keyCode=e.keyName();
		for(int i=0;i<mob.numItems();i++)
		{
			final Item item=mob.getItem(i);
			if((item instanceof DoorKey)&&(((DoorKey)item).getKey().equals(keyCode)))
			{
				key=(DoorKey)item;
				break;
			}
		}
		if(key==null)
		{
			key=(DoorKey)CMClass.getItem("StdKey");
			key.setKey(keyCode);
			mob.addItem(key);
		}
		return key;
	}

	protected int numValidPlayers(MOB mob, Room room)
	{
		if(room==null) return 0;
		int num=0;
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB M=room.fetchInhabitant(i);
			if((M!=null)
			&&(!M.isMonster())
			&&(CMLib.flags().canBeSeenBy(M,mob))
			&&(CMLib.masking().maskCheck(getParms(),M,false)))
				num++;
		}
		return num;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if(host instanceof MOB)
		{
			final MOB mob=(MOB)host;
			if((msg.targetMinor()==CMMsg.TYP_KNOCK)
			&&(!msg.amISource(mob))
			&&(mob.location()!=null)
			&&(mob.location()!=msg.source().location())
			&&(!heardKnock)
			&&(CMLib.flags().canHear(mob))
			&&(canFreelyBehaveNormal(host)))
			{
				final int dir=findGate(mob);
				if((dir>=0)
				&&(CMLib.masking().maskCheck(getParms(),msg.source(),false)))
				{
					final Exit e=mob.location().getExitInDir(dir);
					if(msg.amITarget(e))
						heardKnock=true;
				}
			}
		}
		super.executeMsg(host,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(!canFreelyBehaveNormal(ticking)) return true;
		final MOB mob=(MOB)ticking;
		final int dir=findGate(mob);
		if(dir<0) return true;
		final Exit e=mob.location().getExitInDir(dir);
		int numPlayers=numValidPlayers(mob,mob.location());
		if(noticeTock==0)
		{
			if(heardKnock) numPlayers++;
			if((!allnight)&&(mob.location().getArea().getTimeObj().getTODCode()==TimeClock.TimeOfDay.NIGHT))
			{
				if((!e.isLocked())&&(e.hasALock()))
				{
					if(getMyKeyTo(mob,e)!=null)
					{
						final CMMsg msg=CMClass.getMsg(mob,e,CMMsg.MSG_LOCK,L("<S-NAME> lock(s) <T-NAME>."));
						if(mob.location().okMessage(mob,msg))
							CMLib.utensils().roomAffectFully(msg,mob.location(),dir);
					}
				}
			}
			else
			{
				if((e.isLocked())&&((!keepLocked)||(numPlayers>0)))
				{
					if(getMyKeyTo(mob,e)!=null)
					{
						final CMMsg msg=CMClass.getMsg(mob,e,CMMsg.MSG_UNLOCK,L("<S-NAME> unlock(s) <T-NAME>."));
						if(mob.location().okMessage(mob,msg))
							CMLib.utensils().roomAffectFully(msg,mob.location(),dir);
					}
				}
				if((numPlayers>0)&&(!e.isOpen())&&(!e.isLocked()))
				{
					mob.doCommand(CMParms.parse("OPEN "+Directions.getDirectionName(dir)),Command.METAFLAG_FORCED);
				}
				if((numPlayers==0)&&(e.isOpen()))
				{
					mob.doCommand(CMParms.parse("CLOSE "+Directions.getDirectionName(dir)),Command.METAFLAG_FORCED);
				}
				if((numPlayers==0)&&(!e.isOpen())&&(!e.isLocked())&&(e.hasALock())&&(keepLocked))
				{
					if(getMyKeyTo(mob,e)!=null)
					{
						final CMMsg msg=CMClass.getMsg(mob,e,CMMsg.MSG_LOCK,L("<S-NAME> lock(s) <T-NAME>."));
						if(mob.location().okMessage(mob,msg))
							CMLib.utensils().roomAffectFully(msg,mob.location(),dir);
					}
				}
			}
			heardKnock=false;
			noticeTock--;
		}
		else
		if(noticeTock<0)
		{
			if(heardKnock) numPlayers++;
			if(mob.location().getArea().getTimeObj().getTODCode()==TimeClock.TimeOfDay.NIGHT)
				noticeTock=5;
			else
			if((e.isLocked())||((numPlayers==0)&&(e.isOpen())))
				noticeTock=3;
			else
			if((numPlayers>0)&&(!e.isOpen()))
				noticeTock=0;
		}
		else
			noticeTock--;
		return true;
	}
}
