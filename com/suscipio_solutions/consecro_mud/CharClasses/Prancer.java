package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prancer extends StdCharClass
{
	@Override public String ID(){return "Prancer";}
	private final static String localizedStaticName = CMLib.lang().L("Dancer");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return "Bard";}
	@Override public String getMovementFormula(){return "18*((@x2<@x3)/18)"; }
	@Override public int getBonusPracLevel(){return 1;}
	@Override public int getBonusAttackLevel(){return 0;}
	@Override public int getAttackAttribute(){return CharStats.STAT_CHARISMA;}
	@Override public int getLevelsPerBonusDamage(){ return 10;}
	@Override public String getHitPointsFormula(){return "((@x6<@x7)/3)+(2*(1?6))"; }
	@Override public String getManaFormula(){return "((@x4<@x5)/6)+(1*(1?2))"; }
	@Override protected String armorFailMessage(){return "<S-NAME> armor makes <S-HIM-HER> mess up <S-HIS-HER> <SKILL>!";}
	@Override public int allowedArmorLevel(){return CharClass.ARMOR_CLOTH;}
	@Override public int allowedWeaponLevel(){return CharClass.WEAPONS_THIEFLIKE;}
	private final HashSet disallowedWeapons=buildDisallowedWeaponClasses();
	@Override protected HashSet disallowedWeaponClasses(MOB mob){return disallowedWeapons;}

	public Prancer()
	{
		super();
		maxStatAdj[CharStats.STAT_CHARISMA]=4;
		maxStatAdj[CharStats.STAT_STRENGTH]=4;
	}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Natural",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Ranged",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Sword",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Recall",50,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Write",50,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Swim",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Befriend",50,true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Dance_Stop",100,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Dance_CanCan",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),2,"Thief_Lore",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),2,"Dance_Foxtrot",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Fighter_Kick",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Skill_Climb",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Dance_Tarantella",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Thief_Appraise",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Dance_Waltz",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Skill_Dodge",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Dance_Salsa",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Dance_Grass",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),6,"Dance_Clog",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Thief_Distract",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Dance_Capoeira",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Dance_Tap",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Dance_Swing",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Skill_Disarm",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Dance_Basse",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Fighter_BodyFlip",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Dance_Tango",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Fighter_Spring",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Dance_Polka",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),12,"Dance_RagsSharqi",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),12,"Dance_Manipuri",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Skill_Trip",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Dance_Cotillon",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Skill_TwoWeaponFighting",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Dance_Ballet",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Fighter_Tumble",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Dance_Jitterbug",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Dance_Butoh",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Skill_Attack2",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Dance_Courante",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Dance_Musette",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Fighter_Endurance",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Fighter_Cartwheel",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Dance_Swords",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Dance_Flamenco",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Fighter_Roll",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Dance_Jingledress",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Dance_Morris",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Fighter_BlindFighting",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Dance_Butterfly",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Dance_Macabre",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Fighter_CircleTrip",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Dance_War",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Dance_Square",true);
	}

	@Override public int availabilityCode(){return Area.THEME_FANTASY;}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		Bard.visitationBonusMessage(host,msg);
	}

	private final String[] raceRequiredList=new String[]{
		"Human","Humanoid","Elf","Halfling"
	};
	@Override public String[] getRequiredRaceList(){ return raceRequiredList; }

	private final Pair<String,Integer>[] minimumStatRequirements=new Pair[]{
		new Pair<String,Integer>("Charisma",Integer.valueOf(9)),
		new Pair<String,Integer>("Strength",Integer.valueOf(9))
	};
	@Override public Pair<String,Integer>[] getMinimumStatRequirements() { return minimumStatRequirements; }

	@Override public String getOtherLimitsDesc(){return "";}
	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			final Weapon w=CMClass.getWeapon("Shortsword");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}



	@Override
	public void grantAbilities(MOB mob, boolean isBorrowedClass)
	{
		super.grantAbilities(mob,isBorrowedClass);
		if(mob.playerStats()==null)
		{
			final List<AbilityMapper.AbilityMapping> V=CMLib.ableMapper().getUpToLevelListings(ID(),
												mob.charStats().getClassLevel(ID()),
												false,
												false);
			for(final AbilityMapper.AbilityMapping able : V)
			{
				final Ability A=CMClass.getAbility(able.abilityID);
				if((A!=null)
				&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)
				&&(!CMLib.ableMapper().getDefaultGain(ID(),true,A.ID())))
					giveMobAbility(mob,A,CMLib.ableMapper().getDefaultProficiency(ID(),true,A.ID()),CMLib.ableMapper().getDefaultParm(ID(),true,A.ID()),isBorrowedClass);
			}
		}
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			if(CMLib.flags().isStanding((MOB)affected))
			{
				final MOB mob=(MOB)affected;
				final int attArmor=(((int)Math.round(CMath.div(mob.charStats().getStat(CharStats.STAT_DEXTERITY),9.0)))+1)*(mob.charStats().getClassLevel(this)-1);
				affectableStats.setArmor(affectableStats.armor()-attArmor);
			}
		}
	}

	@Override public int adjustExperienceGain(MOB host, MOB mob, MOB victim, int amount){ return Bard.bardAdjustExperienceGain(host,mob,victim,amount,5.0);}

	@Override public String getOtherBonusDesc(){return "Receives defensive bonus for high dexterity.  Receives group bonus combat experience when in an intelligent group, and more for a group of players.  Receives exploration and pub-finding experience based on danger level.";}

	@Override
	public void level(MOB mob, List<String> newAbilityIDs)
	{
		super.level(mob, newAbilityIDs);
		if(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))  return;
		final int attArmor=(((int)Math.round(CMath.div(mob.charStats().getStat(CharStats.STAT_DEXTERITY),9.0)))+1);
		mob.tell(L("^NYour grace grants you a defensive bonus of ^H@x1^?.^N",""+attArmor));
	}
}

