package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_MassHarm extends Prayer
{
	@Override public String ID() { return "Prayer_MassHarm"; }
	private final static String localizedName = CMLib.lang().L("Mass Harm");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).charStats().getMyRace().racialCategory().equals("Undead"))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_OTHERS);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final int numEnemies=h.size();
		for (final Object element : h)
		{
			final MOB target=(MOB)element;
			if(target!=mob)
			{
				if(success)
				{
					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final Room R=target.location();
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?L("<T-NAME> become(s) surrounded by a dark cloud."):L("^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, @x1.^?",prayingWord(mob)));
					final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_UNDEAD|(auto?CMMsg.MASK_ALWAYS:0),null);
					if((R.okMessage(mob,msg))&&((R.okMessage(mob,msg2))))
					{
						R.send(mob,msg);
						R.send(mob,msg2);
						if((msg.value()<=0)&&(msg2.value()<=0))
						{
							final int harming=CMLib.dice().roll(1,(adjustedLevel(mob,asLevel)+ 24) / numEnemies,8);
							CMLib.combat().postDamage(mob,target,this,harming,CMMsg.MASK_ALWAYS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"The unholy spell <DAMAGE> <T-NAME>!");
						}
					}
				}
				else
					maliciousFizzle(mob,target,L("<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, @x1, but @x2 does not heed.",prayingWord(mob),hisHerDiety(mob)));
			}
		}


		// return whether it worked
		return success;
	}
}
