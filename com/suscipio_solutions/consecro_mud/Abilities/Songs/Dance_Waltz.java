package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Dance_Waltz extends Dance
{
	@Override public String ID() { return "Dance_Waltz"; }
	private final static String localizedName = CMLib.lang().L("Waltz");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	private int[] statadd=null;

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		if(statadd==null)
		{
			statadd=new int[CharStats.CODES.TOTAL()];
			int classLevel=CMLib.ableMapper().qualifyingClassLevel(invoker(),this)+(3*getXLEVELLevel(invoker()));
			classLevel=(classLevel+1)/9;
			classLevel++;

			for(int i=0;i<classLevel;i++)
				statadd[CharStats.CODES.BASECODES()[CMLib.dice().roll(1,CharStats.CODES.BASECODES().length,-1)]]+=3;
		}
		for(final int i: CharStats.CODES.BASECODES())
			affectedStats.setStat(i,affectedStats.getStat(i)+statadd[i]);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		statadd=null;
		return super.invoke(mob,commands,givenTarget,auto,asLevel);
	}

}
