package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Prop_Climbable extends Property
{
	@Override public String ID() { return "Prop_Climbable"; }
	@Override public String name(){ return "Room/Exit navigation limitation";}
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ROOMS|Ability.CAN_ITEMS;}
	@Override
	public String accountForYourself()
	{ 
		return "Must be climbed through.";	
	}

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_CLIMBING);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof Room)||(affected instanceof Exit))
		{
			final Room R=guessRoom(myHost);
			
			if(CMLib.flags().isSleeping(affected)||(R==null)||CMLib.flags().isSleeping(R)) 
				return super.okMessage(myHost, msg);
			
			if((msg.amITarget(affected)||(msg.tool()==affected))
			&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MOVE))
			&&(msg.sourceMinor()!=CMMsg.TYP_RECALL)
			&&((msg.targetMinor()==CMMsg.TYP_ENTER)||(!(msg.tool() instanceof Ability))||(!CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING)))
			&&(!CMLib.flags().isClimbing(msg.source()))
			&&(!CMLib.flags().isInFlight(msg.source())))
			{
				Rideable ladder=CMLib.tracking().findALadder(msg.source(), R);
				if(ladder == null)
					ladder=CMLib.tracking().findALadder(msg.source(), msg.source().location());
				if(ladder!=null)
					CMLib.tracking().postMountLadder(msg.source(),ladder);
				if((!CMLib.flags().isClimbing(msg.source()))
				&&(!CMLib.flags().isFalling(msg.source())))
				{
					msg.source().tell(L("You need to climb that way, if you know how."));
					return false;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}
	
	protected final Room guessRoom(final Environmental myHost)
	{
		Room R=CMLib.map().roomLocation(affected);
		if(R!=null)
			return R;
		return CMLib.map().roomLocation(myHost);
	}
	
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof Room)||(affected instanceof Exit))
		{
			final Room R=guessRoom(myHost);
			
			if(CMLib.flags().isSleeping(affected)||(R==null)||CMLib.flags().isSleeping(R)) 
				return;
			
			if((msg.sourceMinor()==CMMsg.TYP_THROW)
			&&(CMLib.map().roomLocation(msg.target())==R)
			&&(msg.tool() instanceof Item)
			&&((!(msg.tool() instanceof Rideable))
			   ||(((Rideable)msg.tool()).rideBasis()!=Rideable.RIDEABLE_LADDER))
			&&(!CMLib.flags().isFlying((Item)msg.tool())))
				CMLib.tracking().makeFall((Item)msg.tool(),R,0);
			else
			if((msg.targetMinor()==CMMsg.TYP_DROP)
			&&(msg.target() instanceof Item)
			&&((!(msg.target() instanceof Rideable))
			   ||(((Rideable)msg.target()).rideBasis()!=Rideable.RIDEABLE_LADDER))
			&&(!CMLib.flags().isFlying((Item)msg.target())))
				CMLib.tracking().makeFall((Item)msg.target(),R,0);
			else
			if((msg.amITarget(affected)||(msg.tool()==affected))
			&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MOVE))
			&&(!CMLib.flags().isFalling(msg.source())))
			{
				final MOB mob=msg.source();
				if(R.isInhabitant(mob))
				{
					if((!CMLib.flags().isInFlight(mob))
					&&(!CMLib.flags().isClimbing(mob))
					&&(R.getRoomInDir(Directions.DOWN)!=null)
					&&(R.getExitInDir(Directions.DOWN)!=null)
					&&(R.getExitInDir(Directions.DOWN).isOpen()))
					{
						Rideable ladder=CMLib.tracking().findALadder(mob,R);
						if(ladder == null)
							ladder=CMLib.tracking().findALadder(mob, mob.location());
						if(ladder!=null)
							CMLib.tracking().postMountLadder(mob,ladder);
						if(!CMLib.flags().isClimbing(mob))
						{
							ladder=CMLib.tracking().findALadder(mob,R.getRoomInDir(Directions.DOWN));
							if(ladder!=null)
							{
								CMLib.commands().postLook(mob,false);
								CMLib.tracking().postMountLadder(mob,ladder);
							}
							if(CMLib.flags().isClimbing(mob))
								CMLib.tracking().walk(mob,Directions.DOWN,false,true);
							else
								CMLib.tracking().makeFall(mob,R,0);
						}
					}
				}
			}
		}
	}
}
