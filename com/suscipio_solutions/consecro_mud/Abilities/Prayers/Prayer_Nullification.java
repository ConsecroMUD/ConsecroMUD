package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_Nullification extends Prayer
{
	@Override public String ID() { return "Prayer_Nullification"; }
	private final static String localizedName = CMLib.lang().L("Nullification");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_NEUTRALIZATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				for(final Enumeration<Ability> a=target.effects();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if((A!=null)&&(A.canBeUninvoked())&&(!A.isAutoInvoked())
					&&(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
					   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)
					   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_CHANT)
					   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)))
					{
						if((A.invoker()!=null)&&((A.invoker().phyStats().level()<=(mob.phyStats().level()+(2*super.getXLEVELLevel(mob))))))
							if((mob==target)&&(A.invoker()!=mob)&&(A.abstractQuality()==Ability.QUALITY_MALICIOUS))
								return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
							else
							if((mob.getVictim()==target)&&(A.invoker()!=mob)&&(A.abstractQuality()!=Ability.QUALITY_MALICIOUS))
								return super.castingQuality(mob, target,Ability.QUALITY_MALICIOUS);
					}
				}
			}
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB target=mob.location().fetchInhabitant(i);
			if((target!=null)&&(success))
			{
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) nullified."):L("^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>.^?"));
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					Ability revokeThis=null;
					boolean foundSomethingAtLeast=false;
					for(int a=0;a<target.numEffects();a++) // personal affects
					{
						final Ability A=target.fetchEffect(a);
						if((A!=null)&&(A.canBeUninvoked())&&(!A.isAutoInvoked())
						&&(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
						   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)
						   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_CHANT)
						   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)))
						{
							foundSomethingAtLeast=true;
							if((A.invoker()!=null)&&((A.invoker().phyStats().level()<=(mob.phyStats().level()+(2*super.getXLEVELLevel(mob))))))
								revokeThis=A;
						}
					}

					if(revokeThis==null)
					{
						if(foundSomethingAtLeast)
							mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("The magic on <T-NAME> appears too powerful to be nullified."));
						else
						if(auto)
							mob.tell(mob,target,null,L("Nothing seems to be happening to <T-NAME>."));
					}
					else
						revokeThis.unInvoke();
				}
			}
			else
				beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, but @x1 does not heed.",hisHerDiety(mob)));
		}

		// return whether it worked
		return success;
	}
}
