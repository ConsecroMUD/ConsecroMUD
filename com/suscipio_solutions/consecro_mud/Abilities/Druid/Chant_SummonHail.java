package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_SummonHail extends Chant
{
	@Override public String ID() { return "Chant_SummonHail"; }
	@Override public String name(){ return renderedMundane?"hail":"Summon Hail";}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(10);}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_WEATHER_MASTERY;}
	@Override public long flags(){return Ability.FLAG_WEATHERAFFECTING;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		 if(mob!=null)
		 {
			 final Room R=mob.location();
			 if(R!=null)
			 {
				 if((R.domainType()&Room.INDOORS)>0)
					 return Ability.QUALITY_INDIFFERENT;
				 final Area A=R.getArea();
				 if((A.getClimateObj().weatherType(mob.location())!=Climate.WEATHER_WINTER_COLD)
				 &&(A.getClimateObj().weatherType(mob.location())!=Climate.WEATHER_HAIL))
					 return Ability.QUALITY_INDIFFERENT;
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
		if((!auto)
		&&((mob.location().getArea().getClimateObj().weatherType(mob.location())!=Climate.WEATHER_WINTER_COLD)
			&&(mob.location().getArea().getClimateObj().weatherType(mob.location())!=Climate.WEATHER_HAIL)))
		{
			mob.tell(L("This chant requires a cold snap or a hail storm!"));
			return false;
		}
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"^JHailstones falling from the sky whack <T-NAME>.^?":"^S<S-NAME> chant(s) to <T-NAMESELF>.  Suddenly a volley of hailstones assaults <T-HIM-HER>!^?")+CMLib.protocol().msp("hail.wav",40));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,verbalCastMask(mob,target,auto)|CMMsg.TYP_WATER,null);
			if((mob.location().okMessage(mob,msg))&&((mob.location().okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				final int maxDie =  (adjustedLevel( mob, asLevel )+(2*super.getX1Level(mob))) / 2;
				int damage = CMLib.dice().roll(maxDie,4,0);
				if((msg.value()>0)||(msg2.value()>0))
					damage = (int)Math.round(CMath.div(damage,2.0));
				if(target.location()==mob.location())
				{
					Item I=null;
					for(int i=0;i<target.numItems();i++)
					{
						I=target.getItem(i);
						if((I.container()==null)
						&&(I.amWearingAt(Wearable.WORN_HEAD))
						&&(((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_METAL)
							||((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_METAL)
							||((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_METAL)))
							break;
						I=null;
					}
					if((I!=null)&&(I.amWearingAt(Wearable.WORN_HEAD)))
						target.location().show(target,I,null,CMMsg.MSG_OK_ACTION,L("Hailstones bounce harmlessly off <T-NAME> being worn by <S-NAME>."));
					else
						CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_WATER,Weapon.TYPE_BASHING,"The hailstones <DAMAGE> <T-NAME>!");
				}
				final Climate C=mob.location().getArea().getClimateObj();
				final Climate oldC=(Climate)C.copyOf();
				if(C.weatherType(mob.location())!=Climate.WEATHER_HAIL)
				{
					C.setNextWeatherType(Climate.WEATHER_HAIL);
					C.forceWeatherTick(mob.location().getArea());
					Chant_CalmWeather.xpWorthyChange(mob,mob.location().getArea(),oldC,C);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) at <T-NAMESELF>, but the magic fades."));


		// return whether it worked
		return success;
	}
}
