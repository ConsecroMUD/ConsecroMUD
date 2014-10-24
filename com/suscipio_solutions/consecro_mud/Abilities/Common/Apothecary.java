package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MagicDust;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Perfume;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Apothecary extends Cooking
{
	@Override public String ID() { return "Apothecary"; }
	private final static String localizedName = CMLib.lang().L("Apothecary");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"APOTHECARY","MIX"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String supportedResourceString(){return "MISC";}
	@Override public String cookWordShort(){return "mix";}
	@Override public String cookWord(){return "mixing";}
	@Override public boolean honorHerbs(){return false;}
	@Override protected ExpertiseLibrary.SkillCostDefinition getRawTrainingCost() { return CMProps.getSkillTrainCostFormula(ID()); }

	@Override public String parametersFile(){ return "poisons.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}

	public Apothecary()
	{
		super();

		defaultFoodSound = "hotspring.wav";
		defaultDrinkSound = "hotspring.wav";
	}

	@Override public boolean supportsDeconstruction() { return false; }

	@Override
	public boolean mayICraft(final Item I)
	{
		if(I==null) return false;
		if(!super.mayBeCrafted(I))
			return false;
		if(I instanceof Perfume)
		{
			return true;
		}
		else
		if(I instanceof Drink)
		{
			final Drink D=(Drink)I;
			if(D.liquidType()!=RawMaterial.RESOURCE_POISON)
				return false;
			if(CMLib.flags().flaggedAffects(D, Ability.FLAG_INTOXICATING).size()>0)
				return false;
			if(CMLib.flags().domainAffects(D, Ability.ACODE_POISON).size()>0)
				return true;
			return true;
		}
		else
		if(I instanceof MagicDust)
		{
			final MagicDust M=(MagicDust)I;
			final List<Ability> spells=M.getSpells();
			if((spells == null)||(spells.size()==0))
				return false;
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((!super.invoke(mob,commands,givenTarget,auto,asLevel))||(buildingI==null))
			return false;
		final Ability A2=buildingI.fetchEffect(0);
		if((A2!=null)
		&&(buildingI instanceof Drink))
		{
			((Drink)buildingI).setLiquidType(RawMaterial.RESOURCE_POISON);
			buildingI.setMaterial(RawMaterial.RESOURCE_POISON);
		}
		return true;
	}
}
