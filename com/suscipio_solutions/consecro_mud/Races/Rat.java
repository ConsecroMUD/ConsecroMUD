package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


public class Rat extends Rodent
{
	@Override public String ID(){	return "Rat"; }
	@Override public String name(){ return "Rat"; }
	@Override public int shortestMale(){return 6;}
	@Override public int shortestFemale(){return 6;}
	@Override public int heightVariance(){return 6;}
	@Override public int lightestWeight(){return 10;}
	@Override public int weightVariance(){return 10;}
	@Override public String racialCategory(){return "Rodent";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,0 ,0 ,1 ,4 ,4 ,1 ,0 ,1 ,1 ,1 ,0 };
	@Override public int[] bodyMask(){return parts;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH,4);
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
					("some "+name().toLowerCase()+" hair",RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource
					("a pair of "+name().toLowerCase()+" teeth",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
					("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
			}
		}
		final Vector<RawMaterial> rsc=new XVector<RawMaterial>(resources);
		final RawMaterial meat=makeResource
		("some "+name().toLowerCase()+" flesh",RawMaterial.RESOURCE_MEAT);
		if((CMLib.dice().rollPercentage()<10)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
		{
			final Ability A=CMClass.getAbility("Disease_SARS");
			if(A!=null)	meat.addNonUninvokableEffect(A);
		}
		rsc.addElement(meat);
		return rsc;
	}
}
