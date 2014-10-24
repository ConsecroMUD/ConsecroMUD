package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;




public class SmallSack extends StdContainer
{
	@Override public String ID(){	return "SmallSack";}
	public SmallSack()
	{
		super();
		setName("a small sack");
		setDisplayText("a small sack is crumpled up here.");
		setDescription("A nice berlap sack to put your things in.");
		capacity=25;
		material=RawMaterial.RESOURCE_COTTON;
		baseGoldValue=1;
		recoverPhyStats();
	}



}
