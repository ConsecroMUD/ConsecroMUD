package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Youth extends Spell
{
	@Override public String ID() { return "Spell_Youth"; }
	private final static String localizedName = CMLib.lang().L("Youth");
	@Override public String name() { return localizedName; }
	public int overridemana(){return Ability.COST_ALL;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> arms around <T-NAMESELF>, drawing forth <T-HIS-HER> youthful self.^?"));
		if(success)
		{
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if((target.baseCharStats().getStat(CharStats.STAT_AGE)<=0)
					||(target.baseCharStats().ageCategory()<=Race.AGE_YOUNGADULT))
				{
					mob.tell(mob,target,null,L("The magic appears to have had no effect upon <T-NAME>."));
					success=false;
				}
				else
				{
					final int[] chart=target.baseCharStats().getMyRace().getAgingChart();
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> grow(s) younger!"));
					final int cat=target.baseCharStats().ageCategory();
					int age=target.baseCharStats().getStat(CharStats.STAT_AGE);
					if(cat>=Race.AGE_ANCIENT)
					{
						final int diff=chart[Race.AGE_ANCIENT]-chart[Race.AGE_VENERABLE];
						age=age-chart[Race.AGE_ANCIENT];
						final int num=(diff>0)?(int)Math.abs(Math.floor(CMath.div(age,diff))):0;
						if(num<=0)
							age=(int)Math.round(CMath.div(chart[cat]+chart[cat-1],2.0));
						else
							age=target.baseCharStats().getStat(CharStats.STAT_AGE)-diff;
					}
					else
						age=(int)Math.round(CMath.div(chart[cat]+chart[cat-1],2.0));
					if(target.playerStats()!=null)
					{
						final TimeClock C=CMLib.time().localClock(target.getStartRoom());
						target.playerStats().getBirthday()[PlayerStats.BIRTHDEX_YEAR]=C.getYear()-age;
						final int day=C.getDayOfMonth();
						final int month=C.getMonth();
						final int bday=mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_MONTH];
						final int bmonth=mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_DAY];
						if((month<bmonth)||((month==bmonth)&&(day<bday)))
							age--;
						target.baseCharStats().setStat(CharStats.STAT_AGE,age);
					}
					else
						target.baseCharStats().setStat(CharStats.STAT_AGE,age);
					target.recoverCharStats();
					target.recoverPhyStats();
				}
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> wave(s) <S-HIS-HER> arms around <T-NAMESELF>, but the spell fizzles."));


		// return whether it worked
		return success;
	}

}
