package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class IndoorInTheAir extends StdRoom
{
	@Override public String ID(){return "IndoorInTheAir";}
	public IndoorInTheAir()
	{
		super();
		name="the space";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_AIR;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		return InTheAir.isOkAirAffect(this,msg);
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		InTheAir.airAffects(this,msg);
	}

}
