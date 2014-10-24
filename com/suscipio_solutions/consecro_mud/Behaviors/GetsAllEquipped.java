package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class GetsAllEquipped extends ActiveTicker
{
	@Override public String ID(){return "GetsAllEquipped";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	public GetsAllEquipped()
	{
		super();
		maxTicks=5;minTicks=10;chance=100;
		tickReset();
	}

	protected boolean DoneEquipping=false;

	@Override
	public String accountForYourself()
	{
		return "equipping";
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((msg.sourceMinor()==CMMsg.TYP_DEATH)
		&&(msg.source()!=host)
		&&(msg.source().location()!=CMLib.map().roomLocation(host)))
			DoneEquipping=false;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			if(DoneEquipping)
				return true;

			final MOB mob=(MOB)ticking;
			final Room thisRoom=mob.location();
			if(thisRoom.numItems()==0) return true;

			DoneEquipping=true;
			final Vector stuffIHad=new Vector();
			for(int i=0;i<mob.numItems();i++)
				stuffIHad.addElement(mob.getItem(i));
			mob.enqueCommand(new XVector("GET","ALL"),Command.METAFLAG_FORCED,0);
			Item I=null;
			final Vector dropThisStuff=new Vector();
			for(int i=0;i<mob.numItems();i++)
			{
				I=mob.getItem(i);
				if((I!=null)&&(!stuffIHad.contains(I)))
				{
					if(I instanceof DeadBody)
						dropThisStuff.addElement(I);
					else
					if((I.container()!=null)&&(I.container() instanceof DeadBody))
						I.setContainer(null);
				}
			}
			for(int d=0;d<dropThisStuff.size();d++)
				mob.enqueCommand(new XVector("DROP","$"+((Item)dropThisStuff.elementAt(d)).Name()+"$"),Command.METAFLAG_FORCED,0);
			mob.enqueCommand(new XVector("WEAR","ALL"),Command.METAFLAG_FORCED,0);
		}
		return true;
	}
}
