package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



@SuppressWarnings("rawtypes")
public class ManualClasses extends StdItem implements MiscMagic,ImmortalOnly
{
	@Override public String ID(){	return "ManualClasses";}
	public ManualClasses()
	{
		super();

		setName("a book");
		basePhyStats.setWeight(1);
		setDisplayText("an roughly treated book sits here.");
		setDescription("An roughly treated book filled with mystical symbols.");
		secretIdentity="The Manual of Classes.";
		this.setUsesRemaining(Integer.MAX_VALUE);
		baseGoldValue=5000;
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
							CharClass lastC=null;
							CharClass thisC=null;
							for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
							{
								final CharClass C=(CharClass)c.nextElement();
								if(thisC==null) thisC=C;
								if((lastC!=null)&&(thisC==mob.charStats().getCurrentClass()))
								{
									thisC=C;
									break;
								}
								lastC=C;
							}
							if((thisC!=null)&&(!(thisC.ID().equals("Immortal"))))
							{
								mob.charStats().setCurrentClass(thisC);
								if((!mob.isMonster())&&(mob.soulMate()==null))
									CMLib.coffeeTables().bump(mob,CMTableRow.STAT_CLASSCHANGE);
								mob.location().showOthers(mob,null,CMMsg.MSG_OK_ACTION,L("@x1 undergoes a traumatic change.",mob.name()));
								mob.tell(L("You are now a @x1.",thisC.name(mob.charStats().getClassLevel(thisC))));
							}
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
