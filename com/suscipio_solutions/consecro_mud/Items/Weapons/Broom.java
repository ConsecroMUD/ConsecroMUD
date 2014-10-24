package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Broom extends Quarterstaff
{
	@Override public String ID(){	return "Broom";}
	public Broom()
	{
		super();

		setName("a broom");
		setDisplayText("a broom lies in the corner of the room.");
		setDescription("It`s long and wooden, with lots of bristles on one end.");
		material=RawMaterial.RESOURCE_OAK;
	}


}
