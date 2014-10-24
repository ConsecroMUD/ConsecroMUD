package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Law;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Heresy extends Prayer
{
	@Override public String ID() { return "Prayer_Heresy"; }
	private final static String localizedName = CMLib.lang().L("Heresy");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int overrideMana(){return 100;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		LegalBehavior B=null;
		if(mob.location()!=null) B=CMLib.law().getLegalBehavior(mob.location());

		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final MOB oldVictim=mob.getVictim();
		if((success)&&(B!=null))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> accuse(s) <T-NAMESELF> of heresy@x1!^?",againstTheGods(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					MOB D=null;
					if(mob.getWorshipCharID().length()>0) D=CMLib.map().getDeity(mob.getWorshipCharID());
					String crime="heresy against <T-NAME>";
					String desc=null;
					if(D==null)
					{
						final Enumeration deities = CMLib.map().deities();
						if(deities.hasMoreElements())
							D=(MOB)deities.nextElement();
					}
					if(D==null)
					{
						crime="heresy against the gods";
						desc="Angering the gods will bring doom upon us all!";
					}
					else
						desc="Angering "+D.name()+" will bring doom upon us all!";
					final String crimeLocs="";
					final String crimeFlags="!witness";
					final int low=CMLib.ableMapper().lowestQualifyingLevel(ID());
					final int me=CMLib.ableMapper().qualifyingClassLevel(mob,this);
					int lvl=(me-low)/5;
					if(lvl<0) lvl=0;
					if(lvl>Law.PUNISHMENT_HIGHEST) lvl=Law.PUNISHMENT_HIGHEST;
					final String sentence=Law.PUNISHMENT_DESCS[lvl];
					B.addWarrant(CMLib.law().getLegalObject(mob.location()),target,D,crimeLocs,crimeFlags,crime,sentence,desc);
				}
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> accuse(s) <T-NAMESELF> of heresy@x1, but nothing happens.",againstTheGods(mob)));
		mob.setVictim(oldVictim);
		if(oldVictim==null) mob.makePeace();

		// return whether it worked
		return success;
	}
}
