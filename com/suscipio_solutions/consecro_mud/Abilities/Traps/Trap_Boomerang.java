package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Trap_Boomerang extends StdTrap
{
	@Override public String ID() { return "Trap_Boomerang"; }
	private final static String localizedName = CMLib.lang().L("boomerang");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 24;}
	@Override public String requiresToSet(){return "";}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		final boolean wasSprung = sprung;
		super.executeMsg(myHost, msg);
		if((!wasSprung)&&(sprung))
		{
			msg.setSourceCode(CMMsg.NO_EFFECT);
			msg.setTargetCode(CMMsg.NO_EFFECT);
			msg.setOthersCode(CMMsg.NO_EFFECT);
		}
	}
	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			final boolean ok=((invoker()!=null)&&(invoker().location()!=null));
			if((!ok)||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> foil(s) a trap on @x1!",affected.name()));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> set(s) off a trap!")))
			{
				if(affected instanceof Item)
				{
					((Item)affected).unWear();
					((Item)affected).removeFromOwnerContainer();
					invoker().addItem((Item)affected);
					invoker().tell(invoker(),affected,null,L("Magically, <T-NAME> appear(s) in your inventory."));
				}
				super.spring(target);
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
