package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_RoomDark extends Property
{
	@Override public String ID() { return "Prop_RoomDark"; }
	@Override public String name(){ return "Darkening Property";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	@Override
	public String accountForYourself()
	{ return "Darkened";	}


	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_DARK);
	}
}
