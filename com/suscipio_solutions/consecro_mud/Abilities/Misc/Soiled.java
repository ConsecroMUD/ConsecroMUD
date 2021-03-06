package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Soiled extends StdAbility
{
	@Override public String ID() { return "Soiled"; }
	private final static String localizedName = CMLib.lang().L("Soiled");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Soiled)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings =I(new String[] {"SOIL"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)/2);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		final Environmental E=affected;
		if(E==null) return;
		super.unInvoke();
		if(canBeUninvoked())
		{
			if(E instanceof MOB)
			{
				final MOB mob=(MOB)E;
				mob.tell(L("You are no longer soiled."));
				final MOB following=((MOB)E).amFollowing();
				if((following!=null)
				&&(following.location()==mob.location())
				&&(CMLib.flags().isInTheGame(mob,true))
				&&(CMLib.flags().canBeSeenBy(mob,following)))
					following.tell(L("@x1 is no longer soiled.",E.name()));
			}
			else
			if((E instanceof Item)&&(((Item)E).owner() instanceof MOB))
				((MOB)((Item)E).owner()).tell(L("@x1 is no longer soiled.",E.name()));
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(((msg.source()==affected)
		||((affected instanceof Item)
			&&(((Item)affected).owner()==msg.source()))))
		{
			if((msg.sourceMajor(CMMsg.MASK_MOVE))
			&&(msg.source().riding()==null)
			&&(msg.source().location()!=null)
			&&((msg.source().location().domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
				||(msg.source().location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
				||(msg.source().location().domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
				||(msg.source().location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)))
				unInvoke();
			else
			if((msg.sourceMajor(CMMsg.MASK_MOVE))
			&&(msg.source().riding() instanceof Drink)
			&&(((Drink)msg.source().riding()).containsDrink()))
				unInvoke();
			else
			if((affected instanceof Item)
			&&(((Item)affected).container() instanceof Drink)
			&&(msg.target()==affected)
			&&(msg.targetMinor()==CMMsg.TYP_PUT)
			&&(((Drink)((Item)affected).container()).containsDrink()))
				unInvoke();
		}
		if((msg.target()==affected)
		&&(msg.targetMinor()==CMMsg.TYP_SNIFF))
		{
			String smell=null;
			switch(CMLib.dice().roll(1,5,0))
			{
				case 1: smell="<T-NAME> is stinky!"; break;
				case 2: smell="<T-NAME> smells like poo."; break;
				case 3: smell="<T-NAME> has soiled a diaper."; break;
				case 4: smell="Whew! <T-NAME> stinks!"; break;
				case 5: smell="<T-NAME> must have let one go!"; break;
			}
			if((CMLib.flags().canSmell(msg.source()))&&(smell!=null))
				msg.source().tell(msg.source(),affected,null,smell);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(affected!=null)
		if(CMLib.dice().rollPercentage()==1)
		{
			final Environmental E=affected;
			final Room R=CMLib.map().roomLocation(E);
			if(R!=null)
			{
				MOB M=(E instanceof MOB)?(MOB)E:null;
				boolean killmob=false;
				if(M==null)
				{
					M=CMClass.getFactoryMOB();
					M.setName(affected.name());
					M.setDisplayText(L("@x1 is here.",affected.name()));
					M.setDescription("");
					if(M.location()!=R)
						M.setLocation(R);
					killmob=true;
				}
				else
				if((M.playerStats()!=null)&&(M.playerStats().getHygiene()<10000))
				{
					M.playerStats().setHygiene(10000);
					M.recoverCharStats();
				}
				String smell=null;
				switch(CMLib.dice().roll(1,5,0))
				{
				case 1: smell="<S-NAME> <S-IS-ARE> stinky!"; break;
				case 2: smell="<S-NAME> smells like poo."; break;
				case 3: smell="<S-NAME> has soiled a diaper."; break;
				case 4: smell="Whew! <S-NAME> stinks!"; break;
				case 5: smell="<S-NAME> must have let one go!"; break;
				}
				if((smell!=null)
				&&(CMLib.flags().isInTheGame(M,true)))
				{
					final CMMsg msg=CMClass.getMsg(M,null,null,CMMsg.TYP_EMOTE|CMMsg.MASK_ALWAYS,smell);
					if(R.okMessage(M,msg))
					for(int m=0;m<R.numInhabitants();m++)
					{
						final MOB mob=R.fetchInhabitant(m);
						if(CMLib.flags().canSmell(mob))
							mob.executeMsg(M,msg);
					}
				}
				if(killmob) M.destroy();
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if((target==null)||(target.fetchEffect(ID())!=null))
			return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		Ability A=(Ability)copyOf();
		A.startTickDown(mob,target,Ability.TICKS_ALMOST_FOREVER);
		Environmental msgTarget=target;
		if(target instanceof CagedAnimal) msgTarget=((CagedAnimal)target).unCageMe();
		mob.location().show(mob,msgTarget,CMMsg.MSG_OK_VISUAL,L("<T-NAME> has soiled <T-HIM-HERSELF>!"));
		if(target instanceof MOB)
		{
			final Item pants=((MOB)target).fetchFirstWornItem(Wearable.WORN_WAIST);
			if((pants!=null)&&(pants.fetchEffect(ID())==null))
			{
				A=(Ability)copyOf();
				A.startTickDown((MOB)target,pants,Ability.TICKS_ALMOST_FOREVER);
			}
		}
		return true;
	}
}
