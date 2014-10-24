package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class CageBuilding extends Wainwrighting
{
	@Override public String ID() { return "CageBuilding"; }
	private final static String localizedName = CMLib.lang().L("Cage Building");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BUILDCAGE","CAGEBUILDING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String supportedResourceString(){return "WOODEN";}

	@Override public String parametersFile(){ return "cagebuilding.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}

	@Override
	public boolean mayICraft(final Item I)
	{
		if(I==null) return false;
		if(!super.mayBeCrafted(I))
			return false;
		if((I.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
			return false;
		if(CMLib.flags().isDeadlyOrMaliciousEffect(I))
			return false;
		if(!(I instanceof Container))
			return false;
		final Container C=(Container)I;
		if((C.containTypes()==Container.CONTAIN_CAGED)
		||(C.containTypes()==(Container.CONTAIN_BODIES|Container.CONTAIN_CAGED)))
			return true;
		if(isANativeItem(I.Name()))
			return true;
		return false;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		if(commands.size()==0)
		{
			commonTell(mob,L("Build what? Enter \"buildcage list\" for a list, \"buildcage learn <item>\" to gain recipes, or \"buildcage stop\" to cancel."));
			return false;
		}
		return super.invoke(mob,commands,givenTarget,auto,asLevel);
	}
}
