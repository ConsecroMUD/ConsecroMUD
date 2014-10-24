package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Play_Break extends Play
{
	@Override public String ID() { return "Play_Break"; }
	private final static String localizedName = CMLib.lang().L("Break");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected boolean skipStandardSongInvoke(){return true;}
	public Play_Break()
	{
		super();
		setProficiency(100);
	}
	@Override public void setProficiency(int newProficiency){	super.setProficiency(100);}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		boolean foundOne=false;
		for(int a=0;a<mob.numEffects();a++) // personal affects
		{
			final Ability A=mob.fetchEffect(a);
			if((A!=null)&&(A instanceof Play))
				foundOne=true;
		}
		if(!foundOne)
		{
			mob.tell(auto?L("There is noone playing."):L("You aren't playing anything."));
			return true;
		}
		unplayAll(mob,mob);
		mob.location().show(mob,null,CMMsg.MSG_NOISE,auto?L("Silence."):L("<S-NAME> stop(s) playing."));
		mob.location().recoverRoomStats();
		return true;
	}
}
