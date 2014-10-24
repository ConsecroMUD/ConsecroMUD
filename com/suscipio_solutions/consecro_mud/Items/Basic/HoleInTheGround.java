package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Decayable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class HoleInTheGround extends StdContainer
{
	@Override public String ID(){	return "HoleInTheGround";}
	public HoleInTheGround()
	{
		super();
		setName("a hole in the ground");
		setDisplayText("a hole in the ground");
		setDescription("Looks like someone has dug hole here.  Perhaps something is in it?");
		capacity=0;
		baseGoldValue=0;
		basePhyStats().setWeight(0);
		basePhyStats().setSensesMask(basePhyStats.sensesMask()
									|PhyStats.SENSE_ITEMNOTGET
									|PhyStats.SENSE_ITEMNOWISH
									|PhyStats.SENSE_ITEMNORUIN
									|PhyStats.SENSE_UNLOCATABLE);
		basePhyStats.setDisposition(basePhyStats.disposition()
									|PhyStats.IS_UNSAVABLE
									|PhyStats.IS_NOT_SEEN);
		setMaterial(RawMaterial.RESOURCE_DUST);
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(owner()))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_LEAVE:
			case CMMsg.TYP_RECALL:
				if((owner() instanceof Room)
				&&(((Room)owner()).numPCInhabitants()==0))
				{
					if(getContents().size()==0)
					{
						destroy();
						return true;
					}
					else
					{
						basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_HIDDEN);
						recoverPhyStats();
					}
				}
				break;
			case CMMsg.TYP_EXPIRE:
				if(getContents().size()>0)
				{
					return false;
				}
				break;
			}
		}
		else
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_CLOSE:
				msg.setSourceMessage("<S-NAME> fill(s) the hole back in.");
				msg.setOthersMessage("<S-NAME> fill(s) the hole back in.");
				return true;
			case CMMsg.TYP_PUT:
				if((msg.tool()!=null)&&(msg.tool() instanceof Item))
				{
					if((text().length()>0)&&(!text().equals(msg.source().Name())))
					{
						msg.source().tell(L("Go find your own hole."));
						return false;
					}
				}
				if((msg.tool()!=null)&&(msg.tool() instanceof ClanItem))
				{
					msg.source().tell(L("Go may not bury a clan item."));
					return false;
				}
				break;
			}
		}
		return super.okMessage(myHost, msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.target()==owner())
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_DIG:
				if(CMath.bset(basePhyStats().disposition(), PhyStats.IS_NOT_SEEN)
				||CMath.bset(basePhyStats().disposition(), PhyStats.IS_HIDDEN))
				{
					basePhyStats().setDisposition(CMath.unsetb(basePhyStats().disposition(), PhyStats.IS_NOT_SEEN));
					basePhyStats().setDisposition(CMath.unsetb(basePhyStats().disposition(), PhyStats.IS_HIDDEN));
					recoverPhyStats();
				}
				setCapacity(capacity()+msg.value());
				break;
			}
		}
		else
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_CLOSE:
				if(getContents().size()==0)
					destroy();
				else
				{
					basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
					setCapacity(0);
					recoverPhyStats();
				}
				return;
			case CMMsg.TYP_PUT:
				if((msg.tool()!=null)&&(msg.tool() instanceof Item))
				{
					final PlayerStats pstats=mob.playerStats();
					if(pstats!=null)
					{
						if(text().length()==0)
							setMiscText(mob.Name());
						if(!pstats.getExtItems().isContent(this))
							pstats.getExtItems().addItem(this);
						if(msg.tool() instanceof Decayable)
							((Decayable)msg.tool()).setDecayTime(((Decayable)msg.tool()).decayTime()/2);
						((Item)msg.tool()).setExpirationDate(0);
					}
				}
				break;
			}
		}
		super.executeMsg(myHost, msg);
	}
}
