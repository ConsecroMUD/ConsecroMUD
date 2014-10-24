package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Impassable extends GenExit
{
	public Impassable()
	{
		super();
		name="a blocked way";
		description="It doesn't look like you can go that way.";
	}
	@Override public String ID(){	return "Impassable";}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		final MOB mob=msg.source();
		if((!msg.amITarget(this))&&(msg.tool()!=this))
			return true;
		else
		if(msg.targetMajor(CMMsg.MASK_MOVE))
		{
			mob.tell(L("You can't go that way."));
			return false;
		}
		return true;
	}
}
