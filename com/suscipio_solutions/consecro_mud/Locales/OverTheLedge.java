package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class OverTheLedge extends InTheAir
{
	@Override public String ID(){return "OverTheLedge";}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(CMLib.flags().isSleeping(this))
			return true;

		if((msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.amITarget(this))
		&&((getRoomInDir(Directions.DOWN)!=msg.source().location())))
			return true;
		return super.okMessage(myHost,msg);
	}

}
