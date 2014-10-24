package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Mug extends ThiefSkill
{
	@Override public String ID() { return "Thief_Mug"; }
	private final static String localizedName = CMLib.lang().L("Mug");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"MUG"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=mob.getVictim();
		if(!mob.isInCombat())
		{
			mob.tell(L("You can only mug someone you are fighting!"));
			return false;
		}
		String itemToSteal="all";
		if(!auto)
		{
			if(commands.size()<1)
			{
				mob.tell(L("Mug what from @x1?",target.name(mob)));
				return false;
			}
			itemToSteal=CMParms.combine(commands,0);
		}
		final int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(getXLEVELLevel(mob)*2));
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Item stolen=target.fetchItem(null,Wearable.FILTER_UNWORNONLY,itemToSteal);
		if(stolen instanceof Coins)
		{
			mob.tell(L("You can not mug that from @x1.",target.name(mob)));
			return false;
		}
		final boolean success=proficiencyCheck(mob,levelDiff,auto);
		if(!success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_NOISYMOVEMENT,auto?"":L("You fumble the attempt to mug <T-NAME>!"),CMMsg.MSG_NOISYMOVEMENT,auto?"":L("<S-NAME> tries to mug you and fails!"),CMMsg.MSG_NOISYMOVEMENT,auto?"":L("<S-NAME> tries to mug <T-NAME> and fails!"));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
		{
			String str=null;
			int code=(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_THIEF_ACT;
			if(!auto)
				if((stolen!=null)&&(stolen.amWearingAt(Wearable.IN_INVENTORY)))
					str=L("<S-NAME> mug(s) <T-NAMESELF>, stealing @x1 from <T-HIM-HER>.",stolen.name());
				else
				{
					code=CMMsg.MSG_QUIETMOVEMENT;
					str=L("<S-NAME> attempt(s) to mug <T-HIM-HER>, but it doesn't appear @x1 has that in <T-HIS-HER> inventory!",target.charStats().heshe());
				}

			CMMsg msg=CMClass.getMsg(mob,target,this,code,str,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_THIEF_ACT|CMMsg.MASK_MALICIOUS,str,CMMsg.NO_EFFECT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				msg=CMClass.getMsg(target,stolen,null,CMMsg.MSG_DROP,CMMsg.MSG_DROP,CMMsg.MSG_NOISE,null);
				if(target.location().okMessage(target,msg))
				{
					target.location().send(mob,msg);
					msg=CMClass.getMsg(mob,stolen,null,CMMsg.MSG_GET,CMMsg.MSG_GET,CMMsg.MSG_NOISE,null);
					if(mob.location().okMessage(mob,msg))
						mob.location().send(mob,msg);
				}
			}
		}
		return success;
	}

}
