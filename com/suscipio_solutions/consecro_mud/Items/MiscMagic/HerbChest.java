package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMClass;


public class HerbChest extends BagOfHolding {
	@Override public String ID(){	return "HerbChest";}
	public HerbChest()
	{
		super();
		setName("a small chest");
		setDisplayText("a small chest with many tiny drawers stands here.");
		setDescription("The most common magical item in the world, this carefully crafted chest is designed to help alchemists of the world carry their herbal supplies with them everywhere.");
		secretIdentity="An Alchemist's Herb Chest";
		setContainTypes(RawMaterial.RESOURCE_HERBS);
		capacity=500;
		baseGoldValue=0;
		material=RawMaterial.RESOURCE_REDWOOD;
		final Ability A=CMClass.getAbility("Prop_HaveZapper");
		if(A!=null)
		{
			A.setMiscText("+SYSOP -MOB -anyclass +alchemist");
			addNonUninvokableEffect(A);
		}
	}
}
