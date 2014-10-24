package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Specialization_Ranged extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Ranged"; }
	private final static String localizedName = CMLib.lang().L("Ranged Weapon Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Ranged()
	{
		super();
		weaponClass=Weapon.CLASS_RANGED;
		secondWeaponClass=Weapon.CLASS_THROWN;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((activated)
		&&(CMLib.dice().rollPercentage()<25)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.sourceMinor()==CMMsg.TYP_THROW)
		&&(msg.tool() instanceof Item)
		&&(msg.target() instanceof MOB))
			helpProficiency((MOB)affected, 0);
		super.executeMsg(myHost,msg);
	}
}
