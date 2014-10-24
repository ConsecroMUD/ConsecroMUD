package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Chant_SenseFluids extends Chant_SensePlants
{
	@Override public String ID() { return "Chant_SenseFluids"; }
	private final static String localizedName = CMLib.lang().L("Sense Fluids");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Sensing Fluids)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public long flags(){return Ability.FLAG_TRACKING;}
	@Override protected String word(){return "fluids";}

	private final int[] myMats={RawMaterial.MATERIAL_LIQUID};
	@Override protected int[] okMaterials(){	return myMats;}
	@Override protected int[] okResources(){	return null;}
}
