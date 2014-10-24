package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMSecurity.DisFlag;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;




public class PlayerClass extends StdCharClass
{
	@Override public String ID(){return "PlayerClass";}
	private final static String localizedStaticName = CMLib.lang().L("PlayerClass");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return ID();}
	@Override public boolean showThinQualifyList(){return true;}
	private static boolean abilitiesLoaded=false;
	public boolean loaded(){return abilitiesLoaded;}
	public void setLoaded(boolean truefalse){abilitiesLoaded=truefalse;}

	public PlayerClass()
	{
		super();
		for(final int i: CharStats.CODES.BASECODES())
			maxStatAdj[i]=7;
	}

	@Override public int availabilityCode(){return 0;}

	@Override public String getStatQualDesc(){return "";}
	@Override
	public boolean qualifiesForThisClass(MOB mob, boolean quiet)
	{
		if(!quiet)
			mob.tell(L("This class cannot be learned."));
		return false;
	}

	private boolean isSkill(int classCode)
	{
		switch(classCode&Ability.ALL_ACODES)
		{
		case Ability.ACODE_COMMON_SKILL:
		case Ability.ACODE_DISEASE:
		case Ability.ACODE_POISON:
		case Ability.ACODE_SKILL:
		case Ability.ACODE_THIEF_SKILL:
		case Ability.ACODE_TRAP:
		case Ability.ACODE_LANGUAGE:
		case Ability.ACODE_PROPERTY:
		case Ability.ACODE_TECH:
			return true;
		case Ability.ACODE_CHANT:
		case Ability.ACODE_PRAYER:
		case Ability.ACODE_SPELL:
		case Ability.ACODE_SUPERPOWER:
			return false;
		}
		return true;
	}

	private List<String> makeRequirements(LinkedList<List<String>> prevSets, Ability A)
	{
		for(final Iterator<List<String>> i=prevSets.descendingIterator();i.hasNext();)
		{
			final List<String> prevSet=i.next();
			final List<String> reqSet=new Vector<String>();
			for(final String prevID : prevSet)
			{
				final Ability pA=CMClass.getAbility(prevID);
				if(A.classificationCode()==pA.classificationCode())
					reqSet.add(pA.ID());
			}
			if(reqSet.size()==0)
				for(final String prevID : prevSet)
				{
					final Ability pA=CMClass.getAbility(prevID);
					if((A.classificationCode()&Ability.ALL_ACODES)==(pA.classificationCode()&Ability.ALL_ACODES))
						reqSet.add(pA.ID());
				}
			if(reqSet.size()==0)
			{
				final boolean aIsSkill=isSkill(A.classificationCode());
				for(final String prevID : prevSet)
				{
					final Ability pA=CMClass.getAbility(prevID);
					if(aIsSkill==isSkill(pA.classificationCode()))
						reqSet.add(pA.ID());
				}
			}
			if(reqSet.size()>0)
				return reqSet;
		}
		return new Vector<String>();
	}

	@Override
	public void startCharacter(MOB mob, boolean isBorrowedClass, boolean verifyOnly)
	{
		if(!loaded())
		{
			setLoaded(true);
			final LinkedList<CharClass> charClassesOrder=new LinkedList<CharClass>();
			final HashSet<String> names=new HashSet<String>();
			for(final Enumeration<CharClass> c=CMClass.charClasses();c.hasMoreElements();)
			{
				final CharClass C=c.nextElement();
				if(C.baseClass().equals(C.ID()) && (!C.baseClass().equalsIgnoreCase("Immortal"))&& (!C.baseClass().equalsIgnoreCase("PlayerClass"))&& (!C.baseClass().equalsIgnoreCase("Qualifier"))&& (!C.baseClass().equalsIgnoreCase("StdCharClass")))
				{
					names.add(C.ID());
					charClassesOrder.add(C);
				}
			}
			for(final Enumeration<CharClass> c=CMClass.charClasses();c.hasMoreElements();)
			{
				final CharClass C=c.nextElement();
				if(!names.contains(C.ID()) && names.contains(C.baseClass()))
					charClassesOrder.add(C);
			}
			for(final Enumeration<CharClass> c=CMClass.charClasses();c.hasMoreElements();)
			{
				final CharClass C=c.nextElement();
				if(C.baseClass().equals("Commoner") && (!names.contains(C.ID())))
					charClassesOrder.add(C);
			}

			for(final CharClass C : charClassesOrder)
			{
				final LinkedList<List<String>> prevSets=new LinkedList<List<String>>();
				for(int lvl=1;lvl<CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL);lvl++)
				{
					final List<String> curSet=CMLib.ableMapper().getLevelListings(C.ID(), false, lvl);
					for(final String ID : curSet)
					{
						final String defaultParam=CMLib.ableMapper().getDefaultParm(C.ID(), true, ID);
						if(CMLib.ableMapper().getQualifyingLevel(ID(), false, ID)<0)
						{
							final Ability A=CMClass.getAbility(ID);
							if(A==null)
							{
								Log.errOut("Unknonwn class: "+ID);
								continue;
							}
							List<String> reqSet=makeRequirements(prevSets,A);
							if(reqSet.size()>0)
								reqSet=new XVector<String>(CMParms.toStringList(reqSet));
							int level=0;
							if(!this.leveless() && (!CMSecurity.isDisabled(DisFlag.LEVELS)))
								level=CMLib.ableMapper().lowestQualifyingLevel(A.ID());
							if(level<0) level=0;
							CMLib.ableMapper().addCharAbilityMapping(ID(), 0, ID, 0, defaultParam, false, false, reqSet, "");
						}
					}
					if(curSet.size()>0)
						prevSets.add(curSet);
				}
			}
		}
		super.startCharacter(mob, false, verifyOnly);
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
				&&(!CMLib.ableMapper().getAllQualified(ID(),true,A.ID()))
				&&(!CMLib.ableMapper().getDefaultGain(ID(),true,A.ID())))
					giveMobAbility(mob,A,CMLib.ableMapper().getDefaultProficiency(ID(),true,A.ID()),CMLib.ableMapper().getDefaultParm(ID(),true,A.ID()),isBorrowedClass);
			}
		}
	}

}
