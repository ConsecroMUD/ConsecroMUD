package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class GlowingMageArmor extends StdArmor
{
	@Override public String ID(){	return "GlowingMageArmor";}
	public GlowingMageArmor()
	{
		super();

		setName("a mystical glowing breast plate");
		setDisplayText("If this is sitting around somewhere, something is wrong!");
		setDescription("This suit of armor is made from magical energy, but looks sturdy and protective.");
		properWornBitmap=Wearable.WORN_TORSO;
		wornLogicalAnd=false;
		basePhyStats().setArmor(45);
		basePhyStats().setWeight(0);
		basePhyStats().setAbility(0);
		baseGoldValue=40000;
		material=RawMaterial.RESOURCE_NOTHING;
		recoverPhyStats();
	}


	@Override public boolean isSavable(){return false;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((amWearingAt(Wearable.IN_INVENTORY)||(owner()==null)||(owner() instanceof Room))
		&&(!amDestroyed()))
			destroy();

		final MOB mob=msg.source();
		if(!msg.amITarget(this))
			return true;
		else
		if((msg.targetMinor()==CMMsg.TYP_GET)
		||(msg.targetMinor()==CMMsg.TYP_PUSH)
		||(msg.targetMinor()==CMMsg.TYP_PULL)
		||(msg.targetMinor()==CMMsg.TYP_REMOVE))
		{
			mob.tell(L("The mage armor cannot be removed from where it is."));
			return false;
		}
		return true;
	}
}
