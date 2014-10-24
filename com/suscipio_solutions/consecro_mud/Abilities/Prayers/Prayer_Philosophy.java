package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_Philosophy extends Prayer
{
	@Override public String ID() { return "Prayer_Philosophy"; }
	private final static String localizedName = CMLib.lang().L("Philosophy");
	@Override public String name() { return localizedName; }
	@Override public long flags(){return Ability.FLAG_HOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Philosophy spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		int increase = 1;
		if (affectableStats.getCurrentClass().baseClass().equals("Fighter"))
			increase = 1;
		else
		if (affectableStats.getCurrentClass().baseClass().equals("Mage"))
			increase = 2;
		else
		if (affectableStats.getCurrentClass().baseClass().equals("Thief"))
			increase = 1;
		else
		if (affectableStats.getCurrentClass().baseClass().equals("Bard"))
			increase = 1;
		else
		if (affectableStats.getCurrentClass().baseClass().equals("Cleric"))
			increase = 3;
		else
		if (affectableStats.getCurrentClass().baseClass().equals("Druid"))
			increase = 3;
		increase += (super.getXLEVELLevel(invoker())+2)/3;
		affectableStats.setStat(CharStats.STAT_WISDOM,affectableStats.getStat(CharStats.STAT_WISDOM) + increase);
	}


	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("You stop pondering life and the mysteries of the universe."));
	}



	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> give(s) <T-NAMESELF> something to think about.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> start(s) pondering the mysteries of the universe."));
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> give(s) <T-NAMESELF> something to think about, but it just confuses <T-HIM-HER>."));


		// return whether it worked
		return success;
	}
}
