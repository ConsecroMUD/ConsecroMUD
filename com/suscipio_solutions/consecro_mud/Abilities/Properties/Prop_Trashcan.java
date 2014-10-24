package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Prop_Trashcan extends Property
{
	@Override public String ID() { return "Prop_Trashcan"; }
	@Override public String name(){ return "Auto purges items put into a container";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	protected SLinkedList<Item> trashables=new SLinkedList<Item>();
	protected int tickDelay=0;
	protected volatile long lastAddition=0;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(tickID==Tickable.TICKID_PROPERTY_SPECIAL)
		{
			synchronized(trashables)
			{
				if((System.currentTimeMillis()-lastAddition)<((tickDelay-1)*CMProps.getTickMillis()))
					return true;
				for(final Item I : trashables)
					I.destroy();
				lastAddition=0;
				trashables.clear();
				CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
			}
			return false;
		}
		return true;
	}

	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		tickDelay=CMParms.getParmInt(newMiscText, "DELAY", 0);
	}

	protected void process(Item I)
	{
		if(tickDelay<=0)
			I.destroy();
		else
		synchronized(trashables)
		{
			if(lastAddition==0)
			{
				CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
				CMLib.threads().startTickDown(this, Tickable.TICKID_PROPERTY_SPECIAL, tickDelay);
			}
			lastAddition=System.currentTimeMillis()-10;
			trashables.add(I);
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof Item)
		&&(msg.targetMinor()==CMMsg.TYP_PUT)
		&&(msg.amITarget(affected))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Item))
			process((Item)msg.tool());
		else
		if((affected instanceof Room)
		&&(msg.targetMinor()==CMMsg.TYP_DROP)
		&&(msg.target()!=null)
		&&(msg.target() instanceof Item))
			process((Item)msg.target());
	}
}
