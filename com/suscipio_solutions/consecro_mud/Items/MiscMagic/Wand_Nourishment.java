package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Wand_Nourishment extends StdWand
{
	@Override public String ID(){	return "Wand_Nourishment";}
	public Wand_Nourishment()
	{
		super();

		setName("a wooden wand");
		setDisplayText("a small wooden wand is here.");
		setDescription("A wand made out of wood");
		secretIdentity="The wand of nourishment.  Hold the wand say \\`shazam\\` to it.";
		baseGoldValue=200;
		material=RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
		secretWord="SHAZAM";
	}


	@Override
	public void setSpell(Ability theSpell)
	{
		super.setSpell(theSpell);
		secretWord="SHAZAM";
	}
	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		secretWord="SHAZAM";
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_WAND_USE:
				if((mob.isMine(this))
				&&(!amWearingAt(Wearable.IN_INVENTORY))
				&&(msg.targetMessage()!=null))
					if(msg.targetMessage().toUpperCase().indexOf("SHAZAM")>=0)
						if(mob.curState().adjHunger(50,mob.maxState().maxHunger(mob.baseWeight())))
							mob.tell(L("You are full."));
						else
							mob.tell(L("You feel nourished."));
				return;
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
