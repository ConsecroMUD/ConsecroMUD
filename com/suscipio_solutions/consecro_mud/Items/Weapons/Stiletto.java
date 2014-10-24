package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Stiletto extends Dagger
{
	@Override public String ID(){	return "Stiletto";}
	public Stiletto()
	{
		super();

		setName("a cool stiletto");
		setDisplayText("a stiletto is in the corner.");
		setDescription("A dagger, more or less, with a long slender blade and sharp point.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(1);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(3);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_PIERCING;
	}



}
