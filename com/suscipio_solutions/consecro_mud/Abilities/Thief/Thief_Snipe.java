package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Snipe extends ThiefSkill
{
	@Override public String ID() { return "Thief_Snipe"; }
	private final static String localizedName = CMLib.lang().L("Snipe");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"SNIPE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int overrideMana(){return 100;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(CMLib.flags().isSitting(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				if(CMLib.flags().canBeSeenBy(mob,(MOB)target))
					return Ability.QUALITY_INDIFFERENT;
				final Item w=mob.fetchWieldedItem();
				if((w==null)
				||(!(w instanceof Weapon)))
					return Ability.QUALITY_INDIFFERENT;
				final Weapon ww=(Weapon)w;
				if(((ww.weaponClassification()!=Weapon.CLASS_RANGED)&&(ww.weaponClassification()!=Weapon.CLASS_THROWN))
				||(w.maxRange()<=0))
					return Ability.QUALITY_INDIFFERENT;
				return Ability.QUALITY_MALICIOUS;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat())
		{
			mob.tell(L("Not while in combat!"));
			return false;
		}
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You need to stand up!"));
			return false;
		}
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;
		if(CMLib.flags().canBeSeenBy(mob,target))
		{
			mob.tell(L("@x1 is watching you too closely.",target.name(mob)));
			return false;
		}
		final Item w=mob.fetchWieldedItem();
		if((w==null)
		||(!(w instanceof Weapon)))
		{
			mob.tell(L("You need a weapon to snipe."));
			return false;
		}
		final Weapon ww=(Weapon)w;
		if(((ww.weaponClassification()!=Weapon.CLASS_RANGED)&&(ww.weaponClassification()!=Weapon.CLASS_THROWN))
		||(w.maxRange()<=0))
		{
			mob.tell(L("You need a ranged weapon to snipe."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final int code=CMMsg.MASK_MALICIOUS|CMMsg.MSG_THIEF_ACT;
		final String str=auto?"":L("<S-NAME> strike(s) <T-NAMESELF> from the shadows!");
		final int otherCode=success?code:CMMsg.NO_EFFECT;
		final String otherStr=success?str:null;
		final CMMsg msg=CMClass.getMsg(mob,target,this,code,str,otherCode,otherStr,otherCode,otherStr);
		if(mob.location().okMessage(mob,msg))
		{
			final boolean alwaysInvis=CMath.bset(mob.basePhyStats().disposition(),PhyStats.IS_INVISIBLE);
			if(!alwaysInvis) mob.basePhyStats().setDisposition(mob.basePhyStats().disposition()|PhyStats.IS_INVISIBLE);
			mob.recoverPhyStats();
			mob.location().send(mob,msg);
			CMLib.combat().postAttack(mob,target,w);
			if(!alwaysInvis) mob.basePhyStats().setDisposition(mob.basePhyStats().disposition()-PhyStats.IS_INVISIBLE);
			mob.recoverPhyStats();
			if(success)
			{
				final MOB oldVictim=target.getVictim();
				final MOB oldVictim2=mob.getVictim();
				if(oldVictim==mob) target.makePeace();
				if(oldVictim2==target) mob.makePeace();
				if(mob.fetchEffect("Thief_Hide")==null)
				{
					final Ability hide=mob.fetchAbility("Thief_Hide");
					if(hide!=null) hide.invoke(mob,null,false,asLevel);

					mob.location().recoverRoomStats();
					if(CMLib.flags().canBeSeenBy(mob,target))
					{
						target.setVictim(oldVictim);
						mob.setVictim(oldVictim2);
					}
				}
			}
		}
		return success;
	}
}
