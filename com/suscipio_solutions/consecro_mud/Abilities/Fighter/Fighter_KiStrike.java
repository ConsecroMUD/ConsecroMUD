package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_KiStrike extends FighterSkill
{
	@Override public String ID() { return "Fighter_KiStrike"; }
	private final static String localizedName = CMLib.lang().L("Ki Strike");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Ki Strike)");
	@Override public String displayText() { return localizedStaticDisplay; }
	private static final String[] triggerStrings =I(new String[] {"KISTRIKE","KI"});
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_PUNCHING;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&((msg.tool() instanceof Weapon)||(msg.tool()==null))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.value()>0))
		{
			final MOB mob=(MOB)affected;
			if((CMLib.flags().aliveAwakeMobile(mob,true))
			&&(mob.location()!=null))
			{
				mob.location().show(mob,null,CMMsg.MSG_SPEAK,L("<S-NAME> yell(s) 'KIA'!"));
				msg.setValue(msg.value()+adjustedLevel(invoker(),0));
				unInvoke();
			}

		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(!CMLib.flags().canSpeak(mob))
		{
			mob.tell(L("You can't speak!"));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> concentrate(s) <S-HIS-HER> strength."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,mob.isInCombat()?2:4);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> lose(s) concentration."));

		// return whether it worked
		return success;
	}
}
