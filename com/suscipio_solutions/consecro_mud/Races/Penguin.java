package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Penguin extends StdRace
{
	@Override public String ID(){	return "Penguin"; }
	@Override public String name(){ return "Penguin"; }
	@Override public int shortestMale(){return 8;}
	@Override public int shortestFemale(){return 8;}
	@Override public int heightVariance(){return 24;}
	@Override public int lightestWeight(){return 5;}
	@Override public int weightVariance(){return 5;}
	@Override public long forbiddenWornBits(){return ~(Wearable.WORN_HEAD|Wearable.WORN_EYES);}
	@Override public String racialCategory(){return "Avian";}
	private final String[]racialAbilityNames={"Skill_Swim"};
	private final int[]racialAbilityLevels={1};
	private final int[]racialAbilityProficiencies={100};
	private final boolean[]racialAbilityQuals={false};
	@Override protected String[] racialAbilityNames(){return racialAbilityNames;}
	@Override protected int[] racialAbilityLevels(){return racialAbilityLevels;}
	@Override protected int[] racialAbilityProficiencies(){return racialAbilityProficiencies;}
	@Override protected boolean[] racialAbilityQuals(){return racialAbilityQuals;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,0 ,0 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,1 ,2 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,1,2,4,5,5,6,7,8};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH,3);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY,3);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE,1);
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SWIMMING);
	}
	@Override
	public String arriveStr()
	{
		return "waddles in";
	}
	@Override
	public String leaveStr()
	{
		return "waddles";
	}
	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("a menacing beak"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}

	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is hovering on deaths door!^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is covered in blood.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is bleeding badly from lots of wounds.^N";
		else
		if(pct<.40)
			return "^y" + mob.name(viewer) + "^y has numerous bloody wounds.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y has some bloody wounds.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p has a lot of nasty bruises.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p has a few nasty bruises.^N";
		else
		if(pct<.80)
			return "^g" + mob.name(viewer) + "^g has a nasty bruise.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g has a few bumps.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g has a minor bump.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some webbed "+name().toLowerCase()+" feet",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_POULTRY));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
