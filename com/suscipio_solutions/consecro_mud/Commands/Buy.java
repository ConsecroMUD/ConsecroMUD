package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;


@SuppressWarnings({"unchecked","rawtypes"})
public class Buy extends StdCommand
{
	public Buy(){}

	private final String[] access=I(new String[]{"BUY"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		MOB mobFor=null;
		if((commands.size()>2)
		&&(((String)commands.elementAt(commands.size()-2)).equalsIgnoreCase("for")))
		{
			final MOB M=mob.location().fetchInhabitant((String)commands.lastElement());
			if(M==null)
			{
				mob.tell(L("There is noone called '@x1' here.",((String)commands.lastElement())));
				return false;
			}
			commands.removeElementAt(commands.size()-1);
			commands.removeElementAt(commands.size()-1);
			mobFor=M;
		}

		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,"Buy what from whom?");
		if(shopkeeper==null) return false;
		if(commands.size()==0)
		{
			mob.tell(L("Buy what?"));
			return false;
		}
		if(CMLib.coffeeShops().getShopKeeper(shopkeeper)==null)
		{
			mob.tell(L("@x1 is not a shopkeeper!",shopkeeper.name()));
			return false;
		}

		int maxToDo=Integer.MAX_VALUE;
		if((commands.size()>1)
		&&(CMath.s_int((String)commands.firstElement())>0))
		{
			maxToDo=CMath.s_int((String)commands.firstElement());
			commands.setElementAt("all",0);
		}

		String whatName=CMParms.combine(commands,0);
		final Vector V=new Vector();
		boolean allFlag=((String)commands.elementAt(0)).equalsIgnoreCase("all");
		if(whatName.toUpperCase().startsWith("ALL.")){ allFlag=true; whatName="ALL "+whatName.substring(4);}
		if(whatName.toUpperCase().endsWith(".ALL")){ allFlag=true; whatName="ALL "+whatName.substring(0,whatName.length()-4);}
		int addendum=1;
		boolean doBugFix = true;
		while(doBugFix || ((allFlag)&&(addendum<=maxToDo)))
		{
			doBugFix=false;
			final ShopKeeper SK=CMLib.coffeeShops().getShopKeeper(shopkeeper);
			final Environmental itemToDo=SK.getShop().getStock(whatName,mob);
			if(itemToDo==null) break;
			if(CMLib.flags().canBeSeenBy(itemToDo,mob))
				V.addElement(itemToDo);
			if(addendum>=CMLib.coffeeShops().getShopKeeper(shopkeeper).getShop().numberInStock(itemToDo))
				break;
			++addendum;
		}
		String forName="";
		if((mobFor!=null)&&(mobFor!=mob))
		{
			if(mobFor.name().indexOf(' ')>=0)
				forName=" for '"+mobFor.Name()+"'";
			else
				forName=" for "+mobFor.Name();
		}

		if(V.size()==0)
			mob.tell(mob,shopkeeper,null,L("<T-NAME> do(es)n't appear to have any '@x1' for sale.  Try LIST.",whatName));
		else
		for(int v=0;v<V.size();v++)
		{
			final Environmental thisThang=(Environmental)V.elementAt(v);
			final CMMsg newMsg=CMClass.getMsg(mob,shopkeeper,thisThang,CMMsg.MSG_BUY,L("<S-NAME> buy(s) <O-NAME> from <T-NAMESELF>@x1.",forName));
			if(mob.location().okMessage(mob,newMsg))
				mob.location().send(mob,newMsg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}
}
