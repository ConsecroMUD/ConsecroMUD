package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class StdClanApron extends StdClanItem
{
	@Override public String ID(){ return "StdClanApron";}

	public StdClanApron()
	{
		super();

		setName("a clan apron");
		basePhyStats.setWeight(1);
		setDisplayText("an apron belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		setCIType(ClanItem.CI_SPECIALAPRON);
		material=RawMaterial.RESOURCE_COTTON;
		setRawProperLocationBitmap(Wearable.WORN_WAIST|Wearable.WORN_ABOUT_BODY);
		setRawLogicalAnd(false);
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(Environmental affecting, CMMsg msg)
	{
		if(owner() instanceof MOB)
		if(msg.amITarget(owner()))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_VALUE:
			case CMMsg.TYP_SELL:
			case CMMsg.TYP_BUY:
			case CMMsg.TYP_BID:
			case CMMsg.TYP_VIEW:
			case CMMsg.TYP_LIST:
				if((clanID().length()>0)
				&&(msg.source()!=owner())
				&&(msg.source().getClanRole(clanID())==null))
				{
					final Clan C=CMLib.clans().getClan(clanID());
					if(C!=null)
					{
						int state=Clan.REL_NEUTRAL;
						for(final Pair<Clan,Integer> p : CMLib.clans().findRivalrousClans(msg.source()))
						{
							state=C.getClanRelations(p.first.clanID());
							if((state!=Clan.REL_NEUTRAL)
							&&(state!=Clan.REL_ALLY)
							&&(state!=Clan.REL_FRIENDLY))
							{
								msg.source().tell(((MOB)owner()),null,null,L("<S-NAME> seem(s) to be ignoring you."));
								return false;
							}
						}
					}
				}
				break;
			}
		}
		return super.okMessage(affecting,msg);
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(fetchEffect("Merchant")==null)
		{
			final Ability A=CMClass.getAbility("Merchant");
			if(A!=null) addNonUninvokableEffect(A);
		}
		return true;
	}
}
