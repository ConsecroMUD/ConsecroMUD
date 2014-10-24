package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class StdClanCard extends StdClanItem
{
	@Override public String ID(){ return "StdClanCard";}

	public StdClanCard()
	{
		super();

		setName("a clan membership card");
		basePhyStats.setWeight(1);
		setDisplayText("a membership card belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		setCIType(ClanItem.CI_ANTIPROPAGANDA);
		material=RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((msg.target()==owner())
		&&(msg.tool() instanceof ClanItem)
		&&(owner() instanceof MOB)
		&&(((MOB)owner()).isMonster())
		&&(((ClanItem)msg.tool()).ciType()==ClanItem.CI_PROPAGANDA)
		&&(!((ClanItem)msg.tool()).clanID().equals(clanID()))
		&&(CMLib.flags().isInTheGame((MOB)owner(),true))
		&&(msg.source()!=owner())
		&&(CMLib.flags().isInTheGame(msg.source(),true)))
		{
			if(msg.source().location().show((MOB)msg.target(),msg.source(),msg.tool(),CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> reject(s) <O-NAME> from <T-NAME>.")))
			{
				CMLib.commands().postSay((MOB)msg.target(),msg.source(),L("How dare you!  Give me those!"),false,true);
				if(msg.source().location().show((MOB)msg.target(),msg.source(),null,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> takes(s) @x1 away from <T-NAME> and destroys it!",msg.tool().name())))
				{
					Item I=null;
					for(int i=msg.source().numItems();i>=0;i--)
					{
						I=msg.source().getItem(i);
						if((I instanceof ClanItem)
						&&(I!=msg.tool())
						&&(((ClanItem)I).clanID().equals(((ClanItem)msg.tool()).clanID())))
							I.destroy();
					}
				}
				return false;
			}

		}
		return super.okMessage(host,msg);
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
			if(((MOB)owner()).getClanRole(clanID())==null)
			{
				final Room R=((MOB)owner()).location();
				final LegalBehavior B=CMLib.law().getLegalBehavior(R);
				if(B!=null)
				{
					final String rulingClan=B.rulingOrganization();
					if((rulingClan!=null)&&(rulingClan.length()>0)
					&&(rulingClan.equals(clanID())))
					{
						int roleID=0;
						final Clan C=CMLib.clans().getClan(clanID());
						if(C!=null) roleID=C.getGovernment().getAutoRole();
						((MOB)owner()).setClan(clanID(),roleID);
					}
				}
			}

		}
		return true;
	}
}
