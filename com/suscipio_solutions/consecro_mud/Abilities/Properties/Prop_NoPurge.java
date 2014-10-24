package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_NoPurge extends Property
{
	@Override public String ID() { return "Prop_NoPurge"; }
	@Override public String name(){ return "Prevents automatic purging";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_ITEMS;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected!=null)
		{
			if(affected instanceof Room)
			{
				final Room R=(Room)affected;
				for(int i=0;i<R.numItems();i++)
				{
					final Item I=R.getItem(i);
					if(I!=null) I.setExpirationDate(0);
				}
			}
			else
			if(affected instanceof Container)
			{
				if(((Container)affected).owner() instanceof Room)
				{
					((Container)affected).setExpirationDate(0);
					final List<Item> V=((Container)affected).getContents();
					for(int v=0;v<V.size();v++)
						V.get(v).setExpirationDate(0);
				}
			}
			else
			if(affected instanceof Item)
				((Item)affected).setExpirationDate(0);
		}
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(affected!=null)
		{
			if(affected instanceof Room)
			{
				if((msg.targetMinor()==CMMsg.TYP_DROP)
				&&(msg.target()!=null)
				&&(msg.target() instanceof Item))
					((Item)msg.target()).setExpirationDate(0);
			}
			else
			if(affected instanceof Container)
			{
				if(((msg.targetMinor()==CMMsg.TYP_PUT)
					||(msg.targetMinor()==CMMsg.TYP_INSTALL))
				&&(msg.target()!=null)
				&&(msg.target()==affected)
				&&(msg.target() instanceof Item)
				&&(msg.tool()!=null)
				&&(msg.tool() instanceof Item))
				{
					((Item)msg.target()).setExpirationDate(0);
					((Item)msg.tool()).setExpirationDate(0);
				}
			}
			else
			if(affected instanceof Item)
			{
				if((msg.targetMinor()==CMMsg.TYP_DROP)
				&&(msg.target()!=null)
				&&(msg.target() instanceof Item)
				&&(msg.target()==affected))
					((Item)msg.target()).setExpirationDate(0);
			}
		}
	}
}
