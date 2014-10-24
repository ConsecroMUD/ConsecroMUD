package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;


@SuppressWarnings({"unchecked","rawtypes"})
public class Bid extends StdCommand
{
	public Bid(){}

	private final String[] access=I(new String[]{"BID"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,"Bid how much, on what, with whom?");
		if(shopkeeper==null) return false;
		if(commands.size()<2)
		{
			mob.tell(L("Bid how much on what?"));
			return false;
		}
		if(!(CMLib.coffeeShops().getShopKeeper(shopkeeper) instanceof Auctioneer))
		{
			mob.tell(L("@x1 is not an auctioneer!",shopkeeper.name()));
			return false;
		}

		String bidStr=(String)commands.firstElement();
		if(CMLib.english().numPossibleGold(mob,bidStr)<=0)
		{
			mob.tell(L("It does not look like '@x1' is enough to offer.",bidStr));
			return false;
		}
		final Object[] bidThang=CMLib.english().parseMoneyStringSDL(mob,bidStr,null);
		bidStr=CMLib.beanCounter().nameCurrencyShort((String)bidThang[0],CMath.mul(((Double)bidThang[1]).doubleValue(),((Long)bidThang[2]).longValue()));
		commands.removeElementAt(0);

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
		if(V.size()==0)
			mob.tell(mob,shopkeeper,null,L("<T-NAME> do(es)n't appear to have any '@x1' available for auction.  Try LIST.",whatName));
		else
		for(int v=0;v<V.size();v++)
		{
			final Environmental thisThang=(Environmental)V.elementAt(v);
			final CMMsg newMsg=CMClass.getMsg(mob,shopkeeper,thisThang,
					CMMsg.MSG_BID,L("<S-NAME> bid(s) @x1 on <O-NAME> with <T-NAMESELF>.",bidStr),
					CMMsg.MSG_BID,L("<S-NAME> bid(s) '@x1' on <O-NAME> with <T-NAMESELF>.",bidStr),
					CMMsg.MSG_BID,L("<S-NAME> place(s) a bid with <T-NAMESELF>."));
			if(mob.location().okMessage(mob,newMsg))
				mob.location().send(mob,newMsg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}
}
