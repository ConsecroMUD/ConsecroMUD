package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Ruler extends Quarterstaff
{
	@Override public String ID(){	return "Ruler";}
	public Ruler()
	{
		super();

		setName("a ruler");
		setDisplayText("a ruler has been left here.");
		setDescription("It`s long and wooden, with little tick marks on it.");
		material=RawMaterial.RESOURCE_OAK;
	}


}
