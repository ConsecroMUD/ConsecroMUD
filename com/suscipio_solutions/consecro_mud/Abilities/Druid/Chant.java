package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant extends StdAbility
{
	@Override public String ID() { return "Chant"; }
	private final static String localizedName = CMLib.lang().L("a Druidic Chant");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return "("+name()+")"; }
	protected boolean renderedMundane=false;

	/** codes: -1=do nothing, 1=wind, 2=rain, 4=hot, 8=cold, 16=calm */
	public final static int WEATHERQUE_NADA=0;
	public final static int WEATHERQUE_WIND=1;
	public final static int WEATHERQUE_RAIN=2;
	public final static int WEATHERQUE_HOT=4;
	public final static int WEATHERQUE_COLD=8;
	public final static int WEATHERQUE_CALM=16;

	@Override
	protected int verbalCastCode(MOB mob, Physical target, boolean auto)
	{
		if(renderedMundane)
		{
			int affectType=CMMsg.MSG_CAST_VERBAL_SPELL;
			affectType=CMMsg.MSG_NOISE|CMMsg.MASK_MOUTH;
			if(abstractQuality()==Ability.QUALITY_MALICIOUS)
				affectType=affectType|CMMsg.MASK_MALICIOUS;
			if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;
			return affectType;
		}
		return super.verbalCastCode(mob,target,auto);
	}
	private static final String[] triggerStrings =I(new String[] {"CHANT","CH"});
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}

	@Override
	public void setMiscText(String newText)
	{
		if(newText.equalsIgnoreCase("render mundane"))
			renderedMundane=true;
		else
			super.setMiscText(newText);
	}
	@Override public int classificationCode()	{ return renderedMundane?Ability.ACODE_SKILL:Ability.ACODE_CHANT;}

	/** codes: -1=do nothing, 1=wind, 2=rain, 4=hot, 8=cold, 16=calm */
	public int weatherQue(Room R)
	{
		if(R==null) return WEATHERQUE_NADA;
		if((R.domainType()&Room.INDOORS)>0) return WEATHERQUE_NADA;
		switch(R.getArea().getClimateObj().weatherType(R))
		{
		case Climate.WEATHER_BLIZZARD:
		case Climate.WEATHER_THUNDERSTORM:
		case Climate.WEATHER_HEAT_WAVE:
			return WEATHERQUE_NADA;
		case Climate.WEATHER_CLEAR: return WEATHERQUE_WIND|WEATHERQUE_RAIN|WEATHERQUE_HOT|WEATHERQUE_COLD;
		case Climate.WEATHER_CLOUDY: return WEATHERQUE_WIND|WEATHERQUE_RAIN;
		case Climate.WEATHER_DROUGHT: return WEATHERQUE_RAIN|WEATHERQUE_COLD;
		case Climate.WEATHER_DUSTSTORM: return WEATHERQUE_RAIN|WEATHERQUE_CALM|WEATHERQUE_COLD;
		case Climate.WEATHER_HAIL: return WEATHERQUE_HOT|WEATHERQUE_CALM;
		case Climate.WEATHER_RAIN: return WEATHERQUE_WIND|WEATHERQUE_RAIN;
		case Climate.WEATHER_SLEET: return WEATHERQUE_HOT;
		case Climate.WEATHER_SNOW: return WEATHERQUE_WIND;
		case Climate.WEATHER_WINDY: return WEATHERQUE_RAIN;
		case Climate.WEATHER_WINTER_COLD: return WEATHERQUE_RAIN;
		default: return WEATHERQUE_CALM;
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)
		&&(!mob.isMonster())
		&&(!disregardsArmorCheck(mob))
		&&(mob.isMine(this))
		&&(!renderedMundane)
		&&(CMLib.dice().rollPercentage()<50))
		{
			if(!appropriateToMyFactions(mob))
			{
				mob.tell(L("Extreme emotions disrupt your chant."));
				return false;
			}
			else
			if(!CMLib.utensils().armorCheck(mob,CharClass.ARMOR_LEATHER))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> watch(es) <S-HIS-HER> armor absorb <S-HIS-HER> magical energy!"));
				return false;
			}
		}
		return true;
	}
}
