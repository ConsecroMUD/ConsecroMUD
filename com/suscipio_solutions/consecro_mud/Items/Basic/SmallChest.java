package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;




public class SmallChest extends LockableContainer
{
	@Override public String ID(){	return "SmallChest";}
	public SmallChest()
	{
		super();
		setName("a small chest");
		setDisplayText("a small wooden chest sits here.");
		setDescription("It\\`s of solid wood construction with metal bracings.  The lid has a key hole.");
		capacity=50;
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=15;
		basePhyStats().setWeight(25);
		recoverPhyStats();
	}



}
