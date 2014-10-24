package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Spell_PredictWeather extends Spell
{
	@Override public String ID() { return "Spell_PredictWeather"; }
	private final static String localizedName = CMLib.lang().L("Forecast Weather");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Forecast Weather)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}

	String lastPrediction="";

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			lastPrediction="";
		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("Your senses are no longer sensitive to the weather."));
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_MOB)
		   &&(affected!=null)
		   &&(affected instanceof MOB)
		   &&(((MOB)affected).location()!=null)
		   &&((((MOB)affected).location().domainType()&Room.INDOORS)==0))
		{
		   final String prediction=(((MOB)affected).location().getArea().getClimateObj().nextWeatherDescription(((MOB)affected).location()));
		   if(!prediction.equals(lastPrediction))
		   {
			   lastPrediction=prediction;
			   ((MOB)affected).tell(L("Your weather senses gaze into the future, you see: \n\r@x1",prediction));
		   }
		}
		return true;
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already detecting weather."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> gain(s) sensitivity to the weather!"):L("^S<S-NAME> invoke(s) weather sensitivity!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				lastPrediction="";
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> incant(s) into the sky, but the spell fizzles."));

		return success;
	}
}
