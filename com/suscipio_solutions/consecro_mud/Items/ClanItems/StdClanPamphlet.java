package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class StdClanPamphlet extends StdClanItem
{
	@Override public String ID(){	return "StdClanPamphlet";}
	protected int tradeTime=-1;

	public StdClanPamphlet()
	{
		super();

		setName("a clan pamphlet");
		basePhyStats.setWeight(1);
		setDisplayText("a pamphlet belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		setCIType(ClanItem.CI_PROPAGANDA);
		material=RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_CLANITEM)
		&&(owner() instanceof MOB)
		&&(clanID().length()>0)
		&&(((MOB)owner()).isMonster())
		&&(!CMLib.flags().isAnimalIntelligence((MOB)owner()))
		&&(((MOB)owner()).getStartRoom()!=null)
		&&(((MOB)owner()).location()!=null)
		&&(((MOB)owner()).getStartRoom().getArea()==((MOB)owner()).location().getArea()))
		{
			final Room R=((MOB)owner()).location();
			if((((MOB)owner()).getClanRole(clanID())!=null)
			||(((--tradeTime)<=0)))
			{
				final LegalBehavior B=CMLib.law().getLegalBehavior(R);
				if(B!=null)
				{
					final String rulingClan=B.rulingOrganization();
					if((rulingClan!=null)&&(rulingClan.length()>0)
					&&(!rulingClan.equals(clanID()))
					&&(((MOB)owner()).getClanRole(rulingClan)!=null))
						((MOB)owner()).setClan(rulingClan,-1);
					if(tradeTime<=0)
					{
						final MOB mob=(MOB)owner();
						if((rulingClan!=null)
						&&(rulingClan.length()>0)
						&&(!rulingClan.equals(clanID()))
						&&(mob.getClanRole(rulingClan)==null)
						&&(mob.getClanRole(clanID())==null)
						&&(CMLib.flags().canSpeak(mob))
						&&(CMLib.flags().aliveAwakeMobileUnbound(mob,true))
						&&(R!=null))
						{
							final MOB M=R.fetchRandomInhabitant();
							if((M!=null)
							&&(M!=mob)
							&&(M.isMonster())
							&&(M.getClanRole(rulingClan)!=null)
							&&(!CMLib.flags().isAnimalIntelligence(M))
							&&(CMLib.flags().canBeSeenBy(M,mob))
							&&(CMLib.flags().canBeHeardMovingBy(M,mob)))
							{
								CMLib.commands().postSay(mob,M,L("Hey, take a look at this."),false,false);
								final ClanItem I=(ClanItem)copyOf();
								mob.addItem(I);
								final CMMsg newMsg=CMClass.getMsg(mob,M,I,CMMsg.MSG_GIVE,L("<S-NAME> give(s) <O-NAME> to <T-NAMESELF>."));
								if(mob.location().okMessage(mob,newMsg)&&(!((Item)I).amDestroyed()))
									mob.location().send(mob,newMsg);
								if(!M.isMine(I))
									((Item)I).destroy();
								else
								if(mob.isMine(I))
									((Item)I).destroy();
							}
						}
					}
				}
				if(tradeTime<=0)
					tradeTime=CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
			}
		}
		return true;
	}
}
