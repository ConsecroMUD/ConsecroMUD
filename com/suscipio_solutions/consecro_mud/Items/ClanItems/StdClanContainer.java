package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdContainer;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TimeManager;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class StdClanContainer extends StdContainer implements ClanItem
{
	@Override public String ID(){	return "StdClanContainer";}
	private Environmental riteOwner=null;
	@Override public Environmental rightfulOwner(){return riteOwner;}
	@Override public void setRightfulOwner(Environmental E){riteOwner=E;}	protected String myClan="";
	protected int ciType=0;
	private long lastClanCheck=0;
	@Override public int ciType(){return ciType;}
	@Override public void setCIType(int type){ ciType=type;}
	public StdClanContainer()
	{
		super();

		setName("a clan container");
		basePhyStats.setWeight(1);
		setDisplayText("an item belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		capacity=100;
		material=RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
	}

	@Override public String clanID(){return myClan;}
	@Override public void setClanID(String ID){myClan=ID;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((System.currentTimeMillis()-lastClanCheck)>TimeManager.MILI_HOUR)
		{
			if((clanID().length()>0)&&(owner() instanceof MOB)&&(!amDestroyed()))
			{
				if((CMLib.clans().getClan(clanID())==null)
				||((((MOB)owner()).getClanRole(clanID())==null)&&(ciType()!=ClanItem.CI_PROPAGANDA)))
				{
					final Room R=CMLib.map().roomLocation(this);
					setRightfulOwner(null);
					unWear();
					removeFromOwnerContainer();
					if(owner()!=R) R.moveItemTo(this,ItemPossessor.Expire.Player_Drop);
					if(R!=null)
						R.showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 is dropped!",name()));
				}
			}
			lastClanCheck=System.currentTimeMillis();
			if((clanID().length()>0)&&(CMLib.clans().getClan(clanID())==null))
			{
				destroy();
				return;
			}
		}
		if(StdClanItem.stdExecuteMsg(this,msg))
			super.executeMsg(myHost,msg);
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(StdClanItem.stdOkMessage(this,msg))
			return super.okMessage(myHost,msg);
		return false;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!StdClanItem.standardTick(this,tickID))
			return false;
		return super.tick(ticking,tickID);
	}
}
