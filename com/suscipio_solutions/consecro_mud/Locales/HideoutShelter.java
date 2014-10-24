package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;

public class HideoutShelter extends MagicShelter
{
	@Override public String ID(){return "HideoutShelter";}
	public HideoutShelter()
	{
		super();
		name="the hideout";
		displayText=L("Secret Hideout");
		setDescription("You are in a small dark room.");
		basePhyStats.setWeight(0);
		basePhyStats.setDisposition(PhyStats.IS_DARK);
		recoverPhyStats();
		Ability A=CMClass.getAbility("Prop_PeaceMaker");
		if(A!=null)
		{
			A.setSavable(false);
			addEffect(A);
		}
		A=CMClass.getAbility("Prop_NoRecall");
		if(A!=null)
		{
			A.setSavable(false);
			addEffect(A);
		}
		A=CMClass.getAbility("Prop_NoSummon");
		if(A!=null)
		{
			A.setSavable(false);
			addEffect(A);
		}
		A=CMClass.getAbility("Prop_NoTeleport");
		if(A!=null)
		{
			A.setSavable(false);
			addEffect(A);
		}
		A=CMClass.getAbility("Prop_NoTeleportOut");
		if(A!=null)
		{
			A.setSavable(false);
			addEffect(A);
		}
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_WOOD;}
}
