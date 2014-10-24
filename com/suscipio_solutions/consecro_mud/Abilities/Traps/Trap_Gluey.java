package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_Gluey extends StdTrap
{
	@Override public String ID() { return "Trap_Gluey"; }
	private final static String localizedName = CMLib.lang().L("gluey");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 11;}
	@Override public String requiresToSet(){return "";}

	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((doesSaveVsTraps(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> clean(s) off @x1!",affected.name()));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> notice(s) something about @x1 .. it's kinda sticky.",affected.name())))
			{
				super.spring(target);
				if(affected instanceof Item)
				{
					CMLib.flags().setRemovable(((Item)affected),false);
					CMLib.flags().setDroppable(((Item)affected),false);
				}
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
