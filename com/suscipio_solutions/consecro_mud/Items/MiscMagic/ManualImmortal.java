package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class ManualImmortal extends StdItem implements MiscMagic,ImmortalOnly
{
	@Override public String ID(){    return "ManualImmortal";}
	public ManualImmortal()
	{
		super();

		setName("an ornately decorated book");
		basePhyStats.setWeight(1);
		setDisplayText("an ornately decorated book has definitely been left behind by someone.");
		setDescription("A book covered with mystical symbols, inside and out.");
		secretIdentity="The Manual of the Immortals.";
		this.setUsesRemaining(Integer.MAX_VALUE);
		baseGoldValue=50000;
		material=RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_LIGHTSOURCE);
		if(CMLib.flags().isInDark(affected))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_DARK);
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
					mob.tell(L("The manual glows softly, enveloping you in its magical energy."));
					final Session session=mob.session();
					final CharClass newClass=CMClass.getCharClass("Immortal");
					if((session!=null)&&(newClass!=null))
					{
						mob.setSession(null);

						for(final int i : CharStats.CODES.BASECODES())
							mob.baseCharStats().setStat(i,25);
						if((!mob.isMonster())&&(mob.soulMate()==null))
							CMLib.coffeeTables().bump(mob,CMTableRow.STAT_CLASSCHANGE);
						mob.recoverCharStats();
						if((!mob.charStats().getCurrentClass().leveless())
						&&(!mob.charStats().isLevelCapped(mob.charStats().getCurrentClass()))
						&&(!mob.charStats().getMyRace().leveless())
						&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
						while(mob.basePhyStats().level()<100)
						{
							final int oldLevel = mob.basePhyStats().level();
							if((mob.getExpNeededLevel()==Integer.MAX_VALUE)
							||(mob.charStats().getCurrentClass().expless())
							||(mob.charStats().getMyRace().expless()))
								CMLib.leveler().level(mob);
							else
								CMLib.leveler().postExperience(mob,null,null,mob.getExpNeededLevel()+1,false);
							if(mob.basePhyStats().level()==oldLevel)
								break;
						}
						mob.baseCharStats().setCurrentClass(newClass);
						mob.baseCharStats().setClassLevel(mob.baseCharStats().getCurrentClass(),30);
						mob.basePhyStats().setLevel(mob.basePhyStats().level()+30);
						mob.setExperience(mob.getExpNextLevel());
						mob.recoverCharStats();
						mob.recoverPhyStats();
						mob.recoverMaxState();
						mob.resetToMaxState();
						mob.charStats().getCurrentClass().startCharacter(mob,true,false);
						CMLib.utensils().outfit(mob,mob.charStats().getCurrentClass().outfit(mob));
						mob.setSession(session);
						CMLib.database().DBUpdatePlayer(mob);
					}
				}
				mob.tell(L("The book vanishes out of your hands."));
				destroy();
				msg.source().location().recoverRoomStats();
				return;
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}

}
