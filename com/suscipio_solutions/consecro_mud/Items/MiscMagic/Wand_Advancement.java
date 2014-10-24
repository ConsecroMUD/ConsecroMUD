package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Wand_Advancement extends StdWand implements ImmortalOnly
{
	@Override public String ID(){	return "Wand_Advancement";}
	public Wand_Advancement()
	{
		super();

		setName("a platinum wand");
		setDisplayText("a platinum wand is here.");
		setDescription("A wand made out of platinum");
		secretIdentity="The wand of Advancement.  Hold the wand say `level up` to it.";
		this.setUsesRemaining(50);
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=20000;
		recoverPhyStats();
		secretWord="LEVEL UP";
	}


	@Override
	public void setSpell(Ability theSpell)
	{
		super.setSpell(theSpell);
		secretWord="LEVEL UP";
	}
	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		secretWord="LEVEL UP";
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=msg.source();
		switch(msg.sourceMinor())
		{
		case CMMsg.TYP_WAND_USE:
			if((mob.isMine(this))
			   &&(!amWearingAt(Wearable.IN_INVENTORY))
			   &&(msg.target() instanceof MOB)
			   &&(mob.location().isInhabitant((MOB)msg.target())))
			{
				final MOB target=(MOB)msg.target();
				final int x=msg.targetMessage().toUpperCase().indexOf("LEVEL UP");
				if((!mob.isMonster())
				&&(x>=0)
				&&(mob.session().getPreviousCMD()!=null)
				&&(CMParms.combine(mob.session().getPreviousCMD(),0).toUpperCase().indexOf("LEVEL UP")<0))
					mob.tell(L("The wand fizzles in an irritating way."));
				else
				if(x>=0)
				{
					if((usesRemaining()>0)&&(useTheWand(CMClass.getAbility("Falling"),mob,0)))
					{
						this.setUsesRemaining(this.usesRemaining()-1);
						final CMMsg msg2=CMClass.getMsg(mob,msg.target(),null,CMMsg.MSG_HANDS,CMMsg.MSG_OK_ACTION,CMMsg.MSG_OK_ACTION,L("<S-NAME> point(s) @x1 at <T-NAMESELF>, who begins to glow softly.",this.name()));
						if(mob.location().okMessage(mob,msg2))
						{
							mob.location().send(mob,msg2);
							if((target.charStats().getCurrentClass().leveless())
							||(target.charStats().isLevelCapped(target.charStats().getCurrentClass()))
							||(target.charStats().getMyRace().leveless())
							||(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
								mob.tell(L("The wand will not work on such as @x1.",target.name(mob)));
							else
							if((target.getExpNeededLevel()==Integer.MAX_VALUE)
							||(target.charStats().getCurrentClass().expless())
							||(target.charStats().getMyRace().expless()))
								CMLib.leveler().level(target);
							else
								CMLib.leveler().postExperience(target,null,null,target.getExpNeededLevel()+1,false);
						}

					}
				}
			}
			return;
		default:
			break;
		}
	}
}
