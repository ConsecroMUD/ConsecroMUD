package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class StdCageRideable extends StdRideable
{
	@Override public String ID(){	return "StdCageRideable";}
	public StdCageRideable()
	{
		super();
		setName("a cage wagon");
		setDisplayText("a cage wagon sits here.");
		setDescription("It\\`s of solid wood construction with metal bracings.  The door has a key hole.");
		capacity=5000;
		setContainTypes(Container.CONTAIN_BODIES|Container.CONTAIN_CAGED);
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=15;
		basePhyStats().setWeight(1000);
		rideBasis=Rideable.RIDEABLE_WAGON;
		recoverPhyStats();
	}



	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this))
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE)))
		{
			synchronized(this)
			{
				final boolean wasOpen=isOpen;
				isOpen=true;
				CMLib.commands().handleBeingLookedAt(msg);
				isOpen=wasOpen;
			}
			if(behaviors!=null)
				for(final Behavior B : behaviors)
					if(B!=null)
						B.executeMsg(this,msg);

			for(final Enumeration<Ability> a=effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if(A!=null)
					A.executeMsg(this,msg);
			}
			return;
		}
		super.executeMsg(myHost,msg);
	}
}
