package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class PitOpen extends StdOpenDoorway
{
	@Override public String ID(){	return "PitOpen";}
	@Override public String Name(){return "a pit";}
	public PitOpen()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_EnterPit");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
	}
}
