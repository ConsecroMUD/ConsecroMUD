package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_SummonCold extends Chant
{
	@Override public String ID() { return "Chant_SummonCold"; }
	private final static String localizedName = CMLib.lang().L("Summon Cold");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public long flags(){return Ability.FLAG_WEATHERAFFECTING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_WEATHER_MASTERY;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if(CMath.bset(weatherQue(R),WEATHERQUE_COLD))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(((mob.location().domainType()&Room.INDOORS)>0)&&(!auto))
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int size=mob.location().getArea().numberOfProperIDedRooms();
		size=size/(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
		if(size<0) size=0;
		final boolean success=proficiencyCheck(mob,-size,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?L("^JThe sky changes color!^?"):L("^S<S-NAME> chant(s) into the sky for cold!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Climate C=mob.location().getArea().getClimateObj();
				final Climate oldC=(Climate)C.copyOf();
				switch(C.weatherType(mob.location()))
				{
				case Climate.WEATHER_BLIZZARD:
					C.setNextWeatherType(Climate.WEATHER_BLIZZARD);
					break;
				case Climate.WEATHER_CLEAR:
					C.setNextWeatherType(Climate.WEATHER_WINTER_COLD);
					break;
				case Climate.WEATHER_CLOUDY:
					C.setNextWeatherType(Climate.WEATHER_SNOW);
					break;
				case Climate.WEATHER_DROUGHT:
					C.setNextWeatherType(Climate.WEATHER_WINTER_COLD);
					break;
				case Climate.WEATHER_DUSTSTORM:
					C.setNextWeatherType(Climate.WEATHER_WINDY);
					break;
				case Climate.WEATHER_HAIL:
					C.setNextWeatherType(Climate.WEATHER_HAIL);
					break;
				case Climate.WEATHER_HEAT_WAVE:
					C.setNextWeatherType(Climate.WEATHER_WINDY);
					break;
				case Climate.WEATHER_RAIN:
					C.setNextWeatherType(Climate.WEATHER_SLEET);
					break;
				case Climate.WEATHER_SLEET:
					C.setNextWeatherType(Climate.WEATHER_SNOW);
					break;
				case Climate.WEATHER_SNOW:
					C.setNextWeatherType(Climate.WEATHER_SNOW);
					break;
				case Climate.WEATHER_THUNDERSTORM:
					C.setNextWeatherType(Climate.WEATHER_BLIZZARD);
					break;
				case Climate.WEATHER_WINDY:
					C.setNextWeatherType(Climate.WEATHER_WINTER_COLD);
					break;
				case Climate.WEATHER_WINTER_COLD:
					C.setNextWeatherType(Climate.WEATHER_WINTER_COLD);
					break;
				default:
					break;
				}
				C.forceWeatherTick(mob.location().getArea());
				Chant_CalmWeather.xpWorthyChange(mob,mob.location().getArea(),oldC,C);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) into the sky for cold, but the magic fizzles."));

		return success;
	}
}
