package com.suscipio_solutions.consecro_mud.Abilities.SuperPowers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Power_SuperClimb extends SuperPower
{
	@Override public String ID() { return "Power_SuperClimb"; }
	private final static String localizedName = CMLib.lang().L("Super Climb");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"CLIMB","SUPERCLIMB"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MANA|USAGE_MOVEMENT;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_CLIMBING);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final int dirCode=Directions.getDirectionCode(CMParms.combine(commands,0));
		if(dirCode<0)
		{
			mob.tell(L("Climb where?"));
			return false;
		}
		if((mob.location().getRoomInDir(dirCode)==null)
		||(mob.location().getExitInDir(dirCode)==null))
		{
			mob.tell(L("You can't climb that way."));
			return false;
		}
		if(CMLib.flags().isSitting(mob)||CMLib.flags().isSleeping(mob))
		{
			mob.tell(L("You need to stand up first!"));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT,null);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			success=proficiencyCheck(mob,0,auto);

			if(mob.fetchEffect(ID())==null)
			{
				mob.addEffect(this);
				mob.recoverPhyStats();
			}

			CMLib.tracking().walk(mob,dirCode,false,false);
			mob.delEffect(this);
			mob.recoverPhyStats();
			if(!success)
				mob.location().executeMsg(mob,CMClass.getMsg(mob,mob.location(),CMMsg.MASK_MOVE|CMMsg.TYP_GENERAL,null));
		}
		return success;
	}
}
