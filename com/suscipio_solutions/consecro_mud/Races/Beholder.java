package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Beholder extends StdRace
{
	@Override public String ID(){	return "Beholder"; }
	@Override public String name(){ return "Beholder"; }
	@Override public int shortestMale(){return 64;}
	@Override public int shortestFemale(){return 60;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 100;}
	@Override public int weightVariance(){return 100;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Unique";}
	private final String[]racialAbilityNames={"Spell_Sleep","Spell_FloatingDisc","Spell_Fear","Spell_Slow","Spell_Charm","Prayer_CauseCritical","Spell_DispelMagic","Spell_FleshStone","Prayer_DeathFinger","Spell_Disintegrate"};
	private final int[]racialAbilityLevels={1,1,1,5,10,10,15,20,30,30};
	private final int[]racialAbilityProficiencies={50,50,50,50,50,50,100,50,50,50};
	private final boolean[]racialAbilityQuals={false,false,false,false,false,false,false,false,false,false};
	@Override public String[] racialAbilityNames(){return racialAbilityNames;}
	@Override public int[] racialAbilityLevels(){return racialAbilityLevels;}
	@Override public int[] racialAbilityProficiencies(){return racialAbilityProficiencies;}
	@Override public boolean[] racialAbilityQuals(){return racialAbilityQuals;}
	private final String[]culturalAbilityNames={"Undercommon"};
	private final int[]culturalAbilityProficiencies={100};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={-1,10,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,5,20,110,325,500,850,950,1050};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,25);
		affectableStats.setStat(CharStats.STAT_SAVE_MAGIC,75);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,100);
	}

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int x=0;x<10;x++)
					resources.addElement(makeResource
					("a "+name().toLowerCase()+" eye",RawMaterial.RESOURCE_MEAT));
			}
		}
		return resources;
	}
}
