package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class TrappedLockedDoor extends StdLockedDoorway
{
	@Override public String ID(){	return "TrappedLockedDoor";}
	public TrappedLockedDoor()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_Trap");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
	}
}
