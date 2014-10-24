package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NoDamage extends Property
{
	@Override public String ID() { return "Prop_NoDamage"; }
	@Override public String name(){ return "No Damage";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{ return "Harmless";	}

	@Override public long flags(){return Ability.FLAG_IMMUNER;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(affected !=null)
		&&((msg.source()==affected)||(msg.tool()==affected)))
			msg.setValue(0);
		return true;
	}
}
