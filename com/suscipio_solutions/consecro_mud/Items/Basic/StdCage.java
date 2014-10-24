package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class StdCage extends StdContainer
{
	@Override public String ID(){	return "StdCage";}
	public StdCage()
	{
		super();
		setName("a cage");
		setDisplayText("a cage sits here.");
		setDescription("It\\`s of solid wood construction with metal bracings.  The door has a key hole.");
		capacity=1000;
		setContainTypes(Container.CONTAIN_BODIES|Container.CONTAIN_CAGED);
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=15;
		basePhyStats().setWeight(25);
		recoverPhyStats();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_EXIT_REOPEN)&&(isOpen()))
		{
			final Room R=CMLib.map().roomLocation(this);
			if((R!=null)&&(owner() instanceof Room)&&(CMLib.flags().isInTheGame(this,true)))
			{
				final List<Item> mobContents=getContents();
				for (final Item item : mobContents)
				{
					final Environmental E=item;
					if(E instanceof CagedAnimal)
					{
						final MOB M=((CagedAnimal)E).unCageMe();
						if(M!=null)
							M.bringToLife(R,true);
						R.show(M,null,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> escapes from <O-NAME>!"));
						E.destroy();
					}
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_CLOSE:
				if((hasALid)&&(isOpen))
				{
					if(CMLib.threads().isTicking(this,Tickable.TICKID_EXIT_REOPEN))
						CMLib.threads().deleteTick(this,Tickable.TICKID_EXIT_REOPEN);
				}
				break;
			case CMMsg.TYP_OPEN:
				if((hasALid)&&(!isOpen)&&(!isLocked))
				{
					if((owner() instanceof Room)
					&&(!CMLib.threads().isTicking(this,Tickable.TICKID_EXIT_REOPEN)))
						CMLib.threads().startTickDown(this,Tickable.TICKID_EXIT_REOPEN,30);
				}
				break;
			case CMMsg.TYP_LOOK: case CMMsg.TYP_EXAMINE:
			{
				synchronized(this)
				{
					if(!isOpen)
					{
						isOpen=true;
						super.executeMsg(myHost,msg);
						isOpen=false;
						return;
					}
				}
				break;
			}
			}
		}
		super.executeMsg(myHost,msg);
	}
}
