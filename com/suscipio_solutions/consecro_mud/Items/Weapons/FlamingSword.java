package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class FlamingSword extends Longsword
{
	@Override public String ID(){	return "FlamingSword";}
	public FlamingSword()
	{
		super();

		setName("a fancy longsword");
		setDisplayText("a fancy longsword has been dropped on the ground.");
		setDescription("A one-handed sword with a very slight red tinge on the blade.");
		secretIdentity="A Flaming Sword (Additional fire damage when you strike)";
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_LIGHTSOURCE | PhyStats.IS_BONUS);
		baseGoldValue=2500;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&((msg.value())>0)
		&&(msg.tool()==this)
		&&(msg.target() instanceof MOB)
		&&(!((MOB)msg.target()).amDead())
		&&(msg.source()==owner()))
		{
			final Room room=msg.source().location();
			final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),this,
					CMMsg.MSG_OK_ACTION,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_FIRE,CMMsg.MSG_NOISYMOVEMENT,null);
			if((room!=null) && (room.okMessage(msg.source(),msg2)))
			{
				room.send(msg.source(), msg2);
				if(msg2.value()<=0)
				{
					int flameDamage = (int) Math.round( Math.random() * 6 );
					flameDamage *= basePhyStats().level();
					CMLib.combat().postDamage(msg.source(),(MOB)msg.target(),null,flameDamage,
							CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,name()+" <DAMAGE> <T-NAME>!");
				}
			}
		}
	}

}
