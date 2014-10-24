package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GapExit extends StdExit
{
	@Override public String ID(){	return "GapExit";}
	@Override public String Name(){ return "a crevasse";}
	@Override public String description(){return "Looks like you'll have to jump it.";}

	public int mobWeight(MOB mob)
	{
		int weight=mob.basePhyStats().weight();
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((I!=null)&&(!I.amWearingAt(Wearable.WORN_FLOATING_NEARBY)))
				weight+=I.phyStats().weight();
		}
		return weight;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		final MOB mob=msg.source();
		if(((msg.amITarget(this))||(msg.tool()==this))
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(!CMLib.flags().isInFlight(mob))
		&&(!CMLib.flags().isFalling(mob)))
		{
			final int chance=(int)Math.round(CMath.div(mobWeight(mob),mob.maxCarry())*(100.0-(3.0*mob.charStats().getStat(CharStats.STAT_STRENGTH))));
			if(CMLib.dice().rollPercentage()<chance)
			{
				mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> attempt(s) to jump the crevasse, but miss(es) the far ledge!"));
				mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> fall(s)!!!!"));
				CMLib.combat().postDeath(null,mob,null);
				return false;
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		final MOB mob=msg.source();
		if(((msg.amITarget(this))||(msg.tool()==this))
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(!CMLib.flags().isInFlight(mob))
		&&(!CMLib.flags().isFalling(mob)))
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> jump(s) the crevasse!"));
	}
}
