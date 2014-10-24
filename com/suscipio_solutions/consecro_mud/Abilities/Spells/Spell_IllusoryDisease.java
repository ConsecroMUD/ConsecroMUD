package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_IllusoryDisease extends Spell implements DiseaseAffect
{
	@Override public String ID() { return "Spell_IllusoryDisease"; }
	private final static String localizedName = CMLib.lang().L("Illusory Disease");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Diseased)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}
	@Override public int difficultyLevel(){return 9;}
	@Override public int spreadBitmap() { return 0; }
	protected int diseaseTick=5;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_STRENGTH,(int)Math.round(CMath.div(affectableStats.getStat(CharStats.STAT_STRENGTH),2.0)));
	}

	@Override
	public String getHealthConditionDesc()
	{
		return ""; // not really a condition
	}

	@Override public int abilityCode(){return 0;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if((affected==null)||(invoker==null)) return false;

		final MOB mob=(MOB)affected;
		if((--diseaseTick)<=0)
		{
			diseaseTick=5;
			String str=null;
			switch(CMLib.dice().roll(1,5,0))
			{
			case 1:
				str=L("<S-NAME> double(s) over and dry heave(s).");
				break;
			case 2:
				str=L("<S-NAME> sneeze(s). AAAAAAAAAAAAAACHOOO!!!!");
				break;
			case 3:
				str=L("<S-NAME> shake(s) feverishly.");
				break;
			case 4:
				str=L("<S-NAME> look(s) around weakly.");
				break;
			case 5:
				str=L("<S-NAME> cough(s) and shudder(s) feverishly.");
				break;
			}
			if(str!=null)
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,str);
			return true;
		}
		return true;
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
			mob.tell(L("You begin to feel better."));
	}



	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> incant(s) at <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> get(s) sick!"));
					success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> incant(s) at <T-NAMESELF>, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
