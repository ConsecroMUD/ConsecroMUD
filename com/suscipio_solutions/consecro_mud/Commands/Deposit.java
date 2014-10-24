package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.PostOffice;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;


@SuppressWarnings({"unchecked","rawtypes"})
public class Deposit extends StdCommand
{
	public Deposit(){}

	private final String[] access=I(new String[]{"DEPOSIT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,"Deposit how much with whom?");
		final ShopKeeper SHOP=CMLib.coffeeShops().getShopKeeper(shopkeeper);
		if(shopkeeper==null) return false;
		if((!(SHOP instanceof Banker))&&(!(SHOP instanceof PostOffice)))
		{
			mob.tell(L("You can not deposit anything with @x1.",shopkeeper.name()));
			return false;
		}
		if(commands.size()==0)
		{
			mob.tell(L("Deposit what or how much?"));
			return false;
		}
		final String thisName=CMParms.combine(commands,0);
		Item thisThang=CMLib.english().bestPossibleGold(mob,null,thisName);
		if(thisThang==null)
		{
			thisThang=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,thisName);
			if((thisThang==null)||(!CMLib.flags().canBeSeenBy(thisThang,mob)))
			{
				mob.tell(L("You don't seem to be carrying that."));
				return false;
			}
		}
		else
		if(((Coins)thisThang).getNumberOfCoins()<CMLib.english().numPossibleGold(mob,thisName))
			return false;
		CMMsg newMsg=null;
		if(SHOP instanceof Banker)
			newMsg=CMClass.getMsg(mob,shopkeeper,thisThang,CMMsg.MSG_DEPOSIT,L("<S-NAME> deposit(s) <O-NAME> into <S-HIS-HER> account with <T-NAMESELF>."));
		else
			newMsg=CMClass.getMsg(mob,shopkeeper,thisThang,CMMsg.MSG_DEPOSIT,L("<S-NAME> mail(s) <O-NAME>."));
		if(mob.location().okMessage(mob,newMsg))
			mob.location().send(mob,newMsg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
