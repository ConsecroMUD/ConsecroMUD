package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_CarefulStep extends ThiefSkill
{
	@Override public String ID() { return "Thief_CarefulStep"; }
	private final static String localizedName = CMLib.lang().L("Careful Step");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public double castingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillActionCost(ID(),CMath.div(CMProps.getIntVar(CMProps.Int.DEFABLETIME),50.0));}
	@Override public double combatCastingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillCombatActionCost(ID(),CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMABLETIME),50.0));}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"CARESTEP","CAREFULSTEP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC; }

	@Override
	public boolean preInvoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel, int secondsElapsed, double actionsRemaining)
	{
		if(secondsElapsed==0)
		{
			String dir=CMParms.combine(commands,0);
			if(commands.size()>0) dir=commands.get(commands.size()-1);
			final int dirCode=Directions.getGoodDirectionCode(dir);
			if(dirCode<0)
			{
				mob.tell(L("Step where?"));
				return false;
			}
			if(mob.isInCombat())
			{
				mob.tell(L("Not while you are fighting!"));
				return false;
			}

			if((mob.location().getRoomInDir(dirCode)==null)||(mob.location().getExitInDir(dirCode)==null))
			{
				mob.tell(L("Step where?"));
				return false;
			}
			final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_VISUAL:CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> start(s) walking carefully @x1.",Directions.getDirectionName(dirCode)));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			else
				return false;
		}
		return true;
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String dir=CMParms.combine(commands,0);
		if(commands.size()>0) dir=(String)commands.lastElement();
		final int dirCode=Directions.getGoodDirectionCode(dir);
		if(!preInvoke(mob,commands,givenTarget,auto,asLevel,0,0.0))
			return false;

		final MOB highestMOB=getHighestLevelMOB(mob,null);
		int levelDiff=mob.phyStats().level()+(2*super.getXLEVELLevel(mob))-getMOBLevel(highestMOB);

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=false;
		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_VISUAL:CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> walk(s) carefully @x1.",Directions.getDirectionName(dirCode)));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(levelDiff<0)
				levelDiff=levelDiff*8;
			else
				levelDiff=levelDiff*10;
			success=proficiencyCheck(mob,levelDiff,auto);
			final int oldDex=mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY);
			if(success)
				mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,oldDex+100);
			mob.recoverCharStats();
			CMLib.tracking().walk(mob,dirCode,false,false);
			if(oldDex!=mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY))
				mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,oldDex);
			mob.recoverCharStats();
		}
		return success;
	}

}
