package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
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
public class Disease_MummyRot extends StdAbility implements DiseaseAffect
{
	@Override public String ID() { return "Disease_MummyRot"; }
	private final static String localizedName = CMLib.lang().L("Mummy Rot");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Mummy Rot)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings =I(new String[] {"MUMMYROT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_DISEASE;}
	@Override public int abilityCode(){return 0;}
	@Override public int spreadBitmap() { return 0; }
	@Override public int difficultyLevel(){return 2;}

	int conDown=1;
	int diseaseTick=0;

	@Override
	public String getHealthConditionDesc()
	{
		return "Suffering the effects of "+name();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=10;
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> rotting away..."));
			conDown++;
			mob.recoverCharStats();
			return true;
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;
		super.unInvoke();
		if(canBeUninvoked())
		{
			if(!mob.amDead())
				spreadImmunity(mob);
			mob.tell(L("The rot is cured."));
		}
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		if(conDown<0) return;
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-conDown);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
		{
			conDown=-1;
			CMLib.combat().postDeath(invoker(),affected,null);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to touch!"));
			return false;
		}
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		String str=null;
		if(success)
		{
			str=auto?"":L("^S<S-NAME> extend(s) a rotting hand to <T-NAMESELF>!^?");
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_UNDEAD|(auto?CMMsg.MASK_ALWAYS:0),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> turn(s) grey!"));
					conDown=1;
					success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
				}
				else
					spreadImmunity(target);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> extend(s) a rotting hand to <T-NAMESELF>, but fail(s)."));

		return success;
	}
}
