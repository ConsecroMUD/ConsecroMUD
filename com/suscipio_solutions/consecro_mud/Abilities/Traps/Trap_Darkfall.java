package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Trap_Darkfall extends StdTrap
{
	@Override public String ID() { return "Trap_Darkfall"; }
	private final static String localizedName = CMLib.lang().L("darkfall");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 2;}
	@Override public String requiresToSet(){return "";}

	@Override public int baseRejuvTime(int level){return 20;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(sprung)
		{
			if(!disabled)
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_DARK);
		}
		else
			disabled=false;
	}

	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((doesSaveVsTraps(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off a trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> set(s) off a darkness trap!")))
			{
				super.spring(target);
				target.location().recoverRoomStats();
			}
		}
	}
}
