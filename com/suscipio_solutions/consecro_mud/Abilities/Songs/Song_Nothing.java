package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Song_Nothing extends Song
{
	@Override public String ID() { return "Song_Nothing"; }
	private final static String localizedName = CMLib.lang().L("Nothing");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected boolean skipStandardSongInvoke(){return true;}
	public Song_Nothing()
	{
		super();
		setProficiency(100);
	}
	@Override public void setProficiency(int newProficiency){	super.setProficiency(100);}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		boolean foundOne=false;
		for(int a=0;a<mob.numEffects();a++)
		{
			final Ability A=mob.fetchEffect(a);
			if((A!=null)&&(A instanceof Song))
				foundOne=true;
		}
		unsingAllByThis(mob,mob);
		if(!foundOne)
		{
			mob.tell(auto?L("There is no song playing."):L("You aren't singing."));
			return true;
		}

		mob.location().show(mob,null,CMMsg.MSG_NOISE,auto?L("Silence."):L("<S-NAME> stop(s) singing."));
		mob.location().recoverRoomStats();
		return true;
	}
}
