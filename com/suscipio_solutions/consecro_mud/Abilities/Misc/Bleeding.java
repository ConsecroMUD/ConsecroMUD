package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.HealthCondition;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Bleeding extends StdAbility implements HealthCondition
{
	@Override public String ID() { return "Bleeding"; }
	private final static String localizedName = CMLib.lang().L("Bleeding");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Bleeding)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ITEMS|Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	protected int hpToKeep=-1;
	protected int lastDir=-1;
	protected Room lastRoom=null;

	public double healthPct(MOB mob){ return CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints());}

	@Override
	public String getHealthConditionDesc()
	{
		return "Bleeding externally and excessively.";
	}

	@Override
	public void unInvoke()
	{
		if((affected instanceof MOB)
		&&(canBeUninvoked())
		&&(!((MOB)affected).amDead())
		&&(CMLib.flags().isInTheGame((MOB)affected,true)))
			((MOB)affected).location().show((MOB)affected,null,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> stop(s) bleeding."));
		super.unInvoke();
	}

	@Override
	public void  executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)&&(msg.amITarget(affected))&&(affected instanceof MOB))
		{
			if(msg.targetMinor()==CMMsg.TYP_HEALING)
			{
				hpToKeep=-1;
				if(healthPct((MOB)affected)>0.50)
					unInvoke();
			}
			else
			if((msg.targetMinor()==CMMsg.TYP_LOOK)
			||(msg.targetMinor()==CMMsg.TYP_EXAMINE))
				msg.source().tell((MOB)msg.target(),null,null,L("^R<S-NAME> <S-IS-ARE> still bleeding..."));
		}
		else
		if((msg.source()==affected)
		&&(msg.target() instanceof Room)
		&&(msg.tool() instanceof Exit)
		&&(msg.targetMinor()==CMMsg.TYP_LEAVE))
		{
			final Room R=(Room)msg.target();
			int dir=-1;
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				if(msg.tool()==R.getReverseExit(d))
					dir=d;
			if((dir>=0)&&(R.findItem(null,"a trail of blood")==null))
			{
				final Item I=CMClass.getItem("GenFatWallpaper");
				I.setName(L("A trail of blood"));
				if(lastDir>=0)
					I.setDisplayText(L("A faint trail of blood leads from @x1 to @x2.",Directions.getDirectionName(lastDir),Directions.getDirectionName(dir)));
				else
					I.setDisplayText(L("A faint trail of blood leads @x1.",Directions.getDirectionName(dir)));
				I.phyStats().setDisposition(I.phyStats().disposition()|PhyStats.IS_HIDDEN|PhyStats.IS_UNSAVABLE);
				I.setSecretIdentity(msg.source().Name()+"`s blood.");
				R.addItem(I,ItemPossessor.Expire.Monster_EQ);
			}
			lastDir=Directions.getOpDirectionCode(dir);
			lastRoom=R;
		}
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((ticking instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)ticking;
			if(hpToKeep<=0)
			{
				hpToKeep=mob.curState().getHitPoints();
				mob.recoverMaxState();
			}
			else
			{
				if(mob.curState().getHitPoints()>hpToKeep)
					mob.curState().setHitPoints(hpToKeep);
				final int maxMana=(int)Math.round(CMath.mul(mob.maxState().getMana(),healthPct(mob)));
				if(mob.curState().getMana()>maxMana)
					mob.curState().setMana(maxMana);
				final int maxMovement=(int)Math.round(CMath.mul(mob.maxState().getMovement(),healthPct(mob)));
				if(mob.curState().getMovement()>maxMovement)
					mob.curState().setMovement(maxMovement);
			}
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical target, boolean auto, int asLevel)
	{
		if(target==null) target=mob;
		if(!(target instanceof MOB)) return false;
		if(CMLib.flags().isGolem(target)) return false;
		if(((MOB)target).phyStats().level()<CMProps.getIntVar(CMProps.Int.INJBLEEDMINLEVEL)) return false;
		if(((MOB)target).fetchEffect(ID())!=null) return false;
		if(((MOB)target).location()==null) return false;
		if(((MOB)target).location().show((MOB)target,null,this,CMMsg.MSG_OK_VISUAL,L("^R<S-NAME> start(s) BLEEDING!^?")))
			beneficialAffect(mob,target,asLevel,0);
		return true;
	}
}
