package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;


@SuppressWarnings({"unchecked","rawtypes"})
public class Ogre extends Humanoid
{
	@Override public String ID(){	return "Ogre"; }
	@Override public String name(){ return "Ogre"; }
	@Override public int shortestMale(){return 74;}
	@Override public int shortestFemale(){return 69;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 290;}
	@Override public int weightVariance(){return 90;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Giant-kin";}
	private final String[]culturalAbilityNames={"Gigantic","Orcish","Butchering"};
	private final int[]culturalAbilityProficiencies={100,50,75};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	private final int[] agingChart={0,2,6,30,69,104,136,142,148};
	@Override public int[] getAgingChart(){return agingChart;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,affectableStats.getStat(CharStats.STAT_SAVE_MIND)-10);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,affectableStats.getStat(CharStats.STAT_SAVE_POISON)+10);
		if(affectedMOB.isMonster())
		{
			affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)-3);
			affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)-3);
			affectableStats.setStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)-4);
			affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+5);
		}
		else
		{
			affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)-2);
			affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ,affectableStats.getStat(CharStats.STAT_MAX_DEXTERITY_ADJ)-2);
			affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+4);
			affectableStats.setStat(CharStats.STAT_MAX_STRENGTH_ADJ,affectableStats.getStat(CharStats.STAT_MAX_STRENGTH_ADJ)+4);
			affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)-1);
			affectableStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ,affectableStats.getStat(CharStats.STAT_MAX_CHARISMA_ADJ)-1);
			affectableStats.setStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)-1);
			affectableStats.setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ,affectableStats.getStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ)-1);
		}
	}

	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			// Have to, since it requires use of special constructor
			final Armor s1=CMClass.getArmor("GenShirt");
			s1.setName(L("a large patchy tunic"));
			s1.setDisplayText(L("a large patchy tunic is crumpled up here."));
			s1.setDescription(L("It is a large ragged patchy nasty sweat-stained tunic."));
			s1.text();
			outfitChoices.add(s1);
			final Armor p1=CMClass.getArmor("GenPants");
			p1.setName(L("some torn leggings"));
			p1.setDisplayText(L("some torn leggings lie here."));
			p1.setDescription(L("They appear made for a large person with poor taste in clothing."));
			p1.text();
			outfitChoices.add(p1);
			final Armor s3=CMClass.getArmor("GenBelt");
			outfitChoices.add(s3);
		}
		return outfitChoices;
	}
}
