package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Parchment extends GenReadable
{
	@Override public String ID(){	return "Parchment";}
	public Parchment()
	{
		super();
		setName("a piece of parchment");
		setDisplayText("a piece of parchment here.");
		setDescription("looks kinda like a piece of paper");
		basePhyStats().setWeight(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_PAPER);
	}



}
