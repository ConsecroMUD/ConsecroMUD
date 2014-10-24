package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;


public class Ring extends StdItem
{
	@Override public String ID(){	return "Ring";}
	public Ring()
	{
		super();
		setName("an ordinary ring");
		setDisplayText("a nondescript ring sits here doing nothing.");
		setDescription("It looks like a ring you wear on your fingers.");

		properWornBitmap=Wearable.WORN_LEFT_FINGER | Wearable.WORN_RIGHT_FINGER;
		wornLogicalAnd=false;
		baseGoldValue=50;
		recoverPhyStats();
	}

}
