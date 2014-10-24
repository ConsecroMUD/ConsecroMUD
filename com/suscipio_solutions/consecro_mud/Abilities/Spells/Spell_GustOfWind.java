package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_GustOfWind extends Spell
{
	@Override public String ID() { return "Spell_GustOfWind"; }
	private final static String localizedName = CMLib.lang().L("Gust of Wind");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Blown Down)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(4);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	public boolean doneTicking=false;
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
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
				mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> regain(s) <S-HIS-HER> feet."));
				CMLib.commands().postStand(mob,true);
			}
			else
				mob.tell(L("You regain your feet."));
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Room R=CMLib.map().roomLocation(givenTarget);
		if(R==null) R=mob.location();
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
			if(R.show(mob,null,this,verbalCastCode(mob,null,auto),auto?L("A horrendous wind gust blows through here."):L("^S<S-NAME> blow(s) at <S-HIS-HER> enemies.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("<T-NAME> get(s) blown back!"));
					if((R.okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
					{
						if((msg.value()<=0)&&(target.location()==R))
						{
							MOB victim=target.getVictim();
							if((victim!=null)&&(target.rangeToTarget()>=0))
								target.setAtRange(target.rangeToTarget()+1+(adjustedLevel(mob,asLevel)/10));
							if(target.rangeToTarget()>target.location().maxRange())
								target.setAtRange(target.location().maxRange());

							R.send(mob,msg);
							if((!CMLib.flags().isInFlight(target))
							&&(CMLib.dice().rollPercentage()>((target.charStats().getStat(CharStats.STAT_DEXTERITY)*2)+target.phyStats().level()-(adjustedLevel(mob,asLevel)/2)))
							&&(target.charStats().getBodyPart(Race.BODY_LEG)>0))
							{
								R.show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> fall(s) down!"));
								doneTicking=false;
								success=maliciousAffect(mob,target,asLevel,2,-1)!=null;
							}
							victim=target.getVictim();
							if(victim!=null)
								victim.setAtRange(target.rangeToTarget());
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> blow(s), but find(s) <S-HE-SHE> is only full of hot air."));


		// return whether it worked
		return success;
	}
}
