package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


public class Prop_StatTrainer extends Property
{
	@Override public String ID() { return "Prop_StatTrainer"; }
	@Override public String name(){ return "Good training MOB";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected static final int[] all25=new int[CharStats.CODES.instance().total()];
	static { for(final int i : CharStats.CODES.BASECODES()) all25[i]=25;}
	protected int[] stats=all25;
	protected boolean noteach=false;

	@Override
	public String accountForYourself()
	{ return "Stats Trainer";	}

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		if((!noteach)&&(affectedMOB.isAttribute(MOB.Attrib.NOTEACH)))
			affectedMOB.setAttribute(MOB.Attrib.NOTEACH,false);

		for(final int i: CharStats.CODES.BASECODES())
			affectableStats.setStat(i,stats[i]);
	}
	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		if(newMiscText.length()>0)
		{
			if(newMiscText.toUpperCase().indexOf("NOTEACH")>=0)
				noteach=true;
			stats=new int[CharStats.CODES.TOTAL()];
			for(final int i : CharStats.CODES.BASECODES())
				stats[i]=CMParms.getParmInt(newMiscText, CMStrings.limit(CharStats.CODES.NAME(i),3), 25);
		}
	}

}
