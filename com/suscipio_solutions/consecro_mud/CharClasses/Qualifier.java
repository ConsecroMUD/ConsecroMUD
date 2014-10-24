package com.suscipio_solutions.consecro_mud.CharClasses;
import java.util.Enumeration;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Qualifier extends StdCharClass
{
	@Override public String ID(){return "Qualifier";}
	private final static String localizedStaticName = CMLib.lang().L("Qualifier");
	@Override public String name() { return localizedStaticName; }
	@Override public String baseClass(){return ID();}
	private static boolean abilitiesLoaded=false;
	public boolean loaded(){return abilitiesLoaded;}
	public void setLoaded(boolean truefalse){abilitiesLoaded=truefalse;}

	public Qualifier()
	{
		super();
		for(final int i: CharStats.CODES.BASECODES())
			maxStatAdj[i]=7;
	}

	@Override public int availabilityCode(){return 0;}

	@Override public String getStatQualDesc(){return "Must be granted by an Immortal.";}
	@Override
	public boolean qualifiesForThisClass(MOB mob, boolean quiet)
	{
		if(!quiet)
			mob.tell(L("This class cannot be learned."));
		return false;
	}

	@Override
	public void startCharacter(MOB mob, boolean isBorrowedClass, boolean verifyOnly)
	{
		if(!loaded())
		{
			setLoaded(true);
			for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				final int lvl=CMLib.ableMapper().lowestQualifyingLevel(A.ID());
				if((lvl>0)&&(!CMLib.ableMapper().classOnly("Immortal",A.ID())))
					CMLib.ableMapper().addCharAbilityMapping(ID(),lvl,A.ID(),false);
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
