package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



@SuppressWarnings({"unchecked","rawtypes"})
public class Immortal extends StdCharClass
{
	@Override public String ID(){return "Immortal";}
	private final static String localizedStaticName = CMLib.lang().L("Immortal");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return ID();}
	@Override public boolean leveless(){return true;}

	public Immortal()
	{
		super();
		for(final int i : CharStats.CODES.BASECODES())
			maxStatAdj[i]=7;
	}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTaming",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTrading",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTraining",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Domesticating",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"InstrumentMaking",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"PlantLore",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Scrapping",false);

		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AstroEngineering",100,"",true,true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Common",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Resistance",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Multiwatch",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Wrath",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Hush",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Freeze",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Record",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Stinkify",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Banish",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Metacraft",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Immortal_Injure",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Amputation",100,"",true,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Chant_AlterTime",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Chant_MoveSky",true);
	}

	@Override public int availabilityCode(){return 0;}

	@Override public String getStatQualDesc(){return "Must be granted by another Immortal.";}
	@Override
	public boolean qualifiesForThisClass(MOB mob, boolean quiet)
	{
		if(!quiet)
			mob.tell(L("This class cannot be learned."));
		return false;
	}

	public static final String[] IMMORTAL_IMMUNITIES={"Spell_Scry","Thief_Listen","Spell_Claireaudience","Spell_Clairevoyance"};

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.tool() != null)
		&&(msg.target()==myHost)
		&&(msg.tool() instanceof Ability)
		&&((CMParms.indexOf(IMMORTAL_IMMUNITIES,msg.tool().ID())>=0)
			||((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_DISEASE)
			||((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_POISON)))
		{
			//((MOB)msg.target()).tell(L("You are immune to @x1.",msg.tool().name()));
			if(msg.source()!=msg.target())
				msg.source().tell(msg.source(),msg.target(),msg.tool(),L("<T-NAME> is immune to <O-NAME>."));
			return false;
		}
		return super.okMessage(myHost, msg);
	}

	@Override
	public List<Item> outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			final Weapon w=CMClass.getWeapon("ImmortalStaff");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

	@Override
	public void startCharacter(MOB mob, boolean isBorrowedClass, boolean verifyOnly)

	{
		// immortals ALWAYS use borrowed abilities
		super.startCharacter(mob, true, verifyOnly);
		if(verifyOnly)
			grantAbilities(mob,true);
	}

	@Override
	public void grantAbilities(MOB mob, boolean isBorrowedClass)
	{
		final boolean allowed=CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.ALLSKILLS);
		if((!allowed)&&(mob.playerStats()!=null)&&(!mob.playerStats().getSecurityFlags().contains(CMSecurity.SecFlag.ALLSKILLS,false)))
		{
			final List<String> oldSet=CMParms.parseSemicolons(mob.playerStats().getSetSecurityFlags(null),true);
			if(!oldSet.contains(CMSecurity.SecFlag.ALLSKILLS.name()))
			{
				oldSet.add(CMSecurity.SecFlag.ALLSKILLS.name());
				mob.playerStats().getSetSecurityFlags(CMParms.toSemicolonList(oldSet));
			}
		}
		super.grantAbilities(mob,isBorrowedClass);
		if((!allowed)&&(mob.playerStats()!=null)&&(mob.playerStats().getSecurityFlags().contains(CMSecurity.SecFlag.ALLSKILLS,false)))
		{
			final List<String> oldSet=CMParms.parseSemicolons(mob.playerStats().getSetSecurityFlags(null),true);
			if(oldSet.contains(CMSecurity.SecFlag.ALLSKILLS.name()))
			{
				oldSet.remove(CMSecurity.SecFlag.ALLSKILLS.name());
				mob.playerStats().getSetSecurityFlags(CMParms.toSemicolonList(oldSet));
			}
		}
	}
}
