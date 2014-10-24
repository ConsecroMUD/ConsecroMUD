package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_Uncampable extends Property
{
	@Override public String ID() { return "Prop_Uncampable"; }
	@Override public String name(){ return "Can't be camped on";}
	@Override
	protected int canAffectCode(){return Ability.CAN_MOBS
										 |Ability.CAN_ITEMS
										 |Ability.CAN_ROOMS;}
	
	@Override public long flags(){return Ability.FLAG_ADJUSTER;}
	
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_BE_CAMPED);
	}
}
