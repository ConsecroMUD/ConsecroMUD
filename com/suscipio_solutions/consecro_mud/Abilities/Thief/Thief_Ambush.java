package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Ambush extends ThiefSkill
{
	@Override public String ID() { return "Thief_Ambush"; }
	private final static String localizedName = CMLib.lang().L("Ambush");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"AMBUSH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.fetchEffect("Thief_Hide")!=null)
		{
			mob.tell(L("You are already hiding."));
			return false;
		}

		if(mob.isInCombat())
		{
			mob.tell(L("Not while in combat!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> H=mob.getGroupMembers(new HashSet<MOB>());
		if(!H.contains(mob)) H.add(mob);
		int numBesidesMe=0;
		for (final Object element : H)
		{
			final MOB M=(MOB)element;
			if((M!=mob)&&(mob.location().isInhabitant(M)))
				numBesidesMe++;
		}
		if(numBesidesMe==0)
		{
			mob.tell(L("You need a group to set up an ambush!"));
			return false;
		}
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB M=mob.location().fetchInhabitant(i);
			if((M!=null)&&(M!=mob)&&(!H.contains(M))&&(CMLib.flags().canSee(M)))
			{
				mob.tell(M,null,null,L("<S-NAME> is watching you too closely."));
				return false;
			}
		}
		boolean success=proficiencyCheck(mob,0,auto);

		if(!success)
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to set up an ambush, but fail(s)."));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_ACTION:(CMMsg.MSG_DELICATE_HANDS_ACT|CMMsg.MASK_MOVE),L("<S-NAME> set(s) up an ambush, directing everyone to hiding places."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				final Ability hide=CMClass.getAbility("Thief_Hide");
				for (final Object element : H)
				{
					final MOB M=(MOB)element;
					hide.invoke(M,M,true,adjustedLevel(mob,asLevel));
				}
			}
			else
				success=false;
		}
		return success;
	}
}
