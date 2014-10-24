package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Song_Serenity extends Song
{
	@Override public String ID() { return "Song_Serenity"; }
	private final static String localizedName = CMLib.lang().L("Serenity");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}
	@Override protected boolean maliciousButNotAggressiveFlag(){return true;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return super.okMessage(myHost,msg);
		if(!(affected instanceof MOB)) return super.okMessage(myHost,msg);
		if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(CMLib.flags().canBeHeardSpeakingBy(invoker,msg.source()))
		&&(msg.target()!=null))
		{
			msg.source().makePeace();
			msg.source().tell(L("You feel too peaceful to fight."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
