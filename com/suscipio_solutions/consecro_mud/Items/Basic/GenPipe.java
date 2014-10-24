package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class GenPipe extends GenCigar
{
	@Override public String ID(){	return "GenPipe";}
	public GenPipe()
	{
		super();
		setName("a generic pipe");
		basePhyStats.setWeight(1);
		setDisplayText("a generic pipe sits here.");
		setDescription("This nice wooden pipe could use some herbs in it to smoke.");
		setMaterial(RawMaterial.RESOURCE_OAK);
		durationTicks=1200;
		destroyedWhenBurnedOut=false;
		baseGoldValue=5;
		capacity=2;
		recoverPhyStats();
	}

}
