package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_SenseProfessions extends Prayer
{
	@Override public String ID() { return "Prayer_SenseProfessions"; }
	private final static String localizedName = CMLib.lang().L("Sense Professions");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public int enchantQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	protected int senseWhat() { return ACODE_COMMON_SKILL; }
	protected String senseWhatStr() { return "professions"; }

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(target==mob)
		{
			mob.tell(L("You already know your own @x1!.",senseWhatStr()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 and peer(s) at <T-NAMESELF>.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Vector professionsV = new Vector();
				for(final Enumeration<Ability> a=target.allAbilities();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if((A!=null)
					&& ((A.classificationCode() & Ability.ALL_ACODES)==senseWhat()))
						professionsV.addElement(A.name() + " ("+A.proficiency()+"%)");
				}
				if(professionsV.size()==0)
					mob.tell(mob,target,null,L("<T-NAME> seem(s) like <T-HE-SHE> has no @x1.",senseWhatStr()));
				else
					mob.tell(mob,target,null,L("<T-NAME> seem(s) like <T-HE-SHE> understands the following @x1: @x2",senseWhatStr(),CMParms.toStringList(professionsV)));
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> @x1 and peer(s) at <T-NAMESELF>, but then blink(s).",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
