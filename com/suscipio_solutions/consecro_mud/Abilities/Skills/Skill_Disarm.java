package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Disarm extends StdSkill
{
	@Override public String ID() { return "Skill_Disarm"; }
	private final static String localizedName = CMLib.lang().L("Disarm");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"DISARM"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			final MOB victim=mob.getVictim();
			if(victim==null)
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.fetchWieldedItem()==null)
				return Ability.QUALITY_INDIFFERENT;
			Item hisWeapon=victim.fetchWieldedItem();
			if(hisWeapon==null) hisWeapon=victim.fetchHeldItem();
			if((hisWeapon==null)
			||(!(hisWeapon instanceof Weapon))
			||((((Weapon)hisWeapon).weaponClassification()==Weapon.CLASS_NATURAL)))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!mob.isInCombat())
		{
			mob.tell(L("You must be in combat to do this!"));
			return false;
		}
		final MOB victim=super.getTarget(mob, commands, givenTarget);
		if(victim==null) return false;
		if(((victim==mob.getVictim())&&(mob.rangeToTarget()>0))
		||((victim.getVictim()==mob)&&(victim.rangeToTarget()>0)))
		{
			mob.tell(L("You are too far away to disarm!"));
			return false;
		}
		if(mob.fetchWieldedItem()==null)
		{
			mob.tell(L("You need a weapon to disarm someone!"));
			return false;
		}
		Item hisWeapon=victim.fetchWieldedItem();
		if(hisWeapon==null) hisWeapon=victim.fetchHeldItem();
		if((hisWeapon==null)
		||(!(hisWeapon instanceof Weapon))
		||((((Weapon)hisWeapon).weaponClassification()==Weapon.CLASS_NATURAL)))
		{
			mob.tell(L("@x1 is not wielding a weapon!",victim.charStats().HeShe()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=victim.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob)));
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;
		final boolean hit=(auto)||CMLib.combat().rollToHit(mob,victim);
		final boolean success=proficiencyCheck(mob,-levelDiff,auto)&&(hit);
		if((success)
		   &&((hisWeapon.fitsOn(Wearable.WORN_WIELD))
			  ||hisWeapon.fitsOn(Wearable.WORN_WIELD|Wearable.WORN_HELD)))
		{
			if(mob.location().show(mob,victim,this,CMMsg.MSG_NOISYMOVEMENT,null))
			{
				final CMMsg msg=CMClass.getMsg(victim,hisWeapon,null,CMMsg.MSG_DROP,null);
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(victim,msg);
					mob.location().show(mob,victim,CMMsg.MSG_NOISYMOVEMENT,auto?L("<T-NAME> is disarmed!"):L("<S-NAME> disarm(s) <T-NAMESELF>!"));
				}
			}
		}
		else
			maliciousFizzle(mob,victim,L("<S-NAME> attempt(s) to disarm <T-NAMESELF> and fail(s)!"));
		return success;
	}

}
