package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class GenEclipseField extends GenTickerShield
{

	@Override public String ID(){	return "GenEclipseField";}

	public GenEclipseField()
	{
		super();
		setName("a personal eclipse field generator");
		setDisplayText("a personal eclipse field generator sits here.");
		setDescription("");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"An eclipsing field surrounds <O-NAME>.":
			"An eclipsing field surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The eclipsing field around <O-NAME> flickers and dies out as <O-HE-SHE> fade(s) back into view.":
			"The eclipsing field around <T-NAME> flickers and dies out as <T-HE-SHE> fade(s) back into view.";
	}

	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		if(activated() && (affected==owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY)) && (powerRemaining() > 0))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
		super.affectPhyStats(affected, affectableStats);
	}
}
