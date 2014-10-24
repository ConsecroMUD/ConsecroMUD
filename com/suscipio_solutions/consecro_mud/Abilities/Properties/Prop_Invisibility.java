package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Prop_Invisibility extends Property
{
	@Override public String ID() { return "Prop_Invisibility"; }
	@Override public String name(){ return "Persistant Invisibility";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	protected int ticksSinceLoss=100;

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if((msg.amISource(mob))&&(CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS)))
		{
			ticksSinceLoss=0;
			mob.recoverPhyStats();
		}
		return;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(ticksSinceLoss<9999)
			ticksSinceLoss++;
		return super.tick(ticking,tickID);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_INVISIBLE);
		if(ticksSinceLoss>60)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
	}
}
