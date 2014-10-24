package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.Enumeration;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


@SuppressWarnings("rawtypes")
public class Trap_Popper extends StdTrap
{
	@Override public String ID() { return "Trap_Popper"; }
	private final static String localizedName = CMLib.lang().L("popping noise");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 1;}
	@Override public String requiresToSet(){return "";}

	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((doesSaveVsTraps(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off a noise trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> set(s) off a **POP** trap!")))
			{
				super.spring(target);
				final Area A=target.location().getArea();
				for(final Enumeration e=A.getMetroMap();e.hasMoreElements();)
				{
					final Room R=(Room)e.nextElement();
					if(R!=target.location())
						R.showHappens(CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("You hear a loud **POP** coming from somewhere."));
				}
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
