package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_SacredEarth extends Chant
{
	@Override public String ID() { return "Chant_SacredEarth"; }
	private final static String localizedName = CMLib.lang().L("Sacred Earth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Sacred Earth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof Room)))
			return;
		final Room R=(Room)affected;
		if(canBeUninvoked())
			R.showHappens(CMMsg.MSG_OK_VISUAL,L("The sacred earth charm is ended."));

		super.unInvoke();

	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.tool() instanceof Ability)
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_GATHERINGSKILL))
		{
			msg.source().tell(L("The sacred earth will not allow you to violate it."));
			return false;
		}
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.target()!=null)
		&&(msg.target() instanceof MOB)
		&&((((MOB)msg.target()).charStats().getMyRace().racialCategory().equals("Vegetation"))
		||(((MOB)msg.target()).charStats().getMyRace().racialCategory().equals("Earth Elemental"))))
		{
			final int recovery=(int)Math.round(CMath.div((msg.value()),2.0));
			msg.setValue(msg.value()-recovery);
		}
		return true;
	}

   @Override
public int castingQuality(MOB mob, Physical target)
   {
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if(((R.domainType()&Room.INDOORS)>0)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_AIR))
					return Ability.QUALITY_INDIFFERENT;
			}

			if(mob.isInCombat())
			{
				final MOB victim=mob.getVictim();
				if(victim!=null)
				{
					if(((victim.charStats().getMyRace().racialCategory().equals("Vegetation"))
					||(victim.charStats().getMyRace().racialCategory().equals("Earth Elemental"))))
						return Ability.QUALITY_INDIFFERENT;
				}
				if(((!mob.charStats().getMyRace().racialCategory().equals("Vegetation"))
				&&(!mob.charStats().getMyRace().racialCategory().equals("Earth Elemental"))))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This earth is already sacred."));
			return false;
		}
		if((((mob.location().domainType()&Room.INDOORS)>0)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR))
		&&(!auto))
		{
			mob.tell(L("This chant will not work here."));
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
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to the ground.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The charm of the sacred earth begins here!"));
					beneficialAffect(mob,target,asLevel,0);
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					{
						final Room R=mob.location().getRoomInDir(d);
						if((R!=null)
						&&(R.fetchEffect(ID())==null)
						&&((R.domainType()&Room.INDOORS)==0)
						&&(R.domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
						&&(R.domainType()!=Room.DOMAIN_OUTDOORS_WATERSURFACE)
						&&(R.domainType()!=Room.DOMAIN_OUTDOORS_AIR))
							beneficialAffect(mob,R,asLevel,0);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) to the ground, but the magic fades."));
		// return whether it worked
		return success;
	}
}
