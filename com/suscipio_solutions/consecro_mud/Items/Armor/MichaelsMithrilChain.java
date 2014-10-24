package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class MichaelsMithrilChain extends StdArmor
{
	@Override public String ID(){	return "MichaelsMithrilChain";}
	public MichaelsMithrilChain()
	{
		super();

		setName("a chain mail vest made of mithril");
		setDisplayText("a chain mail vest made from the dwarven alloy mithril");
		setDescription("This chain mail vest is made from a dwarven alloy called mithril, making it very light.");
		properWornBitmap=Wearable.WORN_TORSO;
		secretIdentity="Michael\\`s Mithril Chain! (Armor Value:+75, Protection from Lightning)";
		baseGoldValue+=10000;
		wornLogicalAnd=false;
		basePhyStats().setArmor(50);
		basePhyStats().setWeight(40);
		basePhyStats().setAbility(75);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_MITHRIL;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.target()==null)||(!(msg.target() instanceof MOB)))
			return true;

		final MOB mob=(MOB)msg.target();
		if((msg.targetMinor()==CMMsg.TYP_ELECTRIC)
		&&(!this.amWearingAt(Wearable.IN_INVENTORY))
		&&(!this.amWearingAt(Wearable.WORN_HELD))
		&&(owner()==mob))
		{
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> appear(s) to be unaffected."));
			return false;
		}
		return true;
	}


}
