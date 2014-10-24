package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class CorpseEater extends ActiveTicker
{
	@Override public String ID(){return "CorpseEater";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	private boolean EatItems=false;
	public CorpseEater()
	{
		super();
		minTicks=5; maxTicks=20; chance=75;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "corpse eating";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		EatItems=(newParms.toUpperCase().indexOf("EATITEMS") > 0);
	}


	public static MOB makeMOBfromCorpse(DeadBody corpse, String type)
	{
		if((type==null)||(type.length()==0))
			type="StdMOB";
		final MOB mob=CMClass.getMOB(type);
		if(corpse!=null)
		{
			mob.setName(corpse.name());
			mob.setDisplayText(corpse.displayText());
			mob.setDescription(corpse.description());
			mob.setBaseCharStats((CharStats)corpse.charStats().copyOf());
			mob.setBasePhyStats((PhyStats)corpse.basePhyStats().copyOf());
			mob.recoverCharStats();
			mob.recoverPhyStats();
			final int level=mob.basePhyStats().level();
			mob.baseState().setHitPoints(CMLib.dice().rollHP(level,mob.basePhyStats().ability()));
			mob.baseState().setMana(CMLib.leveler().getLevelMana(mob));
			mob.baseState().setMovement(CMLib.leveler().getLevelMove(mob));
			mob.recoverMaxState();
			mob.resetToMaxState();
			mob.baseCharStats().getMyRace().startRacing(mob,false);
		}
		return mob;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			final MOB mob=(MOB)ticking;
			final Room thisRoom=mob.location();
			if(thisRoom.numItems()==0) return true;
			for(int i=0;i<thisRoom.numItems();i++)
			{
				final Item I=thisRoom.getItem(i);
				if((I instanceof DeadBody)
				&&(CMLib.flags().canBeSeenBy(I,mob)||CMLib.flags().canSmell(mob)))
				{
					if(getParms().length()>0)
					{
						if(((DeadBody)I).playerCorpse())
						{
							if(getParms().toUpperCase().indexOf("+PLAYER")<0)
								continue;
						}
						else
						if((getParms().toUpperCase().indexOf("-NPC")>=0)
						||(getParms().toUpperCase().indexOf("-MOB")>=0))
							continue;
						final MOB mob2=makeMOBfromCorpse((DeadBody)I,null);
						if(!CMLib.masking().maskCheck(getParms(),mob2,false))
						{
							mob2.destroy();
							continue;
						}
						mob2.destroy();
					}
					else
					if(((DeadBody)I).playerCorpse())
						continue;

					if((I instanceof Container)&&(!EatItems))
						((Container)I).emptyPlease(false);
					thisRoom.show(mob,null,I,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> eat(s) <O-NAME>."));
					I.destroy();
					return true;
				}
			}
		}
		return true;
	}
}
