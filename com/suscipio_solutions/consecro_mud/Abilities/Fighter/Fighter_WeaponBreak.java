package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_WeaponBreak extends FighterSkill
{
	@Override public String ID() { return "Fighter_WeaponBreak"; }
	private final static String localizedName = CMLib.lang().L("Weapon Break");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BREAK"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			final MOB victim=mob.getVictim();
			if((!mob.isInCombat())||(victim==null))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.fetchWieldedItem()==null)
				return Ability.QUALITY_INDIFFERENT;
			final Item item=victim.fetchWieldedItem();
			if((item==null)
			||(!(item instanceof Weapon))
			||(((Weapon)item).weaponClassification()==Weapon.CLASS_NATURAL))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB victim=mob.getVictim();
		if((!mob.isInCombat())||(victim==null))
		{
			mob.tell(L("You must be in combat to do this!"));
			return false;
		}
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to try that!"));
			return false;
		}
		if((!auto)&&(mob.fetchWieldedItem()==null))
		{
			mob.tell(L("You need a weapon to break someone elses!"));
			return false;
		}
		final Item item=victim.fetchWieldedItem();
		if((item==null)
		||(!(item instanceof Weapon))
		||(((Weapon)item).weaponClassification()==Weapon.CLASS_NATURAL))
		{
			mob.tell(L("@x1 is not wielding a weapon!",victim.charStats().HeShe()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=victim.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;
		final Item hisWeapon=victim.fetchWieldedItem();
		final int chance=(-levelDiff)+(-(victim.charStats().getStat(CharStats.STAT_DEXTERITY)*2));
		final boolean hit=(auto)||CMLib.combat().rollToHit(mob,victim);
		final boolean success=proficiencyCheck(mob,chance,auto)&&(hit);
		if((success)
		   &&(hisWeapon!=null)
		   &&(hisWeapon.phyStats().ability()==0)
		   &&(!CMLib.flags().isABonusItems(hisWeapon))
		&&((hisWeapon.rawProperLocationBitmap()==Wearable.WORN_WIELD)
		   ||(hisWeapon.rawProperLocationBitmap()==Wearable.WORN_WIELD+Wearable.WORN_HELD)))
		{
			final String str=auto?L("@x1 break(s) in <T-HIS-HER> hands!",hisWeapon.name()):L("<S-NAME> attack(s) <T-NAMESELF> and destroy(s) @x1!",hisWeapon.name());
			hisWeapon.unWear();
			final CMMsg msg=CMClass.getMsg(mob,victim,this,CMMsg.MSG_NOISYMOVEMENT,str);
			final CMMsg msg2=CMClass.getMsg(mob,hisWeapon,this,CMMsg.MASK_ALWAYS|CMMsg.MASK_MALICIOUS|CMMsg.TYP_CAST_SPELL,null);
			if(mob.location().okMessage(mob,msg)&&mob.location().okMessage(mob,msg2))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if(msg2.value()<=0)
					hisWeapon.destroy();
				mob.location().recoverRoomStats();
			}
		}
		else
		if(hisWeapon != null)
			return maliciousFizzle(mob,victim,L("<S-NAME> attempt(s) to destroy @x1 and fail(s)!",hisWeapon.name()));
		else
			return maliciousFizzle(mob,victim,L("<S-NAME> attempt(s) to destroy <T-YOUPOSS> non-existant weapon and fail(s)!"));
		return success;
	}

}
