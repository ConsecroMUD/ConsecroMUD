package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;




public class DruidicMonument extends StdItem implements MiscMagic
{
	@Override public String ID(){	return "DruidicMonument";}
	public DruidicMonument()
	{
		super();

		setName("the druidic stones");
		setDisplayText("druidic stones are arrayed here.");
		setDescription("These large mysterious monuments have a power and purpose only the druid understands.");
		secretIdentity="DRUIDIC STONES";
		basePhyStats().setLevel(1);
		setMaterial(RawMaterial.RESOURCE_STONE);
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
		basePhyStats().setWeight(1000);
		baseGoldValue=0;
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
	}


}
