package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class MagicFreeRoom extends StdRoom
{
	@Override public String ID(){return "MagicFreeRoom";}
	public MagicFreeRoom()
	{
		super();
		basePhyStats.setWeight(1);
		recoverPhyStats();
		addEffect(CMClass.getAbility("Prop_MagicFreedom"));
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_STONE;}
}
