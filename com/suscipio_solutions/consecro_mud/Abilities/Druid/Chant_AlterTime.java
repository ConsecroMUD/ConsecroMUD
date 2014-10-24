package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_AlterTime extends Chant
{
	@Override public String ID() { return "Chant_AlterTime"; }
	private final static String localizedName = CMLib.lang().L("Alter Time");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int overrideMana(){return 100;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_MOONSUMMONING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s), and reality seems to start blurring.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				int x=CMath.s_int(text());
				while(x==0)	x=CMLib.dice().roll(1,3,-2);
				if(x>0)
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Time moves forwards!"));
				else
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Time moves backwards!"));
				mob.location().getArea().getTimeObj().tickTock(x);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s), but the magic fades"));


		// return whether it worked
		return success;
	}
}
