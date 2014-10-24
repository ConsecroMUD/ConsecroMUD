package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Surrender extends ThiefSkill
{
	@Override public String ID() { return "Thief_Surrender"; }
	private final static String localizedName = CMLib.lang().L("Surrender");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	private static final String[] triggerStrings =I(new String[] {"SURRENDER"});
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_INFLUENTIAL;}
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Vector theList=new Vector();
		int gold=0;
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB vic=mob.location().fetchInhabitant(i);
			if((vic!=null)&&(vic!=mob)&&(vic.isInCombat())&&(vic.getVictim()==mob))
			{
				gold+=(vic.phyStats().level()*100)-(2*getXLEVELLevel(mob));
				theList.addElement(vic);
			}
		}
		final double goldRequired=gold;
		if((!mob.isInCombat())||(theList.size()==0))
		{
			mob.tell(L("There's no one to surrender to!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		final String localCurrency=CMLib.beanCounter().getCurrency(mob.getVictim());
		final String costWords=CMLib.beanCounter().nameCurrencyShort(localCurrency,goldRequired);
		if(success&&CMLib.beanCounter().getTotalAbsoluteValue(mob,localCurrency)>=goldRequired)
		{
			final StringBuffer enemiesList=new StringBuffer("");
			for(int v=0;v<theList.size();v++)
			{
				final MOB vic=(MOB)theList.elementAt(v);
				if(v==0)
					enemiesList.append(vic.name());
				else
				if(v==theList.size()-1)
					enemiesList.append(", and "+vic.name());
				else
					enemiesList.append(", "+vic.name());
			}
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> surrender(s) to @x1, paying @x2.",enemiesList.toString(),costWords));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				CMLib.beanCounter().subtractMoney(mob,localCurrency,goldRequired);
				mob.recoverPhyStats();
				mob.makePeace();
				for(int v=0;v<theList.size();v++)
				{
					final MOB vic=(MOB)theList.elementAt(v);
					CMLib.beanCounter().addMoney(vic,localCurrency,CMath.div(goldRequired,theList.size()));
					vic.recoverPhyStats();
					vic.makePeace();
				}
			}
			else
				success=false;
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to surrender and fail(s)."));
		return success;
	}
}
