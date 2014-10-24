package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;


@SuppressWarnings("rawtypes")
public class North extends Go
{
	public North(){}

	private final String[] access=I(new String[]{"NORTH","N"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		standIfNecessary(mob,metaFlags);
		if((CMLib.flags().isSitting(mob))||(CMLib.flags().isSleeping(mob)))
		{
			mob.tell(L("You need to stand up first."));
			return false;
		}
		if(mob.isAttribute(MOB.Attrib.AUTORUN))
			CMLib.tracking().run(mob, Directions.NORTH, false,false,false);
		else
			CMLib.tracking().walk(mob, Directions.NORTH, false,false,false);
		return false;
	}
	@Override
	public boolean canBeOrdered()
	{
		return true;
	}

	@Override
	public boolean securityCheck(MOB mob)
	{
		return (mob==null) || (mob.isMonster()) || (mob.location()==null)
				|| ((!(mob.location() instanceof SpaceShip)) && (!(mob.location().getArea() instanceof SpaceShip)));
	}
}
