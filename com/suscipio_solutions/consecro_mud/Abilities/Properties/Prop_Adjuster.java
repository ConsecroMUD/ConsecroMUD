package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_Adjuster extends Prop_HaveAdjuster
{
	@Override public String ID() { return "Prop_Adjuster"; }
	@Override public String name(){ return "Adjustments to stats";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	@Override public boolean bubbleAffect(){return false;}
	@Override public long flags(){return Ability.FLAG_ADJUSTER;}
	@Override
	public boolean canApply(MOB mob)
	{
		if((affected!=null)
		&&((mask==null)||(CMLib.masking().maskCheck(mask,mob,true))))
			return true;
		return false;
	}

	@Override
	public boolean canApply(Environmental E)
	{
		if((affected!=null)
		&&((mask==null)||(CMLib.masking().maskCheck(mask,E,true))))
			return true;
		return false;
	}

	
	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ALWAYS;
	}

	@Override
	public String accountForYourself()
	{
		return fixAccoutingsWithMask("Effects: "+parameters[0],parameters[1]);
	}
}
