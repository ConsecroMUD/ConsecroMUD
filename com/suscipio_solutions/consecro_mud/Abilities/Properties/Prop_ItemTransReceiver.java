package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;


public class Prop_ItemTransReceiver extends Property
{
	@Override public String ID() { return "Prop_ItemTransReceiver"; }
	@Override public String name(){ return "Item Transporter Receiver";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	@Override
	public String accountForYourself()
	{ return "Item Transporter Receiver";	}
}
