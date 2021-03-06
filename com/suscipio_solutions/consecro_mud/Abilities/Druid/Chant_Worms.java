package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Worms extends Chant implements DiseaseAffect
{
	@Override public String ID() { return "Chant_Worms"; }
	private final static String localizedName = CMLib.lang().L("Worms");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Worms)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int abilityCode(){return 0;}
	@Override public int spreadBitmap() { return 0; }
	@Override public int difficultyLevel(){return 1;}

	int plagueDown=5;

	@Override
	public String getHealthConditionDesc()
	{
		return "Internally infested by numerous worm-like parasites.";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		if(!super.tick(ticking,tickID))
			return false;
		final MOB mob=(MOB)affected;
		mob.curState().adjHunger(-100,mob.maxState().maxHunger(mob.baseWeight()));
		if((--plagueDown)<=0)
		{
			plagueDown=5;
			if(invoker==null) invoker=mob;
			final int dmg=((mob.phyStats().level()+(2*super.getXLEVELLevel(invoker())))/4)+1;
			CMLib.combat().postDamage(invoker,mob,this,dmg,CMMsg.TYP_DISEASE,-1,"<T-NAME> feel(s) <T-HIS-HER> innards being consumed by worms!");
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-1);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)-1);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
			{
				spreadImmunity(mob);
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> worms disease is cured."));
			}
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":L("^S<S-NAME> chant(s) at <T-NAMESELF>!^?"));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,verbalCastMask(mob,target,auto)|CMMsg.TYP_DISEASE,null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if((msg.value()<=0)&&(msg2.value()<=0))
				{
					invoker=mob;
					maliciousAffect(mob,target,asLevel,688,-1);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> feel(s) very sick in the stomach!"));
				}
				else
					spreadImmunity(target);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
