package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;


public class Prop_NoCraftability extends Property
{
	@Override public String ID() { return "Prop_NoCraftability"; }
	@Override public String name(){ return "Not Learnable for Crafting";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{ return "Uncraftable";	}

	@Override public long flags(){return Ability.FLAG_UNCRAFTABLE;}
}
