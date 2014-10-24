package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Prop_HereAdjuster extends Prop_HaveAdjuster
{
	@Override public String ID() { return "Prop_HereAdjuster"; }
	@Override public String name(){ return "Adjustments to stats when here";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}

	@Override
	public String accountForYourself()
	{
		return super.fixAccoutingsWithMask("Affects on those here: "+parameters[0],parameters[1]);
	}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_ENTER; }

	@Override
	public boolean canApply(MOB mob)
	{
		if(affected==null) return true;
		if((mob.location()!=affected)
		||((mask!=null)&&(!CMLib.masking().maskCheck(mask,mob,false))))
			return false;
		return true;
	}
}
