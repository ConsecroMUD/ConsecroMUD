package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.PairVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class TemporaryImmunity extends StdAbility
{
	@Override public String ID() { return "TemporaryImmunity"; }
	private final static String localizedName = CMLib.lang().L("Temporary Immunity");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	@Override public boolean canBeUninvoked(){return true;}
	@Override public boolean isAutoInvoked(){return true;}
	public final static long IMMUNITY_TIME=36000000;
	protected PairVector<String,Long> set=new PairVector<String,Long>();

	public TemporaryImmunity()
	{
		super();

		tickDown = 10;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected instanceof MOB)
		&&(tickID==Tickable.TICKID_MOB)
		&&((--tickDown)==0))
		{
			tickDown=10;
			makeLongLasting();
			for(int s=set.size()-1;s>=0;s--)
			{
				final Long L=set.elementAt(s).second;
				if((System.currentTimeMillis()-L.longValue())>IMMUNITY_TIME)
					set.removeElementAt(s);
			}

			if(set.size()==0){ unInvoke(); return false;}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public String text()
	{
		if(set.size()==0) return "";
		final StringBuffer str=new StringBuffer("");
		for(int s=0;s<set.size();s++)
			str.append(set.elementAt(s).first+"/"+set.elementAt(s).second.longValue()+";");
		return str.toString();
	}

	@Override
	public void setMiscText(String str)
	{
		if(str.startsWith("+"))
		{
			str=str.substring(1);
			if(set.indexOf(str)>=0)
				set.setElementAt(new Pair<String,Long>(str,Long.valueOf(System.currentTimeMillis())),set.indexOfFirst(str));
			else
				set.addElement(str,Long.valueOf(System.currentTimeMillis()));
		}
		else
		{
			set.clear();
			final List<String> V=CMParms.parseSemicolons(str,true);
			for(int v=0;v<V.size();v++)
			{
				final String s=V.get(v);
				final int x=s.indexOf('/');
				if(x>0)
					set.addElement(s.substring(0,x),Long.valueOf(CMath.s_long(s.substring(x+1))));
			}
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.amITarget(mob))
		&&(!mob.amDead())
		&&(msg.tool() instanceof Ability)
		&&(set.contains(msg.tool().ID())))
		{
			if(msg.source()!=msg.target())
				mob.location().show(mob,msg.source(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> seem(s) immune to @x1.",msg.tool().name()));
			return false;
		}
		return true;
	}
}
