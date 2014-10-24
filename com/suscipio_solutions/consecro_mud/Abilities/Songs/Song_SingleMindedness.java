package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Song_SingleMindedness extends Song
{
	@Override public String ID() { return "Song_SingleMindedness"; }
	private final static String localizedName = CMLib.lang().L("Single Mindedness");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	protected CMMsg themsg=null;
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	public void executeMsg(Environmental ticking, CMMsg msg)
	{
		super.executeMsg(ticking,msg);
		if((themsg==null)
		&&(msg.source()!=invoker())
		&&(msg.source()==affected)
		&&(msg.sourceMessage()!=null)
		&&(msg.sourceMessage().length()>0)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS)))
			themsg=msg;
	}

	@Override
	public boolean okMessage(Environmental ticking, CMMsg msg)
	{
		if((themsg!=null)
		&&(msg.source()!=invoker())
		&&(msg.source()==affected)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&(themsg.sourceMinor()!=msg.sourceMinor()))
		{
			msg.source().tell(msg.source(),null,null,L("The only thing you have a mind to do is '@x1'.",themsg.sourceMessage()));
			return false;
		}
		return super.okMessage(ticking,msg);
	}
}
