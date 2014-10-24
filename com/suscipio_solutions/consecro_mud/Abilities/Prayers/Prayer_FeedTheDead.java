package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_FeedTheDead extends Prayer
{
	@Override public String ID() { return "Prayer_FeedTheDead"; }
	private final static String localizedName = CMLib.lang().L("Feed The Dead");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_DEATHLORE;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY|Ability.FLAG_NOORDERING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		int amount=100;
		if(!auto)
		{
			if((commands.size()==0)||(!CMath.isNumber((String)commands.lastElement())))
			{
				mob.tell(L("Feed how much experience?"));
				return false;
			}
			amount=CMath.s_int((String)commands.lastElement());
			if((amount<=0)||((amount>mob.getExperience())
			&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.EXPERIENCE))
			&&!mob.charStats().getCurrentClass().expless()
			&&!mob.charStats().getMyRace().expless()))
			{
				mob.tell(L("You cannot feed @x1 experience.",""+amount));
				return false;
			}
			commands.removeElementAt(commands.size()-1);
		}
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(!target.charStats().getMyRace().racialCategory().equals("Undead"))
		{
			mob.tell(L("Only the undead may be fed in this way."));
			return false;
		}
		if(!target.isMonster())
		{
			mob.tell(L("That creature cannot be fed."));
			return false;
		}
		if(mob.isMonster() && (!auto) && (givenTarget==null))
		{
			mob.tell(L("You cannot feed the dead."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"<T-NAME> gain(s) fake life!":"^S<S-NAME> "+prayWord(mob)+" for <T-NAMESELF> to be fed.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				CMLib.leveler().postExperience(mob,null,null,-amount,false);
				if((mob.phyStats().level()>target.phyStats().level())&&(target.isMonster()))
					amount+=(mob.phyStats().level()-target.phyStats().level())
						  *(mob.phyStats().level()/10);
				CMLib.leveler().postExperience(target,null,null,amount,false);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for <T-NAMESELF> to be fed, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
