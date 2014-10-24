package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


public class Wealth extends Inventory
{
	public Wealth(){}

	private final String[] access=I(new String[]{"WEALTH"});
	@Override public String[] getAccessWords(){return access;}


	public StringBuilder getInventory(MOB seer, MOB mob, String mask)
	{
		final StringBuilder msg=new StringBuilder("");
		final InventoryList list = fetchInventory(seer,mob);
		if(list.moneyItems.size()==0)
			msg.append(L("\n\r^HMoney:^N None!\n\r"));
		else
			msg.append(getShowableMoney(list));
		return msg;
	}


	@Override @SuppressWarnings({"unchecked","rawtypes"})
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()==1)&&(commands.firstElement() instanceof MOB))
		{
			commands.addElement(getInventory((MOB)commands.firstElement(),mob,null));
			return true;
		}
		final StringBuilder msg=getInventory(mob,mob,CMParms.combine(commands,1));
		if(msg.length()==0)
			mob.tell(L("You have no money on you."));
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}
	public int ticksToExecute(){return 0;}
	@Override public boolean canBeOrdered(){return true;}
}
