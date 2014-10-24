package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class EternityBarkArmor extends StdArmor
{
	@Override public String ID(){	return "EternityBarkArmor";}
	public EternityBarkArmor()
	{
		super();

		setName("a suit of Eternity Tree Bark Armor");
		setDisplayText("a suit of Eternity tree bark armor sits here.");
		setDescription("This suit of armor is made from the bark of the Fox god\\`s Eternity Tree(armor:  100 and as light as leather armor--wearable by theives)");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		baseGoldValue+=25000;
		basePhyStats().setArmor(100);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(15);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_WOOD;
	}


}
