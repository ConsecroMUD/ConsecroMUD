package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Prop_Hidden extends Property
{
	@Override public String ID() { return "Prop_Hidden"; }
	@Override public String name(){ return "Persistant Hiddenness";}
	@Override
	protected int canAffectCode(){return Ability.CAN_MOBS
										 |Ability.CAN_ITEMS
										 |Ability.CAN_EXITS
										 |Ability.CAN_AREAS;}
	protected int ticksSinceLoss=100;
	protected boolean unLocatable=false;

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;

		if(msg.amISource(mob))
		{

			if(((!msg.sourceMajor(CMMsg.MASK_SOUND)
				 ||(msg.sourceMinor()==CMMsg.TYP_SPEAK)
				 ||(msg.sourceMinor()==CMMsg.TYP_ENTER)
				 ||(msg.sourceMinor()==CMMsg.TYP_LEAVE)
				 ||(msg.sourceMinor()==CMMsg.TYP_RECALL)))
			 &&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
			 &&(msg.sourceMinor()!=CMMsg.TYP_LOOK)
			 &&(msg.sourceMinor()!=CMMsg.TYP_EXAMINE)
			 &&(msg.sourceMajor()>0))
			{
				ticksSinceLoss=0;
				mob.recoverPhyStats();
			}
		}
		return;
	}

	@Override
	public void setMiscText(String text)
	{
		super.setMiscText(text);
		if(!(affected instanceof MOB))
		{
			final Vector parms=CMParms.parse(text.toUpperCase());
			unLocatable=parms.contains("UNLOCATABLE");
		}
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_DETECTION,100+affectableStats.getStat(CharStats.STAT_SAVE_DETECTION));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(ticksSinceLoss<999999)
			ticksSinceLoss++;
		return super.tick(ticking,tickID);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_HIDDEN);
			if(ticksSinceLoss>30)
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
		}
		else
		{
			if(unLocatable)
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.SENSE_UNLOCATABLE);
			if(affected instanceof Item)
			{
				if((((Item)affected).owner() instanceof Room)
				&&((!(affected instanceof Container))
					||(!((Container)affected).hasADoor())
					||(!((Container)affected).isOpen())))
						affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
			}
			else
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
		}
	}
}
