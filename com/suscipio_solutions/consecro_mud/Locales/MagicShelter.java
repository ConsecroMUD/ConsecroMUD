package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;

public class MagicShelter extends StdRoom
{
	@Override public String ID(){return "MagicShelter";}
	public MagicShelter()
	{
		super();
		name="the shelter";
		displayText=L("Magic Shelter");
		setDescription("You are in a domain of complete void and peace.");
		basePhyStats.setWeight(0);
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
	@Override public int domainType(){return Room.DOMAIN_INDOORS_MAGIC;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(CMLib.flags().isSleeping(this))
			return true;
		if((msg.sourceMinor()==CMMsg.TYP_RECALL)
		||(msg.sourceMinor()==CMMsg.TYP_LEAVE))
		{
			msg.source().tell(L("You can't leave the shelter that way.  You'll have to revoke it."));
			return false;
		}
		return true;
	}
}
