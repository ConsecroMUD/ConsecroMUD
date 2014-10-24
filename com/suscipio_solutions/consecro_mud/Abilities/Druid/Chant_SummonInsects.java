package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Set;
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
public class Chant_SummonInsects extends Chant
{
	@Override public String ID() { return "Chant_SummonInsects"; }
	private final static String localizedName = CMLib.lang().L("Summon Insects");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(In a swarm of insects)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	Room castingLocation=null;
	@Override public long flags(){return Ability.FLAG_SUMMONING;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB M=(MOB)affected;
			if(M.location()!=castingLocation)
				unInvoke();
			else
			if((!M.amDead())&&(M.location()!=null))
			{
				CMLib.combat().postDamage(invoker,M,this,CMLib.dice().roll(1,3+super.getXLEVELLevel(invoker),0),CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,-1,"<T-NAME> <T-IS-ARE> stung by the swarm!");
				if((!M.isInCombat())&&(M!=invoker)&&(M.location()!=null)&&(M.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,M)))
					CMLib.combat().postAttack(M,invoker,M.fetchWieldedItem());
			}
		}
		return super.tick(ticking,tickID);
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
			if((!mob.amDead())&&(mob.location()!=null))
				mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to escape the insect swarm!"));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		 if(mob!=null)
		 {
			 if(!mob.isInCombat())
				 return Ability.QUALITY_INDIFFERENT;
			 final Room R=mob.location();
			 if(R!=null)
			 {
				 if((R.domainType()&Room.INDOORS)>0)
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

		final Set<MOB> h=properTargets(mob,givenTarget,auto);

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(h==null)
			{
				mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?L("A swarm of stinging insects appear, then flutter away!"):L("^S<S-NAME> chant(s) into the sky.  A swarm of stinging insects appear.  Finding no one to sting, they flutter away.^?"));
				return false;
			}
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?L("A swarm of stinging insects appear, then flutter away!"):L("^S<S-NAME> chant(s) into the sky.  A swarm of stinging insects appears and attacks!^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					if((mob.location().okMessage(mob,msg))
					   &&(target.fetchEffect(this.ID())==null))
					{
						mob.location().send(mob,msg);
						if((msg.value()<=0)&&(target.location()==mob.location()))
						{
							castingLocation=mob.location();
							success=maliciousAffect(mob,target,asLevel,((mob.phyStats().level()+(2*super.getXLEVELLevel(mob)))*10),-1)!=null;
							target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) enveloped by the swarm of stinging insects!"));
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> chant(s), but the magic fizzles."));


		// return whether it worked
		return success;
	}
}
