package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Slapstick extends BardSkill
{
	@Override public String ID() { return "Skill_Slapstick"; }
	private final static String localizedName = CMLib.lang().L("Slapstick");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SLAPSTICK"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isInCombat())
			&&(target instanceof MOB)
			&&(((MOB)target)!=mob))
				return Ability.QUALITY_MALICIOUS;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		String str=null;
		if(success)
		{
			if(auto) str=L("<T-NAME> is drained of mana!");
			else
			switch(CMLib.dice().roll(1,10,0))
			{
			case 1:
				str=L("<S-NAME> stand(s) on <S-HIS-HER> head and stick(s) <S-HIS-HER> tounge out at <T-NAMESELF>.");
				break;
			case 2:
				str=L("<S-NAME> make(s) a silly face at <T-NAMESELF> and gyrate(s).");
				break;
			case 3:
				str=L("<S-NAME> do(es) the monkey dance with <T-NAMESELF>.");
				break;
			case 4:
				str=L("<S-NAME> trip(s) on <T-YOUPOSS> foot, fall(s) on <S-HIS-HER> back, and bounce(s) back up.");
				break;
			case 5:
				str=L("<S-NAME> smile(s) at <T-NAMESELF> as <S-HIS-HER> drawers drop.");
				break;
			case 6:
				str=L("<S-NAME> run(s) behind <T-NAMESELF>, throw(s) a pie in the air, and catch(es) it on <S-HIS-HER> face.");
				break;
			case 7:
				str=L("<S-NAME> feign(s) an inability to pull something from <S-HIS-HER> nose, looking to <T-NAMESELF> in distress.");
				break;
			case 8:
				str=L("<S-NAME> look(s) at <T-NAMESELF> as <S-HIS-HER> hands get into a silly fight with each other.");
				break;
			case 9:
				str=L("<S-NAME> turn(s) <S-HIS-HER> back to <T-NAMESELF>, tap(s) <S-HIM-HERSELF> on the shoulder with <T-YOUPOSS> hand, and then feign(s) ignorance about the source.");
				break;
			case 10:
				str=L("<S-NAME> do(es) a silly slapstick routine for <T-NAMESELF>.");
				break;
			}
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_SOUND|CMMsg.MASK_HANDS|CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.curState().adjMana(-mob.phyStats().level(),target.maxState());
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to do something silly to <T-NAMESELF>, but fail(s)."));

		return success;
	}

}
