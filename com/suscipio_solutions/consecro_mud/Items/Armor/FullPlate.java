package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class FullPlate extends StdArmor
{
	@Override public String ID(){	return "FullPlate";}
	public FullPlate()
	{
		super();

		setName("suit of Full Plate");
		setDisplayText("a suit of Full Plate Armor.");
		setDescription("A suit of Full Plate Armor including everything from head to toe.  Fine workmanship make this both very decorative and functional.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS | Wearable.WORN_FEET | Wearable.WORN_HEAD | Wearable.WORN_HANDS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(90);
		basePhyStats().setWeight(90);
		basePhyStats().setAbility(0);
		baseGoldValue=20000;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
