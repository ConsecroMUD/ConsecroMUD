package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_StripItem extends ThiefSkill
{
	@Override public String ID() { return "Thief_StripItem"; }
	private final static String localizedName = CMLib.lang().L("Strip Item");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	private static final String[] triggerStrings =I(new String[] {"STRIPITEM"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	public int code=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!(target instanceof MOB))
				return Ability.QUALITY_INDIFFERENT;
			if(((MOB)target).amDead()||(!CMLib.flags().canBeSeenBy(target,mob)))
				return Ability.QUALITY_INDIFFERENT;
			if(!((MOB)target).mayIFight(mob))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String itemToSteal="all";
		if(!auto)
		{
			if(commands.size()<2)
			{
				mob.tell(L("Strip what off of whom?"));
				return false;
			}
			itemToSteal=(String)commands.elementAt(0);
		}

		MOB target=null;
		if((givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		else
			target=mob.location().fetchInhabitant(CMParms.combine(commands,1));
		if((target==null)||(target.amDead())||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",CMParms.combine(commands,1)));
			return false;
		}
		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(getXLEVELLevel(mob)*2));
		if((!target.mayIFight(mob))||(levelDiff>15))
		{
			mob.tell(L("You cannot strip anything off of @x1.",target.charStats().himher()));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Item stolen=target.fetchItem(null,Wearable.FILTER_WORNONLY,itemToSteal);
		if((stolen==null)||(!CMLib.flags().canBeSeenBy(stolen,mob)))
		{
			mob.tell(L("@x1 doesn't seem to be wearing '@x2'.",target.name(mob),itemToSteal));
			return false;
		}
		if(stolen.amWearingAt(Wearable.WORN_WIELD))
		{
			mob.tell(L("@x1 is wielding @x2! Try disarm!",target.name(mob),stolen.name()));
			return false;
		}

		if(levelDiff>0)
			levelDiff=-(levelDiff*((!CMLib.flags().canBeSeenBy(mob,target))?5:15));
		else
			levelDiff=-(levelDiff*((!CMLib.flags().canBeSeenBy(mob,target))?1:2));
		final boolean success=proficiencyCheck(mob,levelDiff,auto);

		if(!success)
		{
			if((target.isMonster())&&(mob.getVictim()==null)) mob.setVictim(target);
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_NOISYMOVEMENT,auto?"":L("You fumble the attempt to strip @x1 off <T-NAME>; <T-NAME> spots you!",stolen.name()),CMMsg.MSG_NOISYMOVEMENT,auto?"":L("<S-NAME> tries to strip @x1 off you and fails!",stolen.name()),CMMsg.MSG_NOISYMOVEMENT,auto?"":L("<S-NAME> tries to strip @x1 off <T-NAME> and fails!",stolen.name()));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
		{
			String str=null;
			if(!auto) str=L("<S-NAME> strip(s) @x1 off <T-NAMESELF>.",stolen.name());

			final boolean alreadyFighting=(mob.getVictim()==target)||(target.getVictim()==mob);
			final String hisStr=str;
			final int hisCode=CMMsg.MSG_THIEF_ACT | ((target.mayIFight(mob))?CMMsg.MASK_MALICIOUS:0);

			CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_THIEF_ACT,str,hisCode,hisStr,CMMsg.NO_EFFECT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);

				if((!target.isMonster())&&(mob.isMonster())&&(!alreadyFighting))
				{
					if(target.getVictim()==mob)
						target.makePeace();
					if(mob.getVictim()==target)
						mob.makePeace();
				}
				else
				if(((hisStr==null)||mob.isMonster())
				&&(!alreadyFighting)
				&&(CMLib.dice().rollPercentage()>stolen.phyStats().level()))
				{
					if(target.getVictim()==mob)
						target.makePeace();
				}
				msg=CMClass.getMsg(target,stolen,null,CMMsg.MSG_REMOVE,CMMsg.MSG_REMOVE,CMMsg.MSG_NOISE,null);
				if(target.location().okMessage(target,msg))
					target.location().send(mob,msg);
			}
		}
		return success;
	}
}
