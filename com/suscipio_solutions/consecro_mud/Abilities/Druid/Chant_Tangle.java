package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Tangle extends Chant
{
	@Override public String ID() { return "Chant_Tangle"; }
	private final static String localizedName = CMLib.lang().L("Tangle");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tangled)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}
	public Item thePlants=null;
	public int amountRemaining=0;
	@Override public long flags(){return Ability.FLAG_BINDING;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_BOUND);
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		if((thePlants==null)||(thePlants.owner()==null)||(!(thePlants.owner() instanceof Room)))
		{
			unInvoke();
			return super.okMessage(myHost,msg);
		}

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amISource(mob))
		{
			if((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
			&&((msg.sourceMajor(CMMsg.MASK_HANDS))
			||(msg.sourceMajor(CMMsg.MASK_MOVE))))
			{
				mob.location().show(mob,null,thePlants,CMMsg.MSG_OK_ACTION,L("<S-NAME> struggle(s) against <O-NAME>."));
				amountRemaining-=(mob.charStats().getStat(CharStats.STAT_STRENGTH)*4);
				if(amountRemaining<0)
					unInvoke();
				else
					return false;
			}
		}
		return super.okMessage(myHost,msg);
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
			if(!mob.amDead())
				mob.location().show(mob,null,thePlants,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to break <S-HIS-HER> way free of <O-NAME>."));
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Item thePlants=Druid_MyPlants.myPlant(mob.location(),mob,0);
			if(thePlants==null)
				 return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		thePlants=Druid_MyPlants.myPlant(mob.location(),mob,0);
		if(thePlants==null)
		{
			mob.tell(L("There doesn't appear to be any plants here you can control!"));
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
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> begin(s) to chant.^?")))
			{
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
						amountRemaining=200;
						if(target.location()==mob.location())
						{
							success=maliciousAffect(mob,target,asLevel,(adjustedLevel(mob,asLevel)*10),-1)!=null;
							target.location().show(target,null,thePlants,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) stuck in <O-NAME> as they grow and twist around <S-HIM-HER>!"));
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> chant(s), but the magic fades."));


		// return whether it worked
		return success;
	}
}
