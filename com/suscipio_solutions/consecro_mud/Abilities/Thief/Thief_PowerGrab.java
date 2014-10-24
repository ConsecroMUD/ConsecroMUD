package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_PowerGrab extends ThiefSkill
{
	@Override public String ID() { return "Thief_PowerGrab"; }
	private final static String localizedName = CMLib.lang().L("Power Grab");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"POWERGRAB"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public double castingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillActionCost(ID(),0.0);}
	@Override public double combatCastingTime(final MOB mob, final List<String> cmds){return CMProps.getSkillCombatActionCost(ID(),0.0);}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item possibleContainer=possibleContainer(mob,commands,true,Wearable.FILTER_UNWORNONLY);
		final Item target=super.getTarget(mob, mob.location(), givenTarget, possibleContainer, commands, Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		final boolean success=proficiencyCheck(mob,0,auto);
		if(!success)
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to power grab something and fail(s)."));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT|CMMsg.MASK_MAGIC,auto?"":L("^S<S-NAME> carefully attempt(s) to acquire <T-NAME>^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final int level=target.basePhyStats().level();
				final int level2=target.phyStats().level();
				target.basePhyStats().setLevel(1);
				target.phyStats().setLevel(1);
				CMLib.commands().postGet(mob, possibleContainer, target, false);
				target.basePhyStats().setLevel(level);
				target.phyStats().setLevel(level2);
				target.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		}
		return success;
	}
}
