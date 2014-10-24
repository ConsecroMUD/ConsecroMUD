package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_Repulsion extends Spell
{
	@Override public String ID() { return "Spell_Repulsion"; }
	private final static String localizedName = CMLib.lang().L("Repulsion");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Repulsion)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(3);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

	public int amountRemaining=0;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amISource(mob))
		{
			if(msg.sourceMinor()==CMMsg.TYP_ADVANCE)
			{
				if(mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> struggle(s) against the repulsion field.")))
				{
					amountRemaining-=mob.charStats().getStat(CharStats.STAT_STRENGTH);
					if(amountRemaining<0)
						unInvoke();
				}
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID == Tickable.TICKID_MOB)
		{
			final Room R=CMLib.map().roomLocation(affected);
			if((R!=null)
			&&(invoker!=null)
			&&((!R.isInhabitant(invoker))||(!invoker.isInCombat())))
			{
				unInvoke();
				return false;
			}
		}
		return super.tick(ticking, tickID);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to break <S-HIS-HER> way free of the repulsion field."));
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if((h==null)||(h.size()==0))
		{
			mob.tell(L("There doesn't appear to be anyone here worth repelling."));
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
			if(mob.location().show(mob,null,this,somanticCastCode(mob,null,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> arms and cast(s) a spell.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
					{
						mob.location().send(mob,msg);
						if(msg.value()<=0)
						{
							amountRemaining=130;
							if(target.location()==mob.location())
							{
								success=maliciousAffect(mob,target,asLevel,((mob.phyStats().level()+(2*getXLEVELLevel(mob)))*10),-1)!=null;
								int level=2;
								if((CMLib.ableMapper().qualifyingClassLevel(mob,this)>0)&&((adjustedLevel(mob,asLevel)-CMLib.ableMapper().qualifyingClassLevel(mob,this))>10))
									level+=((adjustedLevel(mob,asLevel)-CMLib.ableMapper().qualifyingClassLevel(mob,this))-10)/10;
								if(level<2) level=2;
								target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) repelled!"));
								if((target.getVictim()!=null)&&(target.rangeToTarget()>0))
									target.setAtRange(target.rangeToTarget());
								else
								if(target.location().maxRange()<level)
									target.setAtRange(target.location().maxRange());
								else
									target.setAtRange(level);
								if(target.getVictim()!=null)
									target.getVictim().setAtRange(target.rangeToTarget());
								if(mob.getVictim()==null) mob.setVictim(null); // correct range
								if(target.getVictim()==null) target.setVictim(null); // correct range
							}
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> wave(s) <S-HIS-HER> arms, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
