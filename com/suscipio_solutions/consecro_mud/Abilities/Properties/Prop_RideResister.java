package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Prop_RideResister extends Prop_HaveResister
{
	@Override public String ID() { return "Prop_RideResister"; }
	@Override public String name(){ return "Resistance due to riding";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS;}

	@Override
	public String accountForYourself()
	{ return "Those mounted gain resistances: "+describeResistance(text());}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_MOUNT; }

	@Override
	public boolean canResist(Environmental E)
	{
		if((affected instanceof Rideable)
		&&(E instanceof MOB)
		&&(((Rideable)affected).amRiding((MOB)E))
		&&(((MOB)E).location()!=null))
			return true;
		return false;
	}
}
