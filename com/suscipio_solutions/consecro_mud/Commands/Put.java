package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Light;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Put extends StdCommand
{
	public Put(){}

	private final String[] access=I(new String[]{"PUT","PU","P"});
	@Override public String[] getAccessWords(){return access;}

	public void putout(MOB mob, Vector commands, boolean quiet)
	{
		if(commands.size()<3)
		{
			mob.tell(L("Put out what?"));
			return;
		}
		commands.removeElementAt(1);
		commands.removeElementAt(0);

		final List<Item> items=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_UNWORNONLY,true);
		if(items.size()==0)
			mob.tell(L("You don't seem to be carrying that."));
		else
		for(int i=0;i<items.size();i++)
		{
			final Item I=items.get(i);
			if((items.size()==1)||(I instanceof Light))
			{
				final CMMsg msg=CMClass.getMsg(mob,I,null,CMMsg.MSG_EXTINGUISH,quiet?null:L("<S-NAME> put(s) out <T-NAME>."));
				if(mob.location().okMessage(mob,msg))
					mob.location().send(mob,msg);
			}
		}
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Put what where?"));
			return false;
		}

		if(((String)commands.lastElement()).equalsIgnoreCase("on"))
		{
			commands.removeElementAt(commands.size()-1);
			final Command C=CMClass.getCommand("Wear");
			if(C!=null) C.execute(mob,commands,metaFlags);
			return false;
		}

		if(commands.size()>=4)
		{
			final String s=CMParms.combine(commands, 0).toLowerCase();
			final Wearable.CODES codes = Wearable.CODES.instance();
			for(int i=1;i<codes.total();i++)
				if(s.endsWith(" on "+codes.name(i).toLowerCase())||s.endsWith(" on my "+codes.name(i).toLowerCase()))
				{
					final Command C=CMClass.getCommand("Wear");
					if(C!=null) C.execute(mob,commands,metaFlags);
					return false;
				}
		}

		if(((String)commands.elementAt(1)).equalsIgnoreCase("on"))
		{
			commands.removeElementAt(1);
			final Command C=CMClass.getCommand("Wear");
			if(C!=null) C.execute(mob,commands,metaFlags);
			return false;
		}

		if(((String)commands.elementAt(1)).equalsIgnoreCase("out"))
		{
			putout(mob,commands,false);
			return false;
		}

		commands.removeElementAt(0);
		if(commands.size()<2)
		{
			mob.tell(L("Where should I put the @x1",(String)commands.elementAt(0)));
			return false;
		}

		final Environmental container=CMLib.english().possibleContainer(mob,commands,false,Wearable.FILTER_ANY);
		if((container==null)||(!CMLib.flags().canBeSeenBy(container,mob)))
		{
			mob.tell(L("I don't see a @x1 here.",(String)commands.lastElement()));
			return false;
		}

		final int maxToPut=CMLib.english().calculateMaxToGive(mob,commands,true,mob,false);
		if(maxToPut<0) return false;

		String thingToPut=CMParms.combine(commands,0);
		int addendum=1;
		String addendumStr="";
		final Vector V=new Vector();
		boolean allFlag=(commands.size()>0)?((String)commands.elementAt(0)).equalsIgnoreCase("all"):false;
		if(thingToPut.toUpperCase().startsWith("ALL.")){ allFlag=true; thingToPut="ALL "+thingToPut.substring(4);}
		if(thingToPut.toUpperCase().endsWith(".ALL")){ allFlag=true; thingToPut="ALL "+thingToPut.substring(0,thingToPut.length()-4);}
		final boolean onlyGoldFlag=mob.hasOnlyGoldInInventory();
		Item putThis=CMLib.english().bestPossibleGold(mob,null,thingToPut);
		if(putThis!=null)
		{
			if(((Coins)putThis).getNumberOfCoins()<CMLib.english().numPossibleGold(mob,thingToPut))
				return false;
			if(CMLib.flags().canBeSeenBy(putThis,mob))
				V.addElement(putThis);
		}
		boolean doBugFix = true;
		if(V.size()==0)
		while(doBugFix || ((allFlag)&&(addendum<=maxToPut)))
		{
			doBugFix=false;
			putThis=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,thingToPut+addendumStr);
			if((allFlag)&&(!onlyGoldFlag)&&(putThis instanceof Coins)&&(thingToPut.equalsIgnoreCase("ALL")))
				putThis=null;
			else
			{
				if(putThis==null) break;
				if((CMLib.flags().canBeSeenBy(putThis,mob))
				&&(!V.contains(putThis)))
					V.addElement(putThis);
			}
			addendumStr="."+(++addendum);
		}

		if(V.contains(container))
			V.remove(container);

		if(V.size()==0)
			mob.tell(L("You don't seem to be carrying that."));
		else
		for(int i=0;i<V.size();i++)
		{
			putThis=(Item)V.elementAt(i);
			final String putWord=(container instanceof Rideable)?((Rideable)container).putString(mob):"in";
			final CMMsg putMsg=CMClass.getMsg(mob,container,putThis,CMMsg.MASK_OPTIMIZE|CMMsg.MSG_PUT,L("<S-NAME> put(s) <O-NAME> @x1 <T-NAME>.",putWord));
			if(mob.location().okMessage(mob,putMsg))
				mob.location().send(mob,putMsg);
			if(putThis instanceof Coins)
				((Coins)putThis).putCoinsBack();
			if(putThis instanceof RawMaterial)
				((RawMaterial)putThis).rebundle();
		}
		mob.location().recoverRoomStats();
		mob.location().recoverRoomStats();
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
