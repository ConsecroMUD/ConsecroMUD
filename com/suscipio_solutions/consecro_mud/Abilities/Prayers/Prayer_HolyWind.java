package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_HolyWind extends Prayer
{
	@Override public String ID() { return "Prayer_HolyWind"; }
	private final static String localizedName = CMLib.lang().L("Holy Wind");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Blown Down)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CREATION;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(4);}
	public boolean doneTicking=false;
	@Override public long flags(){return Ability.FLAG_MOVING;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!doneTicking)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SITTING);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((doneTicking)&&(msg.amISource(mob)))
			unInvoke();
		else
		if(msg.amISource(mob)&&(msg.sourceMinor()==CMMsg.TYP_STAND))
			return false;
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			doneTicking=true;
		super.unInvoke();
		if(canBeUninvoked())
		{
			if((mob.location()!=null)&&(!mob.amDead()))
			{
				final CMMsg msg=CMClass.getMsg(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> regain(s) <S-HIS-HER> feet."));
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					CMLib.commands().postStand(mob,true);
				}
			}
			else
				mob.tell(L("You regain your feet."));
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if((h==null)||(h.size()==0))
		{
			mob.tell(L("There doesn't appear to be anyone here worth blowing around."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),L(auto?"A horrendous wind gust blows through here.":"^S<S-NAME> "+prayWord(mob)+" for the holy wind to blow through here.^?")+CMLib.protocol().msp("wind.wav",40)))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("<T-NAME> get(s) blown back!"));
					if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
					{
						if((msg.value()<=0)&&(target.location()==mob.location()))
						{
							int howLong=2;
							if((mob.location().getArea().getClimateObj().weatherType(mob.location())==Climate.WEATHER_WINDY)
							||(mob.location().getArea().getClimateObj().weatherType(mob.location())==Climate.WEATHER_DUSTSTORM)
							||(mob.location().getArea().getClimateObj().weatherType(mob.location())==Climate.WEATHER_THUNDERSTORM))
								howLong=4;

							final MOB victim=target.getVictim();
							if((victim!=null)&&(target.rangeToTarget()>=0))
								target.setAtRange(target.rangeToTarget()+(howLong/2));
							if(target.rangeToTarget()>target.location().maxRange())
								target.setAtRange(target.location().maxRange());
							mob.location().send(mob,msg);
							if((!CMLib.flags().isInFlight(target))
							&&(CMLib.dice().rollPercentage()>(((target.charStats().getStat(CharStats.STAT_DEXTERITY)*2)+target.phyStats().level()))-(5*howLong))
							&&(target.charStats().getBodyPart(Race.BODY_LEG)>0))
							{
								mob.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> fall(s) down!"));
								doneTicking=false;
								success=maliciousAffect(mob,target,asLevel,howLong,-1)!=null;
							}
							if(target.getVictim()!=null)
								target.getVictim().setAtRange(target.rangeToTarget());
							if(mob.getVictim()==null) mob.setVictim(null); // correct range
							if(target.getVictim()==null) target.setVictim(null); // correct range
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> @x1, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
