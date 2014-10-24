package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class LargeSack extends StdContainer
{
	@Override public String ID(){	return "LargeSack";}
	public LargeSack()
	{
		super();
		setName("a large sack");
		setDisplayText("a large sack is crumpled up here.");
		setDescription("A nice big berlap sack to put your things in.");
		capacity=100;
		baseGoldValue=5;
		setMaterial(RawMaterial.RESOURCE_COTTON);
		recoverPhyStats();
	}



}
