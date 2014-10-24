package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;



public class ClimbableLedge extends ClimbableSurface
{
	@Override public String ID(){return "ClimbableLedge";}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(CMLib.flags().isSleeping(this))
			return super.okMessage(myHost,msg);

		if((msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.amITarget(this)))
		{
			final Rideable ladder=CMLib.tracking().findALadder(msg.source(),this);
			if(ladder!=null)
			{
				msg.source().setRiding(ladder);
				msg.source().recoverPhyStats();
			}
			if((getRoomInDir(Directions.DOWN)!=msg.source().location()))
				return true;
		}
		return super.okMessage(myHost,msg);
	}

}
