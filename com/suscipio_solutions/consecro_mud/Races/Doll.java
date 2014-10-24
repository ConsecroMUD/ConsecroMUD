package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Doll extends StdRace
{
	@Override public String ID(){	return "Doll"; }
	@Override public String name(){ return "Doll"; }
	@Override public int shortestMale(){return 6;}
	@Override public int shortestFemale(){return 6;}
	@Override public int heightVariance(){return 3;}
	@Override public int lightestWeight(){return 10;}
	@Override public int weightVariance(){return 20;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Wood Golem";}
	@Override public boolean fertile(){return false;}
	@Override public int[] getBreathables() { return breatheAnythingArray; }

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,0,0,0,0,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH,5);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY,5);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE,13);
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOLEM);
	}
	@Override
	public Weapon myNaturalWeapon()
	{ return funHumanoidWeapon();	}

	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is nearly disassembled!^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is covered in tears and cracks.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is broken badly with lots of tears.^N";
		else
		if(pct<.40)
			return "^y" + mob.name(viewer) + "^y has numerous tears and gashes.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y has some tears and gashes.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p has a few cracks.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p is scratched heavily.^N";
		else
		if(pct<.80)
			return "^g" + mob.name(viewer) + "^g has some minor scratches.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g is a bit disheveled.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition^N";
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" clothes",RawMaterial.RESOURCE_COTTON));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" parts",RawMaterial.RESOURCE_WOOD));
			}
		}
		return resources;
	}
}
