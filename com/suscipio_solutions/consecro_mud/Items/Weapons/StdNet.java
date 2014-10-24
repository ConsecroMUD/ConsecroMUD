package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.HashSet;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class StdNet extends StdWeapon
{
	@Override public String ID(){	return "StdNet";}
	public StdNet()
	{
		super();
		setName("a net");
		setDisplayText("a net has been left here.");
		setDescription("Its a wide tangling net!");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(1);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(0);
		baseGoldValue=10;
		recoverPhyStats();
		minRange=1;
		maxRange=1;
		weaponType=Weapon.TYPE_NATURAL;
		material=RawMaterial.RESOURCE_HEMP;
		weaponClassification=Weapon.CLASS_THROWN;
		setRawLogicalAnd(true);
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.tool()==this)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(weaponClassification()==Weapon.CLASS_THROWN))
			return;
			//msg.addTrailerMsg(CMClass.getMsg(msg.source(),this,CMMsg.MSG_DROP,null));
		else
		if((msg.tool()==this)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.target() !=null)
		&&(msg.target() instanceof MOB)
		&&(weaponClassification()==Weapon.CLASS_THROWN))
		{
			unWear();
			msg.addTrailerMsg(CMClass.getMsg(msg.source(),this,CMMsg.MASK_ALWAYS|CMMsg.MSG_DROP,null));
			msg.addTrailerMsg(CMClass.getMsg((MOB)msg.target(),this,CMMsg.MASK_ALWAYS|CMMsg.MSG_GET,null));
			msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),this,CMMsg.MASK_ALWAYS|CMMsg.TYP_GENERAL,null));
		}
		else
		if((msg.tool()==this)
		&&(msg.target()!=null)
		&&(msg.target() instanceof MOB)
		&&(msg.targetMinor()==CMMsg.TYP_GENERAL)
		&&(((MOB)msg.target()).isMine(this))
		&&(msg.sourceMessage()==null))
		{
			final MOB M=(MOB)msg.target();
			final Set<MOB> H=msg.source().getGroupMembers(new HashSet<MOB>());
			if(H.contains(M)) H.remove(M);

			for(int i=0;i<M.location().numInhabitants();i++)
			{
				final MOB M2=M.location().fetchInhabitant(i);
				if((M2!=null)
				&&(M2!=msg.source())
				&&(!H.contains(M2))
				&&(M2.getVictim()==M.getVictim())
				&&(M2.rangeToTarget()==M.rangeToTarget()))
				{
					final Ability A=CMClass.getAbility("Thief_Bind");
					if(A!=null)
					{
						A.setAffectedOne(this);
						A.invoke(msg.source(),M2,true,phyStats().level());
					}
				}
			}
		}
		else
			super.executeMsg(myHost,msg);
	}
}
