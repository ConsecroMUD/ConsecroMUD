package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_MagneticField extends Chant
{
	@Override public String ID() { return "Chant_MagneticField"; }
	private final static String localizedName = CMLib.lang().L("Magnetic Field");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Magnetic Field chant)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public long flags(){return Ability.FLAG_PARALYZING;}

	public boolean wearingHeldMetal(Environmental affected)
	{
		if(affected instanceof MOB)
		{
			final MOB M=(MOB)affected;
			for(int i=0;i<M.numItems();i++)
			{
				final Item I=M.getItem(i);
				if((I!=null)
				&&(I.container()==null)
				&&(CMLib.flags().isMetal(I))
				&&(!I.amWearingAt(Wearable.IN_INVENTORY))
				&&(!I.amWearingAt(Wearable.WORN_HELD))
				&&(!I.amWearingAt(Wearable.WORN_WIELD)))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((msg.source()==affected)
		&&(wearingHeldMetal(affected))
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&(!(msg.tool() instanceof Ability))
		&&((msg.sourceMinor()==CMMsg.TYP_LEAVE)
		||(msg.sourceMinor()==CMMsg.TYP_ENTER)
		||(msg.sourceMinor()==CMMsg.TYP_ADVANCE)
		||(msg.sourceMinor()==CMMsg.TYP_RETREAT)))
		{
			msg.source().tell(L("Your metal armor is holding you in place!"));
			return false;
		}
		else
		if(((CMath.bset(msg.targetMajor(),CMMsg.MASK_DELICATE)
		   ||CMath.bset(msg.targetMajor(),CMMsg.MASK_HANDS)))
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&(affected instanceof MOB))
		{
			if((msg.target() instanceof Item)
			&&(CMLib.flags().isMetal(msg.target()))
			&&(((MOB)affected).isMine(msg.target())))
			{
				msg.source().tell(L("The magnetic field around @x1 prevents you from doing that.",((Item)msg.target()).name(msg.source())));
				return false;
			}
			if((msg.tool() instanceof Item)
			&&(CMLib.flags().isMetal(msg.tool()))
			&&(((MOB)affected).isMine(msg.tool())))
			{
				msg.source().tell(L("The magnetic field around @x1 prevents you from doing that.",((Item)msg.tool()).name(msg.source())));
				return false;
			}
		}
		return true;
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
			mob.tell(L("The magnetic field fades!"));
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff<0) levelDiff=0;
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		boolean success=proficiencyCheck(mob,-(levelDiff*2),auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) at <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					success=maliciousAffect(mob,target,asLevel,-levelDiff,-1)!=null;
					if(success)
						if(target.location()==mob.location())
							target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) surrounded by a powerful magnetic field!!"));
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but the spell fades."));

		// return whether it worked
		return success;
	}
}
