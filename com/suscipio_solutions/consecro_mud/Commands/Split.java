package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.SHashSet;


@SuppressWarnings("rawtypes")
public class Split extends StdCommand
{
	public Split(){}

	private final String[] access=I(new String[]{"SPLIT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Split what, how much?"));
			return false;
		}
		final String itemID=CMParms.combine(commands,1);
		final long numGold=CMLib.english().numPossibleGold(mob,itemID);
		if(numGold>0)
		{
			final String currency=CMLib.english().numPossibleGoldCurrency(mob,itemID);
			final double denom=CMLib.english().numPossibleGoldDenomination(mob,currency,itemID);

			int num=0;
			final Set<MOB> H=mob.getGroupMembers(new SHashSet<MOB>());

			for (final MOB recipientM : H)
			{
				if((!recipientM.isMonster())
				&&(recipientM!=mob)
				&&(recipientM.location()==mob.location())
				&&(mob.location().isInhabitant(recipientM)))
					num++;
				else
				{
					H.remove(recipientM);
				}
			}
			if(num==0)
			{
				mob.tell(L("No one appears to be eligible to receive any of your money."));
				return false;
			}

			double totalAbsoluteValue=CMath.mul(numGold,denom);
			totalAbsoluteValue=CMath.div(totalAbsoluteValue,num+1);
			if((totalAbsoluteValue*num)>CMLib.beanCounter().getTotalAbsoluteValue(mob,currency))
			{
				mob.tell(L("You don't have that much @x1.",CMLib.beanCounter().getDenominationName(currency,denom)));
				return false;
			}
			final List<Coins> V=CMLib.beanCounter().makeAllCurrency(currency,totalAbsoluteValue);
			CMLib.beanCounter().subtractMoney(mob,totalAbsoluteValue*num);
			for (final Object element : H)
			{
				final MOB recipient=(MOB)element;
				for(int v=0;v<V.size();v++)
				{
					Coins C=V.get(v);
					C=(Coins)C.copyOf();
					mob.addItem(C);
					final CMMsg newMsg=CMClass.getMsg(mob,recipient,C,CMMsg.MSG_GIVE,L("<S-NAME> give(s) <O-NAME> to <T-NAMESELF>."));
					if(mob.location().okMessage(mob,newMsg))
						mob.location().send(mob,newMsg);
					C.putCoinsBack();
				}
			}
		}
		else
		if((commands.size()>2)&&(CMath.isInteger((String)commands.lastElement())))
		{
			final int howMuch=CMath.s_int((String)commands.lastElement());
			if(howMuch<=0)
			{
				mob.tell(L("Split what, how much?"));
				return false;
			}
			commands.remove(commands.size()-1);
			final Vector<String> v=CMParms.parse("GET "+howMuch+" FROM \""+CMParms.combine(commands,1)+"\"");
			final Command c=CMClass.getCommand("Get");
			return c.execute(mob, v, metaFlags);
		}
		else
		{
			mob.tell(L("Split what, how much?"));
			return false;
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
