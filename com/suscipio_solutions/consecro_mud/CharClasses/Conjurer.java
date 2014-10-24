package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Conjurer extends SpecialistMage
{
	@Override public String ID(){return "Conjurer";}
	private final static String localizedStaticName = CMLib.lang().L("Conjurer");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_CONJURATION;}
	@Override public int opposed(){return Ability.DOMAIN_TRANSMUTATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}

	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().delCharAbilityMapping(ID(),"Spell_IronGrip");

		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Spell_SummonMarker",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Spell_Scatter",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_WaterCannon",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Spell_ChanneledMissiles",0,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Spell_WordRecall",0,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Spell_FlamingSword",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Spell_MarkerSummoning",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Spell_AcidSpray",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_MarkerPortal",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_TeleportObject",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_ConjureNexus",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_FlamingEnsnarement",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_SummonArmy",25,true);
	}
}
