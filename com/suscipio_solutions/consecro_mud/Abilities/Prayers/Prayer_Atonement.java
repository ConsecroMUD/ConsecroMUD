package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Atonement extends Prayer
{
	@Override public String ID() { return "Prayer_Atonement"; }
	private final static String localizedName = CMLib.lang().L("Atonement");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		CMMsg msg2=null;
		if((mob!=target)&&(!mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
			msg2=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,L("<T-NAME> do(es) not seem to like <S-NAME> messing with <T-HIS-HER> head."));

		if(success&&(CMLib.factions().getFaction(CMLib.factions().AlignID())!=null))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"<T-NAME> feel(s) more good.":"^S<S-NAME> "+prayWord(mob)+" to atone <T-NAMESELF>!^?"));
			if((mob.location().okMessage(mob,msg))
			&&((msg2==null)||(mob.location().okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				if((msg.value()<=0)&&((msg2==null)||(msg2.value()<=0)))
				{
					target.tell(L("Good, pure thoughts fill your head."));
					final int evilness=CMLib.dice().roll(10,adjustedLevel(mob,asLevel),0);
					CMLib.factions().postFactionChange(target,this, CMLib.factions().AlignID(), evilness);
				}
				if(msg2!=null) mob.location().send(mob,msg2);
			}
		}
		else
		{
			if((msg2!=null)&&(mob.location().okMessage(mob,msg2)))
				mob.location().send(mob,msg2);
			return beneficialWordsFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.",prayWord(mob)));
		}


		// return whether it worked
		return success;
	}
}
