package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_SpeedTime extends Chant
{
	@Override public String ID() { return "Chant_SpeedTime"; }
	private final static String localizedName = CMLib.lang().L("Speed Time");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Speed Time)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_MOONSUMMONING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return 100;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?L("Something is happening!"):L("^S<S-NAME> begin(s) to chant...^?"));
			if(mob.location().okMessage(mob,msg))
			{
				final int mana=mob.curState().getMana();
				mob.location().send(mob,msg);
				for(int i=0;i<(adjustedLevel(mob,asLevel)/2);i++)
					CMLib.threads().tickAllTickers(mob.location());
				if(mob.curState().getMana()>mana)
					mob.curState().setMana(mana);
				mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?L("It stops."):L("^S<S-NAME> stop(s) chanting.^?"));
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s), but nothing happens."));

		return success;
	}
}
