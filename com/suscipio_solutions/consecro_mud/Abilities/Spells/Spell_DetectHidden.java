package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_DetectHidden extends Spell
{
	@Override public String ID() { return "Spell_DetectHidden"; }
	private final static String localizedName = CMLib.lang().L("Detect Hidden");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Detecting Hidden)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int enchantQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}


	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("Your vision is no longer as keen."));
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING,affected.phyStats().level()+(2*getXLEVELLevel(invoker()))+100+affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_HIDDEN);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				final Room R=((MOB)target).location();
				boolean found=false;
				if(R!=null)
					for(int r=0;r<R.numInhabitants();r++)
					{
						final MOB M=R.fetchInhabitant(r);
						if((M!=null)&&(M!=mob)&&(M!=target)&&(CMLib.flags().isHidden(M)))
						{ found=true; break;}
					}
				if(!found) return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already detecting hidden things."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> gain(s) keen vision!"):L("^S<S-NAME> incant(s) softly and gain(s) keen vision!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> incant(s) and open(s) <S-HIS-HER> keen eyes, but the spell fizzles."));

		return success;
	}
}
