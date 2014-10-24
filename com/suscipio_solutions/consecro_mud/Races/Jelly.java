package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;


public class Jelly extends Unique
{
	@Override public String ID(){	return "Jelly"; }
	@Override public String name(){ return "Jelly"; }
	@Override public int shortestMale(){return 24;}
	@Override public int shortestFemale(){return 20;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 200;}
	@Override public int weightVariance(){return 200;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Slime";}
	@Override public boolean fertile(){return true;}

	@Override public String arriveStr() { return "sloshes in"; }
	@Override public String leaveStr() { return "sloshes"; }

	@Override
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName(L("an appendange"));
			naturalWeapon.setRanges(0,2);
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BERRIES);
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

	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
			case Race.AGE_CHILD:
				return name().toLowerCase()+" puddle";
			default :
				return super.makeMobName('N', age);
		}
	}
}
