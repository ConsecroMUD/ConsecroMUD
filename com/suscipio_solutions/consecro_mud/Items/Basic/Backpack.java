package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Backpack extends CloseableContainer
{
	@Override public String ID(){	return "Backpack";}
	public Backpack()
	{
		super();
		setName("a backpack");
		setDisplayText("a backpack sits here.");
		setDescription("The straps are a little worn, but it\\`s in nice shape!");
		capacity=25;
		baseGoldValue=5;
		properWornBitmap=Wearable.WORN_BACK|Wearable.WORN_HELD;
		material=RawMaterial.RESOURCE_LEATHER;
		recoverPhyStats();
	}



}
