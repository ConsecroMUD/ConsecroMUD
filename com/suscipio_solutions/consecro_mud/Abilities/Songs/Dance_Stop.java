package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Dance_Stop extends Dance
{
	@Override public String ID() { return "Dance_Stop"; }
	private final static String localizedName = CMLib.lang().L("Stop");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	protected boolean skipStandardSongInvoke(){return true;}
	public Dance_Stop()
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
			if((A!=null)&&(A instanceof Dance))
				foundOne=true;
		}
		undanceAll(mob,null);
		if(!foundOne)
		{
			mob.tell(auto?L("There is no dance going."):L("You aren't dancing."));
			return true;
		}

		mob.location().show(mob,null,CMMsg.MSG_NOISE,auto?L("Rest."):L("<S-NAME> stop(s) dancing."));
		mob.location().recoverRoomStats();
		return true;
	}
}
