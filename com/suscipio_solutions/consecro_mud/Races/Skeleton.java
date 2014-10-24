package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Skeleton extends Undead
{
	@Override public String ID(){	return "Skeleton"; }
	@Override public String name(){ return "Skeleton"; }

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(myHost instanceof MOB)
		{
			final MOB mob=(MOB)myHost;
			if((msg.amITarget(mob))
			&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Weapon)
			&&((((Weapon)msg.tool()).weaponType()==Weapon.TYPE_PIERCING)
				||(((Weapon)msg.tool()).weaponType()==Weapon.TYPE_SLASHING))
			&&(!mob.amDead()))
			{
				final int recovery=(int)Math.round(CMath.div((msg.value()),2.0));
				msg.setValue(recovery);
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override 
	public DeadBody getCorpseContainer(MOB mob, Room room)
	{
		final DeadBody body = super.getCorpseContainer(mob, room);
		if(body != null)
		{
			body.setMaterial(RawMaterial.RESOURCE_BONE);
		}
		return body;
	}
	
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<2;i++)
					resources.addElement(makeResource
						("knuckle bone",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
						("a skull",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
						("a bone",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
