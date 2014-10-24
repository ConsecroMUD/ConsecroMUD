package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_RoomUnmappable extends Property
{
	@Override public String ID() { return "Prop_RoomUnmappable"; }
	@Override public String name(){ return "Unmappable Room/Area";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	private int bitStream=PhyStats.SENSE_ROOMUNMAPPABLE;
	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		bitStream=0;
		if(!CMParms.parse(newText.toUpperCase().trim()).contains("MAPOK"))
			bitStream=PhyStats.SENSE_ROOMUNMAPPABLE;
		if(CMParms.parse(newText.toUpperCase().trim()).contains("NOEXPLORE"))
			bitStream=bitStream|PhyStats.SENSE_ROOMUNEXPLORABLE;
	}
	@Override
	public String accountForYourself()
	{ return "Unmappable";    }


	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setSensesMask(affectableStats.sensesMask()|bitStream);
	}
}
