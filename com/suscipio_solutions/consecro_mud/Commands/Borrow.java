package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;


@SuppressWarnings({"unchecked","rawtypes"})
public class Borrow extends StdCommand
{
	public Borrow(){}

	private final String[] access=I(new String[]{"BORROW"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,"Borrow how much from whom?");
		if(shopkeeper==null) return false;
		final ShopKeeper SHOP=CMLib.coffeeShops().getShopKeeper(shopkeeper);
		if(!(SHOP instanceof Banker))
		{
			mob.tell(L("You can not borrow from @x1.",shopkeeper.name()));
			return false;
		}
		if(commands.size()==0)
		{
			mob.tell(L("Borrow how much?"));
			return false;
		}
		String str=CMParms.combine(commands,0);
		if(str.equalsIgnoreCase("all")) str=""+Integer.MAX_VALUE;
		final long numCoins=CMLib.english().numPossibleGold(null,str);
		final String currency=CMLib.english().numPossibleGoldCurrency(shopkeeper,str);
		final double denomination=CMLib.english().numPossibleGoldDenomination(shopkeeper,currency,str);
		Item thisThang=null;
		if((numCoins==0)||(denomination==0.0))
		{
			mob.tell(L("Borrow how much?"));
			return false;
		}
		thisThang=CMLib.beanCounter().makeCurrency(currency,denomination,numCoins);

		if((thisThang==null)||(!CMLib.flags().canBeSeenBy(thisThang,mob)))
		{
			mob.tell(L("That doesn't appear to be available.  Try LIST."));
			return false;
		}
		final String str2="<S-NAME> borrow(s) <O-NAME> from "+shopkeeper.name()+".";
		final CMMsg newMsg=CMClass.getMsg(mob,shopkeeper,thisThang,CMMsg.MSG_BORROW,str2);
		if(!mob.location().okMessage(mob,newMsg))
			return false;
		mob.location().send(mob,newMsg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}
}

