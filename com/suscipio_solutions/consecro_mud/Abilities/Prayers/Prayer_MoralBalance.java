package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_MoralBalance extends Prayer
{
	@Override public String ID() { return "Prayer_MoralBalance"; }
	private final static String localizedName = CMLib.lang().L("Moral Balance");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;}

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

		if((success)&&(CMLib.factions().getFaction(CMLib.factions().AlignID())!=null))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"<T-NAME> feel(s) completely different about the world.":"^S<S-NAME> "+prayWord(mob)+" to bring balance to <T-NAMESELF>!^?"));
			if((mob.location().okMessage(mob,msg))
			&&((msg2==null)||(mob.location().okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				if((msg.value()<=0)&&((msg2==null)||(msg2.value()<=0)))
				{
					target.tell(L("Your views on the world suddenly change."));
					final Faction F=CMLib.factions().getFaction(CMLib.factions().AlignID());
					if(F!=null)
					{
					 	final int bredth=F.maximum()-F.minimum();
						final int midpoint=F.minimum()+(bredth/2);
						final int distance=midpoint-target.fetchFaction(F.factionID());
						final int amt=target.fetchFaction(F.factionID())+(distance/8);
						final int change=amt-target.fetchFaction(F.factionID());
						CMLib.factions().postFactionChange(target,this, CMLib.factions().AlignID(),change);
					}
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
