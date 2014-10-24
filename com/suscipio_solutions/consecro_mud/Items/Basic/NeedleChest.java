package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class NeedleChest extends LargeChest
{
	@Override public String ID(){	return "NeedleChest";}
	public NeedleChest()
	{
		super();
		final Trap t=(Trap)CMClass.getAbility("Trap_OpenNeedle");
		if(t!=null) CMLib.utensils().setTrapped(this,t,true);
		isLocked=false;
		setMaterial(RawMaterial.RESOURCE_OAK);
	}


}
