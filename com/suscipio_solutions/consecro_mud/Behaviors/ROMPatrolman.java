package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class ROMPatrolman extends StdBehavior
{
	@Override public String ID(){return "ROMPatrolman";}
	int tickTock=0;

	@Override
	public String accountForYourself()
	{
		return "gang member passifying";
	}

	public void keepPeace(MOB observer)
	{
		if(!canFreelyBehaveNormal(observer)) return;
		MOB victim=null;
		for(int i=0;i<observer.location().numInhabitants();i++)
		{
			final MOB inhab=observer.location().fetchInhabitant(i);
			if((inhab!=null)&&(inhab.isInCombat()))
			{
				if(inhab.phyStats().level()>inhab.getVictim().phyStats().level())
					victim=inhab;
				else
					victim=inhab.getVictim();
			}
		}


		if(victim==null) return;
		if(BrotherHelper.isBrother(victim,observer,false)) return;
		observer.location().show(observer,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> blow(s) down hard on <S-HIS-HER> whistle. ***WHEEEEEEEEEEEET***"));
		for(final Enumeration r=observer.location().getArea().getMetroMap();r.hasMoreElements();)
		{
			final Room R=(Room)r.nextElement();
			if((R!=observer.location())&&(R.numPCInhabitants()>0))
				R.showHappens(CMMsg.MSG_NOISE,L("You hear a shrill whistling sound in the distance."));
		}

		Item weapon=observer.fetchWieldedItem();
		if(weapon==null) weapon=observer.myNaturalWeapon();
		boolean makePeace=false;
		boolean fight=false;
		switch(CMLib.dice().roll(1,7,-1))
		{
		case 0:
			observer.location().show(observer,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> yell(s) 'All roit! All roit! break it up!'^?"));
			makePeace=true;
			break;
		case 1:
			observer.location().show(observer,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> sigh(s) 'Society's to blame, but what's a bloke to do?'^?"));
			fight=true;
			break;
		case 2:
			observer.location().show(observer,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> mumble(s) 'bloody kids will be the death of us all.'^?"));
			break;
		case 3:
			observer.location().show(observer,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> yell(s) 'Stop that! Stop that!' and attack(s).^?"));
			fight=true;
			break;
		case 4:
			observer.location().show(observer,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> pull(s) out his billy and go(es) to work."));
			fight=true;
			break;
		case 5:
			observer.location().show(observer,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> sigh(s) in resignation and proceed(s) to break up the fight."));
			makePeace=true;
			break;
		case 6:
			observer.location().show(observer,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> say(s) 'Settle down, you hooligans!'^?"));
			break;
		 }

		if(makePeace)
		{
			final Room room=observer.location();
			for(int i=0;i<room.numInhabitants();i++)
			{
				final MOB inhab=room.fetchInhabitant(i);
				if((inhab!=null)
				&&(inhab.isInCombat())
				&&(inhab.getVictim().isInCombat())
				&&((observer.phyStats().level()>(inhab.phyStats().level()+5))
				&&(!CMLib.flags().isEvil(observer))))
				{
					final String msg="<S-NAME> stop(s) <T-NAME> from fighting with "+inhab.getVictim().name();
					final CMMsg msgs=CMClass.getMsg(observer,inhab,CMMsg.MSG_NOISYMOVEMENT,msg);
					if(observer.location().okMessage(observer,msgs))
					{
						final MOB ivictim=inhab.getVictim();
						if(ivictim!=null) ivictim.makePeace();
					}
				}
			}
		}
		else
		if(fight)
			CMLib.combat().postAttack(observer,victim,weapon);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		final MOB mob=(MOB)ticking;
		tickTock--;
		if(tickTock<=0)
		{
			tickTock=CMLib.dice().roll(1,3,0);
			keepPeace(mob);
		}
		return true;
	}
}
