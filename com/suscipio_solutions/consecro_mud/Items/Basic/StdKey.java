package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DoorKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class StdKey extends StdItem implements DoorKey
{
	@Override public String ID(){	return "StdKey";}
	public StdKey()
	{
		super();
		setName("a metal key");
		setDisplayText("a small metal key sits here.");
		setDescription("You can't tell what it\\`s to by looking at it.");

		material=RawMaterial.RESOURCE_STEEL;
		baseGoldValue=0;
		recoverPhyStats();
	}


	@Override public void setKey(String keyName){miscText=keyName;}
	@Override public String getKey(){return miscText;}
}
