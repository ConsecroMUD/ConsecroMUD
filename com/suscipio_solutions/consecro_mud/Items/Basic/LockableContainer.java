package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class LockableContainer extends StdContainer
{
	@Override public String ID(){	return "LockableContainer";}
	public LockableContainer()
	{
		super();
		hasALid=true;
		isOpen=false;
		hasALock=true;
		isLocked=true;
		setMaterial(RawMaterial.RESOURCE_OAK);
	}


}
