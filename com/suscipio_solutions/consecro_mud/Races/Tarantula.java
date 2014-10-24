package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Tarantula extends StdRace
{
	@Override public String ID(){	return "Tarantula"; }
	@Override public String name(){ return "Tarantula"; }
	@Override public int shortestMale(){return 2;}
	@Override public int shortestFemale(){return 2;}
	@Override public int heightVariance(){return 0;}
	@Override public int lightestWeight(){return 1;}
	@Override public int weightVariance(){return 0;}
	@Override public long forbiddenWornBits(){return Integer.MAX_VALUE;}
	@Override public String racialCategory(){return "Arachnid";}
	private final String[]racialAbilityNames={"Poison_Venom"};
	private final int[]racialAbilityLevels={1};
	private final int[]racialAbilityProficiencies={25};
	private final boolean[]racialAbilityQuals={false};
	@Override public String[] racialAbilityNames(){return racialAbilityNames;}
	@Override public int[] racialAbilityLevels(){return racialAbilityLevels;}
	@Override public int[] racialAbilityProficiencies(){return racialAbilityProficiencies;}
	@Override public boolean[] racialAbilityQuals(){return racialAbilityQuals;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={2 ,99,0 ,1 ,0 ,0 ,0 ,1 ,8 ,8 ,0 ,0 ,1 ,0 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,0,0,1,1,1,1,2,2};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SNEAKING);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOLEM);
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_STRENGTH,3);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,3);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,1);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,affectableStats.getStat(CharStats.STAT_SAVE_POISON)+100);
	}
	@Override
	public String arriveStr()
	{
		return "creeps in";
	}
	@Override
	public String leaveStr()
	{
		return "creeps";
	}
	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
			case Race.AGE_CHILD:
				return "baby "+name().toLowerCase();
			default :
				return super.makeMobName('N', age);
		}
	}
	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("a nasty maw"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" legs",RawMaterial.RESOURCE_MEAT));
			}
		}
		return resources;
	}
}
