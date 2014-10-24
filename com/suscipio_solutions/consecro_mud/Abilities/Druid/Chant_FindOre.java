package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Chant_FindOre extends Chant_FindPlant
{
	@Override public String ID() { return "Chant_FindOre"; }
	private final static String localizedName = CMLib.lang().L("Find Ore");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ROCKCONTROL;}
	@Override public String displayText() { return L("(Finding "+lookingFor+")"); }
	@Override public long flags(){return Ability.FLAG_TRACKING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}

	private final int[] myMats={RawMaterial.MATERIAL_ROCK, // mithril omitted
						  RawMaterial.MATERIAL_METAL};
	@Override protected int[] okMaterials(){	return myMats;}
	@Override protected int[] okResources(){	return null;}

	public Chant_FindOre()
	{
		super();

		lookingFor = "ore";
	}
}
