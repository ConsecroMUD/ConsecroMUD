package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NoTeleport extends Property
{
	@Override public String ID() { return "Prop_NoTeleport"; }
	@Override public String name(){ return "Teleport-INTO Spell Neutralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	@Override public long flags(){return Ability.FLAG_IMMUNER;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(msg.source()!=null)
		&&(msg.source().location()!=null)
		&&(msg.sourceMinor()!=CMMsg.TYP_LEAVE))
		{
			final boolean summon=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_SUMMONING);
			final boolean teleport=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING);
			final boolean shere=(msg.source().location()==affected)
						||((affected instanceof Area)&&(((Area)affected).inMyMetroArea(msg.source().location().getArea())));
			if(((!shere)&&(!summon)&&(teleport))
			   ||((shere)&&(summon)))
			{
				final Ability A=(Ability)msg.tool();
				if(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_CHANT)
				||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
				||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)
				||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG))
					msg.source().location().showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
				return false;
			}
		}
		return true;
	}
}
