package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Technical;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class GenStealthShield extends GenTickerShield
{

	@Override public String ID(){	return "GenStealthShield";}

	public GenStealthShield()
	{
		super();
		setName("a personal stealth generator");
		setDisplayText("a personal stealth generator sits here.");
		setDescription("");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"A stealth field surrounds <O-NAME>.":
			"A stealth field surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The stealth field around <O-NAME> flickers and dies out as <O-HE-SHE> fade(s) back into view.":
			"The stealth field around <T-NAME> flickers and dies out as <T-HE-SHE> fade(s) back into view.";
	}

	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		if(activated() && (affected==owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY)) && (powerRemaining() > 0))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
		super.affectPhyStats(affected, affectableStats);
	}

	@Override
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if(msg.amITarget(owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY)))
		{
			if((msg.targetMinor()==CMMsg.TYP_LOOK)&&(msg.source()!=owner()))
			{
				if((msg.tool() instanceof Technical)&&(CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
				{
					if(((Technical)msg.tool()).techLevel()>techLevel())
						return true;
					return false;
				}
			}
		}
		return true;
	}

}
