package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class ClimbableSurface extends StdRoom
{
	@Override public String ID(){return "ClimbableSurface";}
	protected Ability climbA;
	public ClimbableSurface()
	{
		super();
		name="the surface";
		basePhyStats.setWeight(4);
		climbA=CMClass.getAbility("Prop_Climbable");
		if(climbA!=null)
		{
			climbA.setAffectedOne(this);
			climbA.makeNonUninvokable();
		}
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_ROCKS;}
	
	@Override
	public CMObject copyOf()
	{
		final ClimbableSurface R = (ClimbableSurface)super.copyOf();
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
