package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class NeedleDoor extends StdClosedDoorway
{
	@Override public String ID(){	return "NeedleDoor";}
	public NeedleDoor()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_OpenNeedle");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
	}
}
