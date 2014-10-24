package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_BodyToss extends MonkSkill
{
	@Override public String ID() { return "Fighter_BodyToss"; }
	private final static String localizedName = CMLib.lang().L("Body Toss");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BODYTOSS"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_GRAPPLING;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(anyWeapons(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.rangeToTarget()>0)
				return Ability.QUALITY_INDIFFERENT;
			if(CMLib.flags().isSitting(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.charStats().getBodyPart(Race.BODY_ARM)<=1)
				return Ability.QUALITY_INDIFFERENT;
			if(target.basePhyStats().weight()>(mob.basePhyStats().weight()*2))
				return Ability.QUALITY_INDIFFERENT;
			if(target.fetchEffect(ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=mob.getVictim();
		if(target==null)
		{
			mob.tell(L("You can only do this in combat!"));
			return false;
		}
		if(anyWeapons(mob))
		{
			mob.tell(L("You must be unarmed to use this skill."));
			return false;
		}
		if(mob.rangeToTarget()>0)
		{
			mob.tell(L("You must get closer to @x1 first!",target.charStats().himher()));
			return false;
		}
		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You need to stand up!"));
			return false;
		}
		if(mob.charStats().getBodyPart(Race.BODY_ARM)<=1)
		{
			mob.tell(L("You need arms to do this."));
			return false;
		}
		if(target.basePhyStats().weight()>(mob.basePhyStats().weight()*2))
		{
			mob.tell(L("@x1 is too big for you to toss!",target.name(mob)));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),L("^F^<FIGHT^><S-NAME> pick(s) up <T-NAMESELF> and toss(es) <T-HIM-HER> into the air!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				int dist=2+getXLEVELLevel(mob);
				if(mob.location().maxRange()<2) dist=mob.location().maxRange();
				mob.setAtRange(dist);
				target.setAtRange(dist);
				CMLib.combat().postDamage(mob,target,this,CMLib.dice().roll(1,12,0),CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BASHING,"The hard landing <DAMAGE> <T-NAME>!");
				if(mob.getVictim()==null) mob.setVictim(null); // correct range
				if(target.getVictim()==null) target.setVictim(null); // correct range
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to pick up <T-NAMESELF>, but fail(s)."));

		// return whether it worked
		return success;
	}
}
