package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdJournal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class ImmortalJournal extends StdJournal implements ImmortalOnly, MiscMagic
{
	@Override public String ID(){	return "ImmortalJournal";}
	public ImmortalJournal()
	{
		super();

		setName("");
		setName("the Immortal Journal");
		setDisplayText("a fabulous tome of wisdom has been left here");
		setDescription("");
		secretIdentity="The Immortal's Journal.  Just READ me.";
		baseGoldValue=20000;
		material=RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		final MOB mob=msg.source();
		if(mob.location()==null)
			return true;

		if(msg.amITarget(this))
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_HOLD:
		case CMMsg.TYP_WEAR:
		case CMMsg.TYP_WIELD:
		case CMMsg.TYP_GET:
		case CMMsg.TYP_PUSH:
		case CMMsg.TYP_PULL:
			if(!CMSecurity.isASysOp(msg.source()))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("@x1 flashes and falls out of <S-HIS-HER> hands!",name()));
				return false;
			}
			break;
		}
		return true;
	}

}
