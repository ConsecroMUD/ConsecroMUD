package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_KnowAlignment extends Spell
{
	@Override public String ID() { return "Spell_KnowAlignment"; }
	private final static String localizedName = CMLib.lang().L("Know Alignment");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(target==mob) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^SYou draw out <T-NAME>s disposition.^?"),verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> draw(s) out your disposition.^?"),verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> draws out <T-NAME>s disposition.^?"));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(success)
				mob.tell(mob,target,null,L("<T-NAME> seem(s) like <T-HE-SHE> is @x1.",CMLib.flags().getAlignmentName(target).toLowerCase()));
			else
			{
				mob.tell(mob,target,null,L("<T-NAME> seem(s) like <T-HE-SHE> is @x1.",Faction.Align.values()[CMLib.dice().roll(1,Faction.Align.values().length-1,0)].toString().toLowerCase()));
			}
		}


		// return whether it worked
		return success;
	}
}
