package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMath;


public class FireElemental extends StdRace
{
	@Override public String ID(){	return "FireElemental"; }
	@Override public String name(){ return "Fire Elemental"; }
	@Override public int shortestMale(){return 64;}
	@Override public int shortestFemale(){return 60;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 400;}
	@Override public int weightVariance(){return 100;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Fire Elemental";}
	@Override public boolean fertile(){return false;}
	@Override public boolean uncharmable(){return true;}
	@Override protected boolean destroyBodyAfterUse(){return true;}
	private final String[]culturalAbilityNames={"Ignan"};
	private final int[]culturalAbilityProficiencies={100};
	@Override public String[] culturalAbilityNames(){return culturalAbilityNames;}
	@Override public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,0,0,0,0,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER,YEARS_AGE_LIVES_FOREVER};
	@Override public int[] getAgingChart(){return agingChart;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,affectableStats.getStat(CharStats.STAT_SAVE_POISON)+100);
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,affectableStats.getStat(CharStats.STAT_SAVE_DISEASE)+100);
		affectableStats.setStat(CharStats.STAT_SAVE_FIRE,affectableStats.getStat(CharStats.STAT_SAVE_FIRE)+100);
	}
	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("an arm of flame"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_PLASMA);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BURNING);
		}
		return naturalWeapon;
	}

	@Override
	public String makeMobName(char gender, int age)
	{
		return makeMobName('N',Race.AGE_MATURE);
	}

	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is almost put out!^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is flickering alot and is almost smoked out.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is flickering alot and smoking massively.^N";
		else
		if(pct<.40)
			return "^y" + mob.name(viewer) + "^y is flickering alot and smoking a lot.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y is flickering and smoking.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p is flickering and smoking somewhat.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p is showing large flickers.^N";
		else
		if(pct<.80)
			return "^g" + mob.name(viewer) + "^g is showing some flickers.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g is showing small flickers.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}
	
	@Override 
	public DeadBody getCorpseContainer(MOB mob, Room room)
	{
		final DeadBody body = super.getCorpseContainer(mob, room);
		if(body != null)
		{
			body.setMaterial(RawMaterial.RESOURCE_ASH);
		}
		return body;
	}
	
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
					("a pile of coal",RawMaterial.RESOURCE_COAL));
			}
		}
		return resources;
	}
}
