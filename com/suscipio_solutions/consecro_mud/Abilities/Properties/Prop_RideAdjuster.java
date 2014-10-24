package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public class Prop_RideAdjuster extends Prop_HaveAdjuster
{
	@Override public String ID() { return "Prop_RideAdjuster"; }
	@Override public String name(){ return "Adjustments to stats when ridden";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS;}

	@Override
	public String accountForYourself()
	{
		return super.fixAccoutingsWithMask("Affects on the mounted: "+parameters[0],parameters[1]);
	}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_MOUNT; }

	@Override
	public boolean canApply(MOB mob)
	{
		if(!super.canApply(mob))
			return false;
		if(mob.riding()==affected)
			return true;
		return false;
	}
}
