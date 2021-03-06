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
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Dragon extends StdRace
{
	@Override public String ID(){	return "Dragon"; }
	@Override public String name(){ return "Dragon"; }
	@Override public int shortestMale(){return 84;}
	@Override public int shortestFemale(){return 78;}
	@Override public int heightVariance(){return 80;}
	@Override public int lightestWeight(){return 2000;}
	@Override public int weightVariance(){return 500;}
	@Override public long forbiddenWornBits(){return Wearable.WORN_WIELD|Wearable.WORN_WAIST|Wearable.WORN_BACK|Wearable.WORN_ABOUT_BODY|Wearable.WORN_FEET|Wearable.WORN_HANDS;}
	@Override public String racialCategory(){return "Dragon";}
	private final String[]racialAbilityNames={"Dragonbreath","WingFlying"};
	private final int[]racialAbilityLevels={5,1};
	private final int[]racialAbilityProficiencies={100,100};
	private final boolean[]racialAbilityQuals={false,false};
	@Override protected String[] racialAbilityNames(){return racialAbilityNames;}
	@Override protected int[] racialAbilityLevels(){return racialAbilityLevels;}
	@Override protected int[] racialAbilityProficiencies(){return racialAbilityProficiencies;}
	@Override protected boolean[] racialAbilityQuals(){return racialAbilityQuals;}
	private final String[]culturalAbilityNames={"Draconic"};
	private final int[]culturalAbilityProficiencies={100};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}
	@Override public boolean useRideClass() { return true; }

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,0 ,0 ,1 ,4 ,4 ,1 ,0 ,1 ,1 ,1 ,2 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,5,20,110,325,500,850,950,1050};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_INFRARED);
		if(!CMLib.flags().isSleeping(affected))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		final int bump = affectedMOB.isMonster()?15:5;
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+bump);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)+bump);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)+bump);
	}
	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("huge talons"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_PIERCING);
		}
		return naturalWeapon;
	}

	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
				return name().toLowerCase()+" hatchling";
			case Race.AGE_CHILD:
			default:
				return super.makeMobName(gender, age);
		}
	}

	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is raging in bloody pain!^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is covered in blood.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is bleeding badly from lots of wounds.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y has some bloody wounds and gashed scales.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p has a few bloody wounds.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p is cut and bruised heavily.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g has a few bruises and scratched scales.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g has a few small bruises.^N";
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
				("a "+name().toLowerCase()+" claw",RawMaterial.RESOURCE_BONE));
				for(int i=0;i<100;i++)
					resources.addElement(makeResource
					("a strip of "+name().toLowerCase()+" scales",RawMaterial.RESOURCE_DRAGONSCALES));
				for(int i=0;i<50;i++)
					resources.addElement(makeResource
					("a pound of "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_DRAGONMEAT));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_DRAGONBLOOD));
			}
		}
		return resources;
	}
}
