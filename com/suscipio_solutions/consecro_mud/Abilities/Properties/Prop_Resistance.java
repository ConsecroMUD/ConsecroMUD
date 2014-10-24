package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_Resistance extends Prop_HaveResister
{
	@Override public String ID() { return "Prop_Resistance"; }
	@Override public String name(){ return "Resistance to Stuff";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override public boolean bubbleAffect(){return false;}

	@Override public long flags(){return Ability.FLAG_RESISTER;}

	@Override
	public String accountForYourself()
	{ return "Have resistances: "+describeResistance(text());}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_ALWAYS; }

	@Override
	public boolean canResist(Environmental E)
	{
		return ((E instanceof MOB)
				&&(E==affected));
	}
}
