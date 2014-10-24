package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer extends StdAbility
{
	@Override public String ID() { return "Prayer"; }
	private final static String localizedName = CMLib.lang().L("a Prayer");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"PRAY","PR"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER;}


	protected String prayWord(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return "pray(s) to "+mob.getMyDeity().name();
		return "pray(s)";
	}

	protected String prayForWord(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return "pray(s) for "+mob.getMyDeity().name();
		return "pray(s)";
	}

	protected String inTheNameOf(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return " in the name of "+mob.getMyDeity().name();
		return "";
	}
	protected String againstTheGods(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return " against "+mob.getMyDeity().name();
		return " against the gods";
	}
	protected String hisHerDiety(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return mob.getMyDeity().name();
		return "<S-HIS-HER> god";
	}
	protected String ofDiety(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return " of "+mob.getMyDeity().name();
		return "";
	}
	protected String prayingWord(MOB mob)
	{
		if(mob.getMyDeity()!=null)
			return "praying to "+mob.getMyDeity().name();
		return "praying";
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical target, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,target,auto,asLevel))
			return false;
		if((!auto)
		&&(!mob.isMonster())
		&&(!disregardsArmorCheck(mob))
		&&(mob.isMine(this))
		&&(!appropriateToMyFactions(mob)))
		{
			int hq=500;
			if(CMath.bset(flags(),Ability.FLAG_HOLY))
			{
				if(!CMath.bset(flags(),Ability.FLAG_UNHOLY))
					hq=1000;
			}
			else
			if(CMath.bset(flags(),Ability.FLAG_UNHOLY))
				hq=0;

			int basis=0;
			if(hq==0)
				basis=CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()),Faction.Align.EVIL);
			else
			if(hq==1000)
				basis=CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()),Faction.Align.GOOD);
			else
			{
				basis=CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()),Faction.Align.NEUTRAL);
				basis-=10;
			}

			if(CMLib.dice().rollPercentage()>basis)
				return true;

			if(hq==0)
				mob.tell(L("The evil nature of @x1 disrupts your prayer.",name()));
			else
			if(hq==1000)
				mob.tell(L("The goodness of @x1 disrupts your prayer.",name()));
			else
			if(CMLib.flags().isGood(mob))
				mob.tell(L("The anti-good nature of @x1 disrupts your thought.",name()));
			else
			if(CMLib.flags().isEvil(mob))
				mob.tell(L("The anti-evil nature of @x1 disrupts your thought.",name()));
			return false;
		}
		return true;
	}

}
