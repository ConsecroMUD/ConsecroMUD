package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TimeManager;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Prayer_ReligiousDoubt extends Prayer
{
	public static final long DOUBT_TIME=TimeManager.MILI_HOUR;

	@Override public String ID() { return "Prayer_ReligiousDoubt"; }
	private final static String localizedName = CMLib.lang().L("Religious Doubt");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override
	public String displayText()
	{
		if(otherSide) return "";
		return "(Religious Doubt)";
	}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	protected int tickUp=0;
	protected boolean otherSide=false;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(super.canBeUninvoked())
		{
			if(!otherSide)
				affectableStats.setStat(CharStats.STAT_FAITH,affectableStats.getStat(CharStats.STAT_FAITH)-100);
			else
				affectableStats.setStat(CharStats.STAT_FAITH,affectableStats.getStat(CharStats.STAT_FAITH)+100);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(super.canBeUninvoked()))
		{
			final boolean oldOther=otherSide;
			otherSide=(++tickUp)>tickDown;
			if((oldOther!=otherSide)&&(affected instanceof MOB))
				((MOB)affected).recoverCharStats();
		}
		return super.tick(ticking,tickID);
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		if(otherSide) return true;
		if(msg.target()==affected)
		{
			if(!(affected instanceof MOB)) return true;
			if((msg.source()!=msg.target())
			&&(msg.tool() instanceof Ability)
			&&(msg.tool().ID().equalsIgnoreCase("Skill_Convert")))
			{
				msg.source().tell((MOB)msg.target(),null,null,L("<S-NAME> is not interested in hearing your religious beliefs."));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 for <T-NAMESELF>.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> questioning <S-HIS-HER> faith, but does not seem convinced yet."));
				beneficialAffect(mob,target,asLevel,(int)(DOUBT_TIME/CMProps.getTickMillis()));
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for <T-NAMESELF>, but the magic fades.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
