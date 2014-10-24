package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class TrappedDoor extends StdClosedDoorway
{
	@Override public String ID(){	return "TrappedDoor";}
	public TrappedDoor()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_Open");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
	}
}
