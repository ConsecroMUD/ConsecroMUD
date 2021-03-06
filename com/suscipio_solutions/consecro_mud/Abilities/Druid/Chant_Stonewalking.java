package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Stonewalking extends Chant
{
	@Override public String ID() { return "Chant_Stonewalking"; }
	private final static String localizedName = CMLib.lang().L("Stonewalking");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Stonewalking spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ROCKCONTROL;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if(msg.amISource(mob)
		&&((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
		   ||((msg.sourceMinor()==CMMsg.TYP_GET)&&((msg.target()==null)||(!mob.isMine(msg.target()))))))
			unInvoke();
		return;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		if((affected instanceof MOB)&&(((MOB)affected).location()!=null))
		{
			final Room R=((MOB)affected).location();
			if((R.domainType()==Room.DOMAIN_INDOORS_CAVE)
			   ||(R.domainType()==Room.DOMAIN_INDOORS_STONE)
			   ||(R.domainType()==Room.DOMAIN_OUTDOORS_MOUNTAINS)
			   ||(R.domainType()==Room.DOMAIN_OUTDOORS_ROCKS))
			{
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
				affectableStats.setWeight(0);
				affectableStats.setHeight(-1);
			}
		}
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
			if((mob.location()!=null)&&(!mob.amDead()))
			{
				final Room R=mob.location();
				if((R.domainType()==Room.DOMAIN_INDOORS_CAVE)||(R.domainType()==Room.DOMAIN_INDOORS_STONE))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> drawn out of the walls."));
				else
				if((R.domainType()==Room.DOMAIN_OUTDOORS_MOUNTAINS)||(R.domainType()==Room.DOMAIN_OUTDOORS_ROCKS))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> drawn out of the rocks."));
				else
					mob.tell(L("Your stone walk has ended."));
			}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if((R.domainType()!=Room.DOMAIN_INDOORS_CAVE)
				&&(R.domainType()!=Room.DOMAIN_INDOORS_STONE)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_ROCKS))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already @x1.",name()));
			return false;
		}

		final Room R=mob.location();
		if((R.domainType()!=Room.DOMAIN_INDOORS_CAVE)
		   &&(R.domainType()!=Room.DOMAIN_INDOORS_STONE)
		   &&(R.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
		   &&(R.domainType()!=Room.DOMAIN_OUTDOORS_ROCKS))
		{
			mob.tell(L("You must be near walls of stone or massive rock to use this chant."));
			return false;
		}
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) quietly to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> fade(s) into the walls!"));
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) quietly to <T-NAMESELF>, but nothing more happens."));

		// return whether it worked
		return success;
	}
}
