package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Fill extends StdCommand
{
	public Fill(){}

	private final String[] access=I(new String[]{"FILL"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Fill what, from what?"));
			return false;
		}
		commands.removeElementAt(0);
		final String testFill=CMParms.combine(commands,0);
		final Environmental fillThisItem=mob.location().fetchFromRoomFavorItems(null,testFill);
		if((fillThisItem instanceof Container)
		&&(!CMLib.flags().isGettable((Container)fillThisItem))
		&&(((Container)fillThisItem).material()==RawMaterial.RESOURCE_DUST))
		{
			final String fillMsg="<S-NAME> fill(s) in <T-NAMESELF>.";
			final CMMsg msg=CMClass.getMsg(mob,fillThisItem,null,CMMsg.MSG_CLOSE,fillMsg,testFill,fillMsg);
			if(mob.location().okMessage(msg.source(),msg))
				mob.location().send(msg.source(),msg);
			return false;
		}

		if((commands.size()<2)&&(!(mob.location() instanceof Drink)))
		{
			mob.tell(L("From what should I fill the @x1?",(String)commands.elementAt(0)));
			return false;
		}
		Environmental fillFromThis=null;
		if((commands.size()==1)&&(mob.location() instanceof Drink))
			fillFromThis=mob.location();
		else
		{
			int fromDex=commands.size()-1;
			for(int i=commands.size()-2;i>=1;i--)
				if(((String)commands.elementAt(i)).equalsIgnoreCase("from"))
				{
					fromDex=i;
					commands.removeElementAt(i);
				}
			final String thingToFillFrom=CMParms.combine(commands,fromDex);
			fillFromThis=mob.location().fetchFromMOBRoomFavorsItems(mob,null,thingToFillFrom,Wearable.FILTER_ANY);
			if((fillFromThis==null)||(!CMLib.flags().canBeSeenBy(fillFromThis,mob)))
			{
				mob.tell(L("I don't see @x1 here.",thingToFillFrom));
				return false;
			}
			while(commands.size()>=(fromDex+1))
				commands.removeElementAt(commands.size()-1);
		}

		final int maxToFill=CMLib.english().calculateMaxToGive(mob,commands,true,mob,false);
		if(maxToFill<0) return false;

		String thingToFill=CMParms.combine(commands,0);
		int addendum=1;
		String addendumStr="";
		final Vector V=new Vector();
		boolean allFlag=(commands.size()>0)?((String)commands.elementAt(0)).equalsIgnoreCase("all"):false;
		if(thingToFill.toUpperCase().startsWith("ALL.")){ allFlag=true; thingToFill="ALL "+thingToFill.substring(4);}
		if(thingToFill.toUpperCase().endsWith(".ALL")){ allFlag=true; thingToFill="ALL "+thingToFill.substring(0,thingToFill.length()-4);}
		boolean doBugFix = true;
		while(doBugFix || ((allFlag)&&(maxToFill<addendum)))
		{
			doBugFix=false;
			final Item fillThis=mob.findItem(null,thingToFill+addendumStr);
			if(fillThis==null) break;
			if((CMLib.flags().canBeSeenBy(fillThis,mob))
			&&(!V.contains(fillThis)))
				V.addElement(fillThis);
			addendumStr="."+(++addendum);
		}

		if(V.size()==0)
			mob.tell(L("You don't seem to have '@x1'.",thingToFill));
		else
		for(int i=0;i<V.size();i++)
		{
			final Environmental fillThis=(Environmental)V.elementAt(i);
			final CMMsg fillMsg=CMClass.getMsg(mob,fillThis,fillFromThis,CMMsg.MSG_FILL,L("<S-NAME> fill(s) <T-NAME> from <O-NAME>."));
			if((!mob.isMine(fillThis))&&(fillThis instanceof Item))
			{
				if(CMLib.commands().postGet(mob,null,(Item)fillThis,false))
					if(mob.location().okMessage(mob,fillMsg))
						mob.location().send(mob,fillMsg);
			}
			else
			if(mob.location().okMessage(mob,fillMsg))
				mob.location().send(mob,fillMsg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
