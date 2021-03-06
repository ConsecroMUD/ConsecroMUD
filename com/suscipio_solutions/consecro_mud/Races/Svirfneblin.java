package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Svirfneblin extends StdRace
{
	@Override public String ID(){	return "Svirfneblin"; }
	@Override public String name(){ return "Svirfneblin"; }
	@Override public int shortestMale(){return 40;}
	@Override public int shortestFemale(){return 36;}
	@Override public int heightVariance(){return 6;}
	@Override public int lightestWeight(){return 60;}
	@Override public int weightVariance(){return 50;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Gnome";}
	private final String[]culturalAbilityNames={"Gnomish","Digging","Undercommon"};
	private final int[]culturalAbilityProficiencies={100,50,25};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,1,5,40,100,150,200,230,260};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_INFRARED);
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+2);
		affectableStats.setStat(CharStats.STAT_MAX_STRENGTH_ADJ,affectableStats.getStat(CharStats.STAT_MAX_STRENGTH_ADJ)+2);
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-2);
		affectableStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ,affectableStats.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ)-2);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)+1);
		affectableStats.setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ,affectableStats.getStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ)+1);
		affectableStats.setStat(CharStats.STAT_WISDOM,affectableStats.getStat(CharStats.STAT_WISDOM)-1);
		affectableStats.setStat(CharStats.STAT_MAX_WISDOM_ADJ,affectableStats.getStat(CharStats.STAT_MAX_WISDOM_ADJ)-1);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,affectableStats.getStat(CharStats.STAT_SAVE_MIND)+10);
		affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING,affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING)+10);
	}
	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			// Have to, since it requires use of special constructor
			final Armor s1=CMClass.getArmor("GenShirt");
			s1.setName(L("a small patchy scale tunic"));
			s1.setDisplayText(L("a small patchy scale tunic has been left here."));
			s1.setDescription(L("This small tunic is made of bits and pieces of several scaley hides, it seems.  There are lots of tiny hidden compartments on it, and loops for hanging tools."));
			s1.text();
			outfitChoices.add(s1);

			final Armor s2=CMClass.getArmor("GenShoes");
			s2.setName(L("a pair of small scaley shoes"));
			s2.setDisplayText(L("a pair of small scaley shoes lie here."));
			s2.setDescription(L("This pair of small scaley shoes appears to be a hodgepodge of scaley materials and workmanship."));
			s2.text();
			outfitChoices.add(s2);

			final Armor p1=CMClass.getArmor("GenPants");
			p1.setName(L("a pair of small patchy scale pants"));
			p1.setDisplayText(L("a pair of small patchy scale pants lie here."));
			p1.setDescription(L("This pair of small pants is made of bits and pieces of several scaley hides, it seems.  There are lots of tiny hidden compartments on it, and loops for hanging tools."));
			p1.text();
			outfitChoices.add(p1);

			final Armor s3=CMClass.getArmor("GenBelt");
			outfitChoices.add(s3);
		}
		return outfitChoices;
	}
	@Override
	public Weapon myNaturalWeapon()
	{ return funHumanoidWeapon();	}
	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is curiously close to death.^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is covered in excessive bloody wounds.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is bleeding badly from a plethora of small wounds.^N";
		else
		if(pct<.40)
			return "^y" + mob.name(viewer) + "^y has numerous bloody wounds and unexpected gashes.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y has some alarming wounds and small gashes.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p has some small unwanted bloody wounds.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p is cut and bruised in strange places.^N";
		else
		if(pct<.80)
			return "^g" + mob.name(viewer) + "^g has some small cuts and bruises.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g has a few bruises and interesting scratches.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g has a few small curious bruises.^N";
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
				("a pair of "+name().toLowerCase()+" eyes",RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
