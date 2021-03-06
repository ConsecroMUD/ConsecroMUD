package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Display extends StdCommand
{
	public Display(){}

	private final String[] access=I(new String[]{"DISPLAY","SHOW"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Show what to whom?"));
			return false;
		}
		commands.removeElementAt(0);
		if(commands.size()<2)
		{
			mob.tell(L("To whom should I show that?"));
			return false;
		}

		final MOB recipient=mob.location().fetchInhabitant((String)commands.lastElement());
		if((recipient==null)||(!CMLib.flags().canBeSeenBy(recipient,mob)))
		{
			mob.tell(L("I don't see anyone called @x1 here.",(String)commands.lastElement()));
			return false;
		}
		commands.removeElementAt(commands.size()-1);
		if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("to")))
			commands.removeElementAt(commands.size()-1);

		final int maxToGive=CMLib.english().calculateMaxToGive(mob,commands,true,mob,false);
		if(maxToGive<0) return false;

		String thingToGive=CMParms.combine(commands,0);
		int addendum=1;
		String addendumStr="";
		final Vector V=new Vector();
		boolean allFlag=(commands.size()>0)?((String)commands.elementAt(0)).equalsIgnoreCase("all"):false;
		if(thingToGive.toUpperCase().startsWith("ALL.")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(4);}
		if(thingToGive.toUpperCase().endsWith(".ALL")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(0,thingToGive.length()-4);}
		boolean doBugFix = true;
		while(doBugFix || ((allFlag)&&(addendum<=maxToGive)))
		{
			doBugFix=false;
			Environmental giveThis=CMLib.english().bestPossibleGold(mob,null,thingToGive);
			if(giveThis!=null)
			{
				if(((Coins)giveThis).getNumberOfCoins()<CMLib.english().numPossibleGold(mob,thingToGive))
					return false;
			}
			else
				giveThis=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,thingToGive+addendumStr);
			if((giveThis==null)
			&&(V.size()==0)
			&&(addendumStr.length()==0)
			&&(!allFlag))
				giveThis=mob.fetchItem(null,Wearable.FILTER_WORNONLY,thingToGive);
			if(giveThis==null) break;
			if(CMLib.flags().canBeSeenBy(giveThis,mob))
				V.addElement(giveThis);
			addendumStr="."+(++addendum);
		}

		if(V.size()==0)
			mob.tell(L("You don't seem to be carrying that."));
		else
		for(int i=0;i<V.size();i++)
		{
			final Environmental giveThis=(Environmental)V.elementAt(i);
			final CMMsg newMsg=CMClass.getMsg(recipient,giveThis,mob,CMMsg.MSG_LOOK,L("<O-NAME> show(s) <T-NAME> to <S-NAMESELF>."));
			if(mob.location().okMessage(recipient,newMsg))
			{
				recipient.tell(recipient,giveThis,mob,L("<O-NAME> show(s) <T-NAME> to <S-NAMESELF>."));
				mob.location().send(recipient,newMsg);
			}

		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
