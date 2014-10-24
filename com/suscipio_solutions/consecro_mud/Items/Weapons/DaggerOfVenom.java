package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;




public class DaggerOfVenom extends Dagger
{
	@Override public String ID(){	return "DaggerOfVenom";}
	public DaggerOfVenom()
	{
		super();

		setName("a small dagger");
		setDisplayText("a sharp little dagger lies here.");
		setDescription("It has a wooden handle and a metal blade.");
		secretIdentity="A Dagger of Venom (Periodically injects poison on a successful hit.)";
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats.setWeight(1);
		baseGoldValue=1500;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		material=RawMaterial.RESOURCE_STEEL;
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		weaponType=Weapon.TYPE_PIERCING;
		weaponClassification=Weapon.CLASS_DAGGER;
		recoverPhyStats();
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.source().location()!=null)
		   &&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		   &&((msg.value())>0)
		   &&(msg.tool()==this)
		   &&(msg.target() instanceof MOB))
		{
			final int chance = (int)Math.round(Math.random() * 20.0);
			if(chance == 10)
			{
				final Ability poison = CMClass.getAbility("Poison");
				if(poison!=null) poison.invoke(msg.source(),(MOB)msg.target(), true,phyStats().level());
			}
		}
	}

}
