package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Apprentice extends StdCharClass
{
	@Override public String ID(){return "Apprentice";}
	private final static String localizedStaticName = CMLib.lang().L("Apprentice");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return "Commoner";}
	@Override public int getBonusPracLevel(){return 5;}
	@Override public int getBonusAttackLevel(){return -1;}
	@Override public int getAttackAttribute(){return CharStats.STAT_WISDOM;}
	@Override public int getLevelsPerBonusDamage(){ return 10;}
	@Override public int getTrainsFirstLevel(){return 6;}
	@Override public String getHitPointsFormula(){return "((@x6<@x7)/9)+(1*(1?4))"; }
	@Override public String getManaFormula(){return "((@x4<@x5)/10)+(1*(1?2))"; }
	@Override public int getLevelCap(){ return 1;}
	@Override public SubClassRule getSubClassRule() { return SubClassRule.ANY; }
	@Override public int allowedArmorLevel(){return CharClass.ARMOR_CLOTH;}
	@Override public int allowedWeaponLevel(){return CharClass.WEAPONS_DAGGERONLY;}
	private final HashSet disallowedWeapons=buildDisallowedWeaponClasses();
	@Override protected HashSet disallowedWeaponClasses(MOB mob){return disallowedWeapons;}
	protected HashSet currentApprentices=new HashSet();

	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Write",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Natural",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Recall",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Swim",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Climb",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"ClanCrafting",false);
	}

	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_HEROIC|Area.THEME_TECHNOLOGY;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(ticking instanceof MOB)
		&&(!((MOB)ticking).isMonster()))
		{
			if(((MOB)ticking).baseCharStats().getCurrentClass().ID().equals(ID()))
			{
				if(!currentApprentices.contains(ticking))
					currentApprentices.add(ticking);
			}
			else
			if(currentApprentices.contains(ticking))
			{
				currentApprentices.remove(ticking);
				((MOB)ticking).tell(L("\n\r\n\r^ZYou are no longer an apprentice!!!!^N\n\r\n\r"));
				CMLib.leveler().postExperience((MOB)ticking,null,null,1000,false);
			}
		}
		return super.tick(ticking,tickID);
	}

	private final String[] raceRequiredList=new String[]{"All"};
	@Override public String[] getRequiredRaceList(){ return raceRequiredList; }
	private final Pair<String,Integer>[] minimumStatRequirements=new Pair[]{
		new Pair<String,Integer>("Wisdom",Integer.valueOf(5)),
		new Pair<String,Integer>("Intelligence",Integer.valueOf(5))
	};
	@Override public Pair<String,Integer>[] getMinimumStatRequirements() { return minimumStatRequirements; }

	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			final Weapon w=CMClass.getWeapon("Dagger");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

	@Override public String getOtherBonusDesc(){return "Gains lots of xp for training to a new class.";}
}
