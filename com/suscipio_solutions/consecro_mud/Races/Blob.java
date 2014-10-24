package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;


public class Blob extends Unique
{
	@Override public String ID(){	return "Blob"; }
	@Override public String name(){ return "Blob"; }
	@Override public int shortestMale(){return 24;}
	@Override public int shortestFemale(){return 20;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 200;}
	@Override public int weightVariance(){return 200;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Slime";}
	@Override public boolean fertile(){return true;}

	@Override public String arriveStr() { return "drags itself in"; }
	@Override public String leaveStr() { return "drags itself"; }

	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("a body slam"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_SLIME);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
		}
		return naturalWeapon;
	}

	@Override 
	public DeadBody getCorpseContainer(MOB mob, Room room)
	{
		final DeadBody body = super.getCorpseContainer(mob, room);
		if(body != null)
		{
			body.setMaterial(RawMaterial.RESOURCE_BLOOD);
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
				resources.addElement(makeResource
				("a palm-full of "+name().toLowerCase(),RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
