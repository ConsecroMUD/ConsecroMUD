package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_ItemNoRuin extends Property
{
	@Override public String ID() { return "Prop_ItemNoRuin"; }
	@Override public String name(){ return "Prevents deletion/corruption from corpses";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{ return "A Prize";    }

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.SENSE_ITEMNORUIN);
	}
}
