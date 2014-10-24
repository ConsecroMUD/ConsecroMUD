package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class ClimbableExit extends StdExit
{
	@Override public String ID(){	return "ClimbableExit";}
	@Override public String Name(){ return "a sheer surface";}
	@Override public String displayText(){ return "a sheer surface";}
	@Override public String description(){ return "Looks like you'll have to climb it.";}
	protected Ability climbA;

	public ClimbableExit()
	{
		super();
		climbA=CMClass.getAbility("Prop_Climbable");
		if(climbA!=null)
		{
			climbA.setAffectedOne(this);
			climbA.makeNonUninvokable();
		}
		recoverPhyStats();
	}
	
	@Override
	public CMObject copyOf()
	{
		final ClimbableExit R = (ClimbableExit)super.copyOf();
		R.climbA=CMClass.getAbility("Prop_Climbable");
		R.climbA.setAffectedOne(R);
		R.climbA.makeNonUninvokable();
		return R;
	}
	
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((climbA!=null)&&(!climbA.okMessage(myHost, msg)))
			return false;
		return super.okMessage(myHost, msg);
	}
	
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(climbA!=null)
			climbA.executeMsg(myHost, msg);
		super.executeMsg(myHost,msg);
	}
	
	@Override
	public void recoverPhyStats()
	{
		super.recoverPhyStats();
		if(climbA!=null)
			climbA.affectPhyStats(this, phyStats());
	}
}
