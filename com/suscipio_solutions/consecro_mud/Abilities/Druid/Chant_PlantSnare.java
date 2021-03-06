package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_PlantSnare extends Chant
{
	@Override public String ID() { return "Chant_PlantSnare"; }
	private final static String localizedName = CMLib.lang().L("Plant Snare");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Snared)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
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
				if(mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> struggle(s) against the snaring plants.")))
				{
					amountRemaining-=(mob.charStats().getStat(CharStats.STAT_STRENGTH)*4);
					if(amountRemaining<0)
						unInvoke();
				}
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
				mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to break <S-HIS-HER> way free of the plants."));
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Set<MOB> h=properTargets(mob,target,false);
			if(h==null)
				return Ability.QUALITY_INDIFFERENT;
			final Room room=mob.location();
			if(room!=null)
			{
				if((room.domainType()!=Room.DOMAIN_OUTDOORS_WOODS)
				&&(room.domainType()!=Room.DOMAIN_OUTDOORS_PLAINS)
				&&((room.myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
				&&((room.myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION)
				&&(room.domainType()!=Room.DOMAIN_OUTDOORS_HILLS)
				&&(room.domainType()!=Room.DOMAIN_OUTDOORS_JUNGLE)
				&&(room.domainType()!=Room.DOMAIN_OUTDOORS_SWAMP))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth snaring."));
			return false;
		}
		final Room room=mob.location();
		if((room.domainType()!=Room.DOMAIN_OUTDOORS_WOODS)
		&&(room.domainType()!=Room.DOMAIN_OUTDOORS_PLAINS)
		&&((room.myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
		&&((room.myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION)
		&&(room.domainType()!=Room.DOMAIN_OUTDOORS_HILLS)
		&&(room.domainType()!=Room.DOMAIN_OUTDOORS_JUNGLE)
		&&(room.domainType()!=Room.DOMAIN_OUTDOORS_SWAMP))
		{
			mob.tell(L("There doesn't seem to be a large enough mass of plant life around here...\n\r"));
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
			if(room.show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the plants around <S-HIM-HER>.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					final Room troom = CMLib.map().roomLocation(target);

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					if((troom!=null)&&(troom.okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
					{
						troom.send(mob,msg);
						if(msg.value()<=0)
						{
							amountRemaining=400+(100*super.getXLEVELLevel(mob));
							if(troom==room)
							{
								success=maliciousAffect(mob,target,asLevel,(adjustedLevel(mob,asLevel)*10),-1)!=null;
								troom.show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) stuck as tangling mass of plant life grows onto <S-HIM-HER>!"));
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
