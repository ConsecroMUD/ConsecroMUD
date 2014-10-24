package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class MasterDistilling extends Baking
{
	private String cookingID="";
	@Override public String ID() { return "MasterDistilling"+cookingID; }
	@Override public String name() { return L("Master Distilling"+cookingID); }
	private static final String[] triggerStrings =I(new String[] {"MDISTILLING","MASTERDISTILLING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	protected List<String> noUninvokes=new ArrayList<String>(0);
	@Override protected List<String> getUninvokeException() { return noUninvokes; }

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(60,mob,1,8);
	}
	@Override protected int baseYield() { return 2; }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		try
		{
			cookingID="";
			int num=1;
			while(mob.fetchEffect("MasterDistilling"+cookingID)!=null)
				cookingID=Integer.toString(++num);
			final List<String> noUninvokes=new Vector<String>(1);
			for(int i=0;i<mob.numEffects();i++)
			{
				final Ability A=mob.fetchEffect(i);
				if(((A instanceof MasterDistilling)||A.ID().equals("Distilling"))
				&&(noUninvokes.size()<5))
					noUninvokes.add(A.ID());
			}
			this.noUninvokes=noUninvokes;
			return super.invoke(mob, commands, givenTarget, auto, asLevel);
		}
		finally
		{
			cookingID="";
		}
	}
}
