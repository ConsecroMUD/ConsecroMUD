package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Chant_FindGem extends Chant_FindPlant
{
	@Override public String ID() { return "Chant_FindGem"; }
	private final static String localizedName = CMLib.lang().L("Find Gem");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Finding "+lookingFor+")"); }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ROCKCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public long flags(){return Ability.FLAG_TRACKING;}

	private final int[] myMats={RawMaterial.MATERIAL_PRECIOUS,
						  RawMaterial.MATERIAL_GLASS};
	@Override protected int[] okMaterials(){	return myMats;}
	@Override protected int[] okResources(){	return null;}

	public Chant_FindGem()
	{
		super();

		lookingFor = "gem";
	}
}
