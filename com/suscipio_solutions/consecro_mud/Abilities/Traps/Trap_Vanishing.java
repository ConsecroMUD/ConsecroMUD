package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_Vanishing extends StdTrap
{
	@Override public String ID() { return "Trap_Vanishing"; }
	private final static String localizedName = CMLib.lang().L("vanishing trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 24;}
	@Override public String requiresToSet(){return "";}

	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if(doesSaveVsTraps(target))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> foil(s) a trap on @x1!",affected.name()));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> notice(s) something about @x1 .. it's fading away.",affected.name())))
			{
				super.spring(target);
				affected.basePhyStats().setDisposition(affected.basePhyStats().disposition()|PhyStats.IS_INVISIBLE);
				affected.recoverPhyStats();
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
