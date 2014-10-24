package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_RoomLit extends Property
{
	@Override public String ID() { return "Prop_RoomLit"; }
	@Override public String name(){ return "Lighting Property";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}
	@Override public boolean bubbleAffect(){return true;}

	@Override
	public String accountForYourself()
	{ return "Always Lit";	}


	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(CMLib.flags().isInDark(affected))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_DARK);
	}
}
