package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Torch extends LightSource
{
	@Override public String ID(){	return "Torch";}
	public Torch()
	{
		super();
		setName("a torch");
		setDisplayText("a small straw torch sits here.");
		setDescription("It looks like it is lightly covered in oil near the end.");
		durationTicks=200;

		material=RawMaterial.RESOURCE_OAK;
		this.destroyedWhenBurnedOut=true;
		this.goesOutInTheRain=true;
		baseGoldValue=1;
	}



}
