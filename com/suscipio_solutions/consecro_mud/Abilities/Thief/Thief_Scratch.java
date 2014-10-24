package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Scratch extends ThiefSkill
{
	@Override public String ID() { return "Thief_Scratch"; }
	private final static String localizedName = CMLib.lang().L("Scratch");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"SCRATCH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public int overrideMana(){return 1;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(target.charStats().getBodyPart(Race.BODY_HAND)<0)
		{
			mob.tell(L("@x1 must stand up first!",target.name(mob)));
			return false;
		}

		
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		String str=null;
		if((success)&&(CMLib.combat().rollToHit(mob, target)))
		{
			str=auto?null:L("^F^<FIGHT^><S-NAME> descretely swipe(s) at <T-NAMESELF>!^</FIGHT^>^?");
			final int attackCode =  CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0);
			final int hideOverrideCode = CMLib.flags().isHidden(mob)?CMMsg.TYP_LOOK:attackCode;
			final Set<MOB> combatants=CMLib.combat().getAllFightingAgainst(mob, new HashSet<MOB>(1));
			final boolean makePeace=(!mob.isInCombat()) && (combatants.size()==0);
			final CMMsg msg=CMClass.getMsg(mob,target,this,attackCode,str,attackCode,str,hideOverrideCode,str);
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final int damage=CMLib.dice().roll(1, 4, 0);
				CMLib.combat().postDamage(mob, target, mob.myNaturalWeapon(), damage, CMMsg.MASK_ALWAYS|CMMsg.TYP_WEAPONATTACK,
						mob.myNaturalWeapon().weaponType(),"<S-YOUPOSS> scratch <DAMAGES> <T-NAME>!");
				if(CMLib.flags().isHidden(mob) && makePeace)
				{
					mob.makePeace();
					CMLib.combat().forcePeaceAllFightingAgainst(mob, combatants);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to scratch <T-NAMESELF>, but miss(es)."));

		return success;
	}

}
