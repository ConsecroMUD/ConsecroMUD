package com.suscipio_solutions.consecro_mud.Abilities.Thief;
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
public class Thief_Snatch extends ThiefSkill
{
	@Override public String ID() { return "Thief_Snatch"; }
	private final static String localizedName = CMLib.lang().L("Weapon Snatch");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	private static final String[] triggerStrings =I(new String[] {"SNATCH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			final Item weapon=mob.fetchWieldedItem();
			if(weapon==null)
				return Ability.QUALITY_INDIFFERENT;
			if(mob.freeWearPositions(Wearable.WORN_HELD,(short)0,(short)0)>0)
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				final MOB targetM=(MOB)target;
				final Item hisItem=targetM.fetchWieldedItem();
				if((hisItem==null)
				||(!(hisItem instanceof Weapon))
				||((((Weapon)hisItem).weaponClassification()==Weapon.CLASS_NATURAL)))
					return Ability.QUALITY_INDIFFERENT;
				if(hisItem.rawLogicalAnd())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=(auto&&(givenTarget instanceof MOB))?(MOB)givenTarget:mob.getVictim();
		if((!mob.isInCombat())||(target==null))
		{
			mob.tell(L("You must be in combat to do this!"));
			return false;
		}
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to disarm!"));
			return false;
		}
		final Item weapon=mob.fetchWieldedItem();
		if(weapon==null)
		{
			mob.tell(L("You need a weapon to disarm someone!"));
			return false;
		}
		else
		if(mob.freeWearPositions(Wearable.WORN_HELD,(short)0,(short)0)>0)
		{
			mob.tell(L("Your other hand needs to be free to do a weapon snatch."));
			return false;
		}

		final Item hisItem=target.fetchWieldedItem();
		if((hisItem==null)
		||(!(hisItem instanceof Weapon))
		||((((Weapon)hisItem).weaponClassification()==Weapon.CLASS_NATURAL)))
		{
			mob.tell(L("@x1 is not wielding a weapon!",target.charStats().HeShe()));
			return false;
		}
		else
		if(hisItem.rawLogicalAnd())
		{
			mob.tell(L("You can't snatch a two-handed weapon!"));
			return false;
		}
		final Weapon hisWeapon=(Weapon)hisItem;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(getXLEVELLevel(mob)*2));
		if(levelDiff>0)
			levelDiff=levelDiff*6;
		else
			levelDiff=0;
		final boolean hit=(auto)||CMLib.combat().rollToHit(mob,target);
		final boolean success=proficiencyCheck(mob,-levelDiff,auto)&&(hit);
		if((success)
		&&((hisWeapon.rawProperLocationBitmap()==Wearable.WORN_WIELD)
			||(hisWeapon.rawProperLocationBitmap()==Wearable.WORN_WIELD+Wearable.WORN_HELD)))
		{
			CMMsg msg=CMClass.getMsg(target,hisWeapon,null,CMMsg.MSG_DROP,null);
			final CMMsg msg2=CMClass.getMsg(mob,null,this,CMMsg.MSG_THIEF_ACT,null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(target,msg);
				mob.location().send(mob,msg2);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<S-NAME> disarm(s) <T-NAMESELF>!"));
				if(mob.location().isContent(hisWeapon))
				{
					CMLib.commands().postGet(mob,null,hisWeapon,true);
					if(mob.isMine(hisWeapon))
					{
						msg=CMClass.getMsg(mob,hisWeapon,null,CMMsg.MSG_HOLD,L("<S-NAME> snatch(es) the <T-NAME> out of mid-air!"));
						if(mob.location().okMessage(mob,msg))
							mob.location().send(mob,msg);
					}
				}
			}
		}
		else
			maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to disarm <T-NAMESELF> and fail(s)!"));
		return success;
	}
}
