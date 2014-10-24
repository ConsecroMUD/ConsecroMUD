package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class TrappedChest extends LargeChest
{
	@Override public String ID(){	return "TrappedChest";}
	public TrappedChest()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_Trap");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
		material=RawMaterial.RESOURCE_OAK;
		isLocked=false;
	}


}
