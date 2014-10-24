package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Palm extends ThiefSkill
{
	@Override public String ID() { return "Thief_Palm"; }
	private final static String localizedName = CMLib.lang().L("Palm");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"PALM"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public double castingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillActionCost(ID(),0.0);}
	@Override public double combatCastingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillCombatActionCost(ID(),0.0);}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final boolean success=proficiencyCheck(mob,0,auto);
		if(!success)
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to palm something and fail(s)."));
		else
		{
			if((commands.size()>0)&&(!((String)commands.lastElement()).equalsIgnoreCase("UNOBTRUSIVELY")))
			   commands.addElement("UNOBTRUSIVELY");
			try
			{
				final Command C=CMClass.getCommand("Get");
				commands.insertElementAt("GET",0);
				if(C!=null) C.execute(mob,commands,0);
			}
			catch(final Exception e)
			{}
		}
		return success;
	}
}
