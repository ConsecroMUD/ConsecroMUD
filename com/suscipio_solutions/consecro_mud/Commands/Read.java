package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Read extends StdCommand
{
	public Read(){}

	private final String[] access=I(new String[]{"READ"});
	@Override public String[] getAccessWords(){return access;}

	private final static Class[][] internalParameters=new Class[][]{{Environmental.class,String.class,Boolean.class}};

	public boolean read(MOB mob, Environmental thisThang, String theRest, boolean quiet)
	{
		if((thisThang==null)||((!(thisThang instanceof Item)&&(!(thisThang instanceof Exit))))||(!CMLib.flags().canBeSeenBy(thisThang,mob)))
		{
			mob.tell(L("You don't seem to have that."));
			return false;
		}
		if(thisThang instanceof Item)
		{
			final Item thisItem=(Item)thisThang;
			if((CMLib.flags().isGettable(thisItem))&&(!mob.isMine(thisItem)))
			{
				mob.tell(L("You don't seem to be carrying that."));
				return false;
			}
		}
		final String srcMsg="<S-NAME> read(s) <T-NAMESELF>.";
		final String soMsg=(mob.isMine(thisThang)?srcMsg:null);
		String tMsg=theRest;
		if((tMsg==null)||(tMsg.trim().length()==0)||(thisThang instanceof MOB)) tMsg=soMsg;
		final CMMsg newMsg=CMClass.getMsg(mob,thisThang,null,CMMsg.MSG_READ,quiet?srcMsg:null,CMMsg.MSG_READ,tMsg,CMMsg.MSG_READ,quiet?null:soMsg);
		if(mob.location().okMessage(mob,newMsg))
		{
			mob.location().send(mob,newMsg);
			return true;
		}
		return false;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Read what?"));
			return false;
		}
		commands.removeElementAt(0);
		if(commands.firstElement() instanceof Environmental)
		{
			read(mob,(Environmental)commands.firstElement(),CMParms.combine(commands,1), false);
			return false;
		}

		final int dir=Directions.getGoodDirectionCode(CMParms.combine(commands,0));
		Environmental thisThang=null;
		if(dir>=0)	thisThang=mob.location().getExitInDir(dir);
		thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.lastElement(), StdCommand.noCoinFilter);
		if(thisThang==null)
			thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.lastElement(),Wearable.FILTER_ANY);
		String theRest=null;
		if(thisThang==null)
			thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,CMParms.combine(commands,0),Wearable.FILTER_ANY);
		else
		{
			commands.removeElementAt(commands.size()-1);
			theRest=CMParms.combine(commands,0);
		}
		read(mob,thisThang, theRest, false);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}

	@Override
	public Object executeInternal(MOB mob, int metaFlags, Object... args) throws java.io.IOException
	{
		if(!super.checkArguments(internalParameters, args))
			return Boolean.FALSE;
		return Boolean.valueOf(read(mob,(Environmental)args[0],(String)args[1],((Boolean)args[2]).booleanValue()));
	}
}
