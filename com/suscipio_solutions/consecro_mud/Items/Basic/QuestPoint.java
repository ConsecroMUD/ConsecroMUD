package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.AccountStats.PrideStat;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class QuestPoint extends StdItem
{
	@Override public String ID(){	return "QuestPoint";}
	public QuestPoint()
	{
		super();
		setName("a quest point");
		setDisplayText("A shiny blue coin has been left here.");
		myContainer=null;
		setDescription("A shiny blue coin with magical script around the edges.");
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
				unWear();
				setContainer(null);
				if(!mob.isMine(this))
				{
					mob.setQuestPoint(mob.getQuestPoint()+1);
					CMLib.players().bumpPrideStat(mob,PrideStat.QUESTPOINTS_EARNED, 1);
				}
				if(!CMath.bset(msg.targetMajor(),CMMsg.MASK_OPTIMIZE))
					mob.location().recoverRoomStats();
				destroy();
				return;
			}
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
