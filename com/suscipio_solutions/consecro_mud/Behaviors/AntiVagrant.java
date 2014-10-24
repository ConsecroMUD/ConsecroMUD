package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class AntiVagrant extends ActiveTicker
{
	@Override public String ID(){return "AntiVagrant";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	protected int speakDown=3;
	protected MOB target=null;
	protected boolean kickout=false;
	protected boolean anywhere=false;

	public AntiVagrant()
	{
		super();
		minTicks=2; maxTicks=3; chance=99;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "vagrant disliking";
	}

	@Override
	public void setParms(String parms)
	{
		kickout=parms.toUpperCase().indexOf("KICK")>=0;
		anywhere=parms.toUpperCase().indexOf("ANYWHERE")>=0;
		super.setParms(parms);
	}

	public void wakeVagrants(MOB observer)
	{
		if(!canFreelyBehaveNormal(observer)) return;
		if(anywhere||(observer.location().domainType()==Room.DOMAIN_OUTDOORS_CITY))
		{
			if(target!=null)
			if(CMLib.flags().isSleeping(target)&&(target!=observer)&&(CMLib.flags().canBeSeenBy(target,observer)))
			{
				CMLib.commands().postSay(observer,target,L("Damn lazy good for nothing!"),false,false);
				final CMMsg msg=CMClass.getMsg(observer,target,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> shake(s) <T-NAME> awake."));
				if(observer.location().okMessage(observer,msg))
				{
					observer.location().send(observer,msg);
					target.tell(L("@x1 shakes you awake.",observer.name()));
					CMLib.commands().postStand(target,true);
					if((kickout)&&(CMLib.flags().isStanding(target)))
					{
						CMLib.commands().postSay(observer,target,L("Go home @x1!",target.name(observer)),false,false);
						CMLib.tracking().beMobile(target,true,false,false,false,null,null);
					}
				}
			}
			else
			if((CMLib.flags().isSitting(target)&&(target!=observer))&&(CMLib.flags().canBeSeenBy(target,observer)))
			{
				CMLib.commands().postSay(observer,target,L("Get up and move along!"),false,false);
				final CMMsg msg=CMClass.getMsg(observer,target,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> stand(s) <T-NAME> up."));
				if(observer.location().okMessage(observer,msg))
				{
					observer.location().send(observer,msg);
					CMLib.commands().postStand(target,true);
					if((kickout)&&(CMLib.flags().isStanding(target)))
						CMLib.tracking().beMobile(target,true,false,false,false,null,null);
				}
			}
			target=null;
			for(int i=0;i<observer.location().numInhabitants();i++)
			{
				final MOB mob=observer.location().fetchInhabitant(i);
				if((mob!=null)
				&&(mob!=observer)
				&&((CMLib.flags().isSitting(mob))||(CMLib.flags().isSleeping(mob)))
				&&(CMLib.flags().canBeSeenBy(mob,observer)))
				{
				   target=mob;
				   break;
				}
			}
		}
	}


	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		// believe it or not, this is for arrest behavior.
		super.executeMsg(affecting,msg);
		if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
		&&(msg.sourceMessage()!=null)
		&&(msg.sourceMessage().toUpperCase().indexOf("SIT")>=0))
			speakDown=3;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;

		// believe it or not, this is for arrest behavior.
		if(speakDown>0)	{	speakDown--;return true;	}

		if((canFreelyBehaveNormal(ticking))&&(canAct(ticking,tickID)))
		{
			final MOB mob=(MOB)ticking;
			wakeVagrants(mob);
			return true;
		}
		return true;
	}
}
