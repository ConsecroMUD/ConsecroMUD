package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Druid_Rend extends StdAbility
{
	@Override public String ID() { return "Druid_Rend"; }
	private final static String localizedName = CMLib.lang().L("Rend");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"REND"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_WEAPON_USE;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		 if(mob!=null)
		 {
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				 return Ability.QUALITY_INDIFFERENT;
			if(!Druid_ShapeShift.isShapeShifted(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.charStats().getBodyPart(Race.BODY_LEG)<=0)
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				if(CMLib.flags().isStanding((MOB)target))
					return Ability.QUALITY_INDIFFERENT;
			}
		 }
		 return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to rend!"));
			return false;
		}
		if(!Druid_ShapeShift.isShapeShifted(mob))
		{
			mob.tell(L("You must be in your animal form to rend."));
			return false;
		}
		if(mob.charStats().getBodyPart(Race.BODY_LEG)<=0)
		{
			mob.tell(L("You must have legs to rend!"));
			return false;
		}
		final Ability A=mob.fetchEffect("Fighter_Pin");
		if(A!=null)
		{
			mob.tell(L("You rend your way out of the pin!"));
			A.unInvoke();
			mob.delEffect(A);
			CMLib.commands().postStand(mob,true);
			return true;
		}

		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(CMLib.flags().isStanding(target))
		{
			mob.tell(L("You can only rend someone who is on the ground!"));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,mob.charStats().getStat(CharStats.STAT_STRENGTH)-target.charStats().getStat(CharStats.STAT_STRENGTH)-10,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final int topDamage=(adjustedLevel(mob,asLevel)+getX2Level(mob))*2;
			int damage=CMLib.dice().roll(1,topDamage,adjustedLevel(mob,asLevel));
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()>0)
					damage = (int)Math.round(CMath.div(damage,2.0));
				CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_PIERCING,"^F^<FIGHT^><S-NAME> <DAMAGE> <T-NAME> by rending <T-HIM-HER> with <S-HIS-HER> feet!^</FIGHT^>^?");
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> fail(s) to rend <T-NAMESELF>."));

		// return whether it worked
		return success;
	}
}
