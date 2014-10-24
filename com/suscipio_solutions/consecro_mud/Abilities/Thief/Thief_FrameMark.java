package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_FrameMark extends ThiefSkill
{
	@Override public String ID() { return "Thief_FrameMark"; }
	private final static String localizedName = CMLib.lang().L("Frame Mark");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"FRAME"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int overrideMana(){return 50;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	public MOB getMark(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if(A!=null)
			return A.mark;
		return null;
	}
	public int getMarkTicks(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if((A!=null)&&(A.mark!=null))
			return A.ticks;
		return -1;
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getMark(mob);
		if(target==null)
		{
			mob.tell(L("You need to have marked someone before you can frame him or her."));
			return false;
		}

		LegalBehavior B=null;
		if(mob.location()!=null) B=CMLib.law().getLegalBehavior(mob.location());
		if((B==null)
		||(!B.hasWarrant(CMLib.law().getLegalObject(mob.location()),mob)))
		{
			mob.tell(L("You aren't wanted for anything here."));
			return false;
		}
		final double goldRequired=target.phyStats().level() * 1000.0;
		final String localCurrency=CMLib.beanCounter().getCurrency(mob.location());
		if(CMLib.beanCounter().getTotalAbsoluteValue(mob,localCurrency)<goldRequired)
		{
			final String costWords=CMLib.beanCounter().nameCurrencyShort(localCurrency,goldRequired);
			mob.tell(L("You'll need at least @x1 on hand to frame @x2.",costWords,target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=(target.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)))*15);
		if(levelDiff<0) levelDiff=0;
		final boolean success=proficiencyCheck(mob,-levelDiff,auto);
		if(!success)
		{
			maliciousFizzle(mob,target,L("<S-NAME> attempt(s) frame <T-NAMESELF>, but <S-IS-ARE> way too obvious."));
			return false;
		}

		CMLib.beanCounter().subtractMoney(mob,localCurrency,goldRequired);

		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> frame(s) <T-NAMESELF>."),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			B.frame(CMLib.law().getLegalObject(mob.location()),mob,target);
		}
		return success;
	}

}
