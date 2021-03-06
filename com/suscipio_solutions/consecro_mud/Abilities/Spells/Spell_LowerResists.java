package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_LowerResists extends Spell
{
	@Override public String ID() { return "Spell_LowerResists"; }
	private final static String localizedName = CMLib.lang().L("Lower Resistance");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Lowered Resistances)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}

	int amount=0;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			mob.tell(L("Your cold weakness is now gone."));

		super.unInvoke();

	}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		for(final int i : CharStats.CODES.SAVING_THROWS())
			affectedStats.setStat(i,affectedStats.getStat(i)-amount);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A shimmering unresistable field appears around <T-NAMESELF>."):L("^S<S-NAME> invoke(s) a shimmering unresistable field around <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int a=0;a<target.numEffects();a++) // personal effects
				{
					final Ability A=target.fetchEffect(a);
					if((!A.isAutoInvoked())
					&&(A.canBeUninvoked())
					&&((A.classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_ABJURATION))
					{
						final int x=target.numEffects();
						A.unInvoke();
						if(x>target.numEffects())
							a--;
					}
				}
				amount=((mob.phyStats().level()+(2*getXLEVELLevel(mob)))-target.phyStats().level())*5;
				if(amount<5) amount=5;
				success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
			}
		}
		else
			maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to invoke lowered resistances on <T-NAMESELF>, but fail(s)."));

		return success;
	}
}
