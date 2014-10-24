package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class ManualAdvancement extends StdItem implements MiscMagic,ImmortalOnly
{
	@Override public String ID(){	return "ManualAdvancement";}
	public ManualAdvancement()
	{
		super();

		setName("a book");
		basePhyStats().setWeight(1);
		setDisplayText("an ornately bound book sits here.");
		setDescription("An ornately bound book filled with mystical symbols.");
		secretIdentity="The Manual of Advancement.";
		this.setUsesRemaining(5);
		baseGoldValue=10000;
		material=RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_READ:
				if(mob.isMine(this))
				{
					if(mob.fetchEffect("Spell_ReadMagic")!=null)
					{
						if(this.usesRemaining()<=0)
							mob.tell(L("The markings have been read off the parchment, and are no longer discernable."));
						else
						{
							this.setUsesRemaining(this.usesRemaining()-1);
							mob.tell(L("The manual glows softly, enveloping you in its wisdom."));
							if(mob.getExpNeededLevel()==Integer.MAX_VALUE)
								CMLib.leveler().level(mob);
							else
								CMLib.leveler().postExperience(mob,null,null,mob.getExpNeededLevel()+1,false);
						}
					}
					else
						mob.tell(L("The markings look magical, and are unknown to you."));
				}
				return;
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}

}
