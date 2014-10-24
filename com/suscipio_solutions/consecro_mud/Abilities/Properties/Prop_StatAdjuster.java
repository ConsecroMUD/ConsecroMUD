package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


public class Prop_StatAdjuster extends Property
{
	@Override public String ID() { return "Prop_StatAdjuster"; }
	@Override public String name(){ return "Char Stats Adjusted MOB";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected static final int[] all25=new int[CharStats.CODES.instance().total()];
	static { for(final int i : CharStats.CODES.BASECODES()) all25[i]=0;}
	protected int[] stats=all25;
	@Override public boolean bubbleAffect(){return false;}
	@Override public long flags(){return Ability.FLAG_ADJUSTER;}
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ALWAYS;
	}

	@Override
	public String accountForYourself()
	{ return "Stats Trainer";	}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		for(final int i: CharStats.CODES.BASECODES())
			if(stats[i]!=0)
			{
				int newStat=affectableStats.getStat(i)+stats[i];
				final int maxStat=affectableStats.getMaxStat(i);
				if(newStat>maxStat)
					newStat=maxStat;
				else
				if(newStat<1)
					newStat=1;
				affectableStats.setStat(i,newStat);
			}
	}
	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		if(newMiscText.length()>0)
		{
			stats=new int[CharStats.CODES.TOTAL()];
			for(final int i : CharStats.CODES.BASECODES())
				stats[i]=CMParms.getParmInt(newMiscText, CMStrings.limit(CharStats.CODES.NAME(i),3), 0);
		}
	}

}
