package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;



public class Diviner extends SpecialistMage
{
	@Override public String ID(){return "Diviner";}
	private final static String localizedStaticName = CMLib.lang().L("Diviner");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_DIVINATION;}
	@Override public int opposed(){return Ability.DOMAIN_ILLUSION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Skill_Spellcraft",false);


		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_AnalyzeDweomer",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Spell_SolveMaze",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Spell_GroupStatus",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Spell_DetectWeaknesses",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),6,"Spell_PryingEye",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Spell_Telepathy",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Spell_NaturalCommunion",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Spell_DetectTraps",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Spell_ArmsLength",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),12,"Spell_SpyingStone",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_DetectScrying",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Spell_HearThoughts",25,"",true,false,CMParms.parseSemicolons("Spell_Telepathy",true),"");
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Spell_KnowOrigin",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Spell_KnowFate",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Spell_DiviningEye",25,"",true,false,CMParms.parseSemicolons("Spell_PryingEye",true),"");
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Spell_SpottersOrders",25,"",true,false,CMParms.parseSemicolons("Spell_DetectWeaknesses",true),"");
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Spell_Breadcrumbs",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_FindDirections",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_KnowPain",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_KnowBliss",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_DeathWarning",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Spell_DetectAmbush",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_TrueSight",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_FutureDeath",25,true);
	}
}
