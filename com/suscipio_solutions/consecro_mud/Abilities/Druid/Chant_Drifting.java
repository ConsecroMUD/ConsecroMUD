package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Drifting extends Chant
{
	@Override public String ID() { return "Chant_Drifting"; }
	private final static String localizedName = CMLib.lang().L("Drifting");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Drifting)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

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
			if((msg.sourceMinor()==CMMsg.TYP_ADVANCE)
			||(msg.sourceMinor()==CMMsg.TYP_RETREAT))
			{
				mob.tell(L("You can't seem to drift accurately enough to advance or retreat!"));
				return false;
			}
			else
			if((!CMLib.flags().isFalling(mob))
			&&(msg.sourceMinor()==CMMsg.TYP_ENTER)
			&&(msg.target() instanceof Room)
			&&(mob.location()!=null)
			&&((mob.location().getRoomInDir(Directions.UP)==msg.target())
			   ||(mob.location().getRoomInDir(Directions.DOWN)==msg.target())))
			{
				mob.tell(L("You can not seem to direct your flying that way."));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected instanceof MOB)
		&&(tickID==Tickable.TICKID_MOB)
		&&(!CMLib.flags().isFalling(affected))
		&&(((MOB)affected).riding()==null)
		&&(((MOB)affected).location()!=null)
		&&((((MOB)affected).location().domainType()&Room.INDOORS)==0))
		{
			final Ability A=CMClass.getAbility("Falling");
			A.setAffectedOne(null);
			A.setProficiency(100);
			A.invoke(null,null,affected,true,0);
			affected.recoverPhyStats();
		}
		return true;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((affected.fetchEffect("Falling")==null)&&(!CMLib.flags().isFalling(affected)))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			final Ability A=mob.fetchEffect("Falling");
			if(A!=null) A.unInvoke();
			mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> float(s) back down."));
			CMLib.commands().postStand(mob,true);
		}
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

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) <S-HIM-HERSELF> off the ground.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					success=beneficialAffect(mob,target,asLevel,0)!=null;
					target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> start(s) drifting up!"));
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s), but fail(s) to leave the ground."));
		// return whether it worked
		return success;
	}
}
