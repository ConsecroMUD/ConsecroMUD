package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;



public class HorseCart extends StdRideable
{
	@Override public String ID(){	return "HorseCart";}
	public HorseCart()
	{
		super();
		setName("a horse cart");
		setDisplayText("a large horse cart is here");
		setDescription("Looks like quite a bit can ride in there!");
		capacity=1000;
		baseGoldValue=500;
		basePhyStats().setWeight(500);
		setMaterial(RawMaterial.RESOURCE_OAK);
		setRideBasis(Rideable.RIDEABLE_WAGON);
		setRiderCapacity(10);
		recoverPhyStats();
	}



}
