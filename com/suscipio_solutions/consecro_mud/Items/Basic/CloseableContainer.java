package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class CloseableContainer extends StdContainer
{
	@Override public String ID(){	return "CloseableContainer";}
	public CloseableContainer()
	{
		super();

		hasALid=true;
		isOpen=false;
		material=RawMaterial.RESOURCE_OAK;
	}


}
