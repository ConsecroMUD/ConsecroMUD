package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class PracticePoint extends StdItem implements MiscMagic
{
	@Override public String ID(){ return "PracticePoint";}
	public PracticePoint()
	{
		super();
		setName("a practice point");
		setDisplayText("A shiny green coin has been left here.");
		myContainer=null;
		setDescription("A shiny green coin with magical script around the edges.");
		myUses=Integer.MAX_VALUE;
		myWornCode=0;
		material=0;
		basePhyStats.setWeight(0);
		basePhyStats.setSensesMask(basePhyStats().sensesMask()|PhyStats.SENSE_ITEMNORUIN|PhyStats.SENSE_ITEMNOWISH);
		recoverPhyStats();
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_REMOVE:
			{
				setContainer(null);
				destroy();
				if(!mob.isMine(this))
					mob.setPractices(mob.getPractices()+1);
				unWear();
				if(!CMath.bset(msg.targetMajor(),CMMsg.MASK_OPTIMIZE))
					mob.location().recoverRoomStats();
				return;
			}
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
