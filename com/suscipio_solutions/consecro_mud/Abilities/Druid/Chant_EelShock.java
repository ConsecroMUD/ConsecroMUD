package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_EelShock extends Chant
{
	@Override public String ID() { return "Chant_EelShock"; }
	private final static String localizedName = CMLib.lang().L("Eel Shock");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Stunned)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int maxRange() {return 3;}
	@Override public int minRange() {return 0;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_WEATHER_MASTERY;}
	@Override public long flags(){return Ability.FLAG_AIRBASED;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
		super.unInvoke();
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.tell(L("<S-YOUPOSS> are no longer stunned."));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(PhyStats.IS_SITTING);
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amISource(mob))
		&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
		&&(msg.sourceMajor()>0))
		{
			mob.tell(L("You are stunned."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}

	private boolean roomWet(Room location)
	{
		if(location.domainType() == Room.DOMAIN_INDOORS_UNDERWATER ||
		   location.domainType() == Room.DOMAIN_INDOORS_WATERSURFACE ||
		   location.domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER ||
		   location.domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE ||
		   location.domainType() == Room.DOMAIN_OUTDOORS_SWAMP)
			return true;

		final Area currentArea = location.getArea();
		if(currentArea.getClimateObj().weatherType(location) == Climate.WEATHER_RAIN ||
		   currentArea.getClimateObj().weatherType(location) == Climate.WEATHER_THUNDERSTORM)
			return true;
		return false;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Set<MOB> h=CMLib.combat().properTargets(this,mob,false);
			if(h==null)
				return Ability.QUALITY_INDIFFERENT;
			final Room location=mob.location();
			if(location!=null)
			{
				if(!roomWet(location))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=CMLib.combat().properTargets(this,mob,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth shocking."));
			return false;
		}

		final Room location = mob.location();

		if(!roomWet(location))
		{
				mob.tell(L("It's too dry to invoke this chant."));
				return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),L("^S<S-NAME> chant(s) and electrical sparks dance across <S-HIS-HER> skin.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastMask(mob,target,auto)|CMMsg.TYP_ELECTRIC,L("<T-NAME> is stunned."));
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						if(msg.value()<=0)
							maliciousAffect(mob,target,asLevel,3+super.getXLEVELLevel(mob)+(2*super.getX1Level(mob)),-1);
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> sees tiny sparks dance across <S-HIS-HER> skin, but nothing more happens."));
		// return whether it worked
		return success;
	}
}
