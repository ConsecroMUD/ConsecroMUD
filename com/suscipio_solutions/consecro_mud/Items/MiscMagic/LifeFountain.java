package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.Hashtable;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdDrink;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class LifeFountain extends StdDrink implements MiscMagic
{
	@Override public String ID(){	return "LifeFountain";}

	private final Hashtable lastDrinks=new Hashtable();

	public LifeFountain()
	{
		super();
		setName("a fountain");
		amountOfThirstQuenched=250;
		amountOfLiquidHeld=999999;
		amountOfLiquidRemaining=999999;
		basePhyStats().setWeight(5);
		capacity=0;
		setDisplayText("a fountain of life flows here.");
		setDescription("The fountain is coming magically from the ground.  The water looks pure and clean.");
		baseGoldValue=10;
		material=RawMaterial.RESOURCE_FRESHWATER;
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_DRINK:
				if((msg.othersMessage()==null)
				&&(msg.sourceMessage()==null))
					return true;
				break;
			case CMMsg.TYP_FILL:
				mob.tell(L("You can't fill the fountain of life."));
				return false;
			default:
				break;
			}
		}
		return super.okMessage(myHost,msg);
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_DRINK:
				if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
				{
					Long time=(Long)lastDrinks.get(msg.source());
					if((time==null)||(time.longValue()<(System.currentTimeMillis()-16000)))
					{
						Ability A=CMClass.getAbility("Prayer_CureLight");
						if(A!=null) A.invoke(msg.source(),msg.source(),true,phyStats().level());
						A=CMClass.getAbility("Prayer_RemovePoison");
						if(A!=null) A.invoke(msg.source(),msg.source(),true,phyStats().level());
						A=CMClass.getAbility("Prayer_CureDisease");
						if(A!=null) A.invoke(msg.source(),msg.source(),true,phyStats().level());
						time=Long.valueOf(System.currentTimeMillis());
						lastDrinks.put(msg.source(),time);
					}
				}
				else
				{
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
					super.executeMsg(myHost,msg);
				}
				break;
			default:
				super.executeMsg(myHost,msg);
				break;
			}
		}
		else
			super.executeMsg(myHost,msg);
	}

}
