package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;


@SuppressWarnings("rawtypes")
public class Starboard extends Go
{
	public Starboard(){}

	private final String[] access=I(new String[]{"STARBOARD","STB"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		int direction=Directions.EAST;
		if((commands!=null)&&(commands.size()>1))
		{
			final int nextDir=Directions.getDirectionCode((String)commands.get(2));
			if(nextDir == Directions.NORTH)
				direction=Directions.NORTHEAST;
			else
			if(nextDir == Directions.SOUTH)
				direction=Directions.SOUTHEAST;
		}
		standIfNecessary(mob,metaFlags);
		if((CMLib.flags().isSitting(mob))||(CMLib.flags().isSleeping(mob)))
		{
			mob.tell(L("You need to stand up first."));
			return false;
		}
		if(mob.isAttribute(MOB.Attrib.AUTORUN))
			CMLib.tracking().run(mob, direction, false,false,false);
		else
			CMLib.tracking().walk(mob, direction, false,false,false);
		return false;
	}
	@Override public boolean canBeOrdered(){return true;}

	@Override
	public boolean securityCheck(MOB mob)
	{
		return (mob==null) || (mob.isMonster()) || (mob.location()==null)
			|| (mob.location() instanceof SpaceShip) || (mob.location().getArea() instanceof SpaceShip);
	}
}
