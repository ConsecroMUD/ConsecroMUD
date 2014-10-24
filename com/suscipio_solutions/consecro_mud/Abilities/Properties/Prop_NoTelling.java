package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NoTelling extends Property
{
	@Override public String ID() { return "Prop_NoTelling"; }
	@Override public String name(){ return "Tel Neutralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	@Override
	public String accountForYourself()
	{ return "No Telling Field";	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;


		if((msg.sourceMinor()==CMMsg.TYP_TELL)
		&&((!(affected instanceof MOB))||(msg.source()==affected)))
		{
			if(affected instanceof MOB)
				msg.source().tell(L("Your message drifts into oblivion."));
			else
				msg.source().tell(L("This is a no-tell area."));
			return false;
		}
		return true;
	}
}
