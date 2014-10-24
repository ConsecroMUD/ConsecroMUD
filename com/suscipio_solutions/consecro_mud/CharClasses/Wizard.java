package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Wizard extends Mage
{
	@Override public String ID(){return "Wizard";}
	private final static String localizedStaticName = CMLib.lang().L("Wizard");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return "Mage";}
	@Override protected boolean grantSomeSpells(){return false;}

	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Spellcraft",50,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_ScrollCopy",100,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_Scribe",75,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Papermaking",75,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_ReadMagic",100,true);


		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_MagicMissile",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_ResistMagicMissiles",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_Shield",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Spell_IronGrip",false);

		for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)
			&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL))
			{
				final int level=CMLib.ableMapper().getQualifyingLevel(ID(),true,A.ID());
				if(level>0)
				{
					final AbilityMapper.AbilityMapping able=CMLib.ableMapper().getAbleMap(ID(),A.ID());
					if((able!=null)
					&&(!CMLib.ableMapper().getDefaultGain(ID(),true,A.ID())))
					{
						able.costOverrides=new Integer[]{Integer.valueOf(0),Integer.valueOf(0),Integer.valueOf(0),Integer.valueOf(0)};
						able.defaultProficiency=100;
					}
				}
			}
		}
	}

	@Override public int availabilityCode(){return 0;}
	//public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public String getOtherBonusDesc()
	{
		return "Can memorize any spell for casting without expending a training point.";
	}
	@Override
	public String getOtherLimitsDesc()
	{
		return "Unable to learn spells permanently; can only memorize them.";
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((myHost==null)||(!(myHost instanceof MOB)))
		   return;
		final MOB mob=(MOB)myHost;
		if(msg.amISource(mob)&&(msg.tool()!=null))
		{
			if(msg.tool() instanceof Ability)
			{
				final Ability A=mob.fetchAbility(msg.tool().ID());
				if((A!=null)&&(!CMLib.ableMapper().getDefaultGain(ID(),false,A.ID()))
				&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL))
				{
					mob.delAbility(A);
					mob.recoverMaxState();
				}
			}
			else
			if(msg.tool().ID().equalsIgnoreCase("Skill_ScrollCopy"))
			{

			}
		}
	}

	@Override
	public void affectCharState(MOB mob, CharState state)
	{
		super.affectCharState(mob,state);
		if(mob.baseCharStats().getCurrentClass().ID().equals(ID()))
		{
			Ability A=null;
			for(int a=0;a<mob.numAbilities();a++)
			{
				A=mob.fetchAbility(a);
				if((A!=null)
				&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
				&&(!CMLib.ableMapper().getDefaultGain(ID(),false,A.ID())))
				{
					final int[] cost=A.usageCost(mob,true);
					final int manaCost=cost[Ability.USAGEINDEX_MANA];
					if(manaCost>0)
					{
						if(state.getMana()<manaCost)
						{
							mob.delAbility(A);
							a--;
						}
						else
							state.setMana(state.getMana()-manaCost);
					}
				}
			}
			if(mob.curState().getMana()>state.getMana())
				mob.curState().setMana(state.getMana());
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(myHost instanceof MOB))
			return super.okMessage(myHost,msg);
		final MOB myChar=(MOB)myHost;
		if((msg.tool()==null)||(!(msg.tool() instanceof Ability)))
		   return super.okMessage(myChar,msg);
		if(msg.amISource(myChar)
		&&(myChar.isMine(msg.tool())))
		{
			if((msg.sourceMinor()==CMMsg.TYP_CAST_SPELL)
			&&(!CMLib.ableMapper().getDefaultGain(ID(),true,msg.tool().ID())))
			{
				if(CMLib.dice().rollPercentage()>
				   (myChar.charStats().getStat(CharStats.STAT_INTELLIGENCE)*((myChar.charStats().getCurrentClass().ID().equals(ID()))?1:2)))
				{
					myChar.location().show(myChar,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> fizzle(s) a spell."));
					return false;
				}
			}
		}
		return super.okMessage(myChar,msg);
	}
}
