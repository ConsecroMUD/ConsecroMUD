package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spirit extends Undead
{
	@Override public String ID(){	return "Spirit"; }
	@Override public String name(){ return "Spirit"; }
	@Override public int shortestMale(){return 64;}
	@Override public int shortestFemale(){return 60;}
	@Override public int heightVariance(){return 12;}
	@Override protected boolean destroyBodyAfterUse(){return true;}
	@Override public int[] getBreathables() { return breatheAnythingArray; }

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	protected Weapon funHumanoidWeapon()
	{
		if(naturalWeaponChoices==null)
		{
			naturalWeaponChoices=new Vector();
			for(int i=1;i<11;i++)
			{
				naturalWeapon=CMClass.getWeapon("StdWeapon");
				switch(i)
				{
					case 1:
					case 2:
					case 3:
					naturalWeapon.setName(L("an invisible punch"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 4:
					naturalWeapon.setName(L("an incorporal bite"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 5:
					naturalWeapon.setName(L("a fading elbow"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 6:
					naturalWeapon.setName(L("a translucent backhand"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 7:
					naturalWeapon.setName(L("a strong ghostly jab"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 8:
					naturalWeapon.setName(L("a ghostly punch"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 9:
					naturalWeapon.setName(L("a translucent knee"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
					case 10:
					naturalWeapon.setName(L("an otherworldly slap"));
					naturalWeapon.setWeaponType(Weapon.TYPE_BURSTING);
					break;
				}
				naturalWeapon.setMaterial(RawMaterial.RESOURCE_PLASMA);
				naturalWeapon.setUsesRemaining(1000);
				naturalWeaponChoices.add(naturalWeapon);
			}
		}
		return naturalWeaponChoices.get(CMLib.dice().roll(1,naturalWeaponChoices.size(),-1));
	}
	@Override
	public Weapon myNaturalWeapon()
	{ return funHumanoidWeapon();	}

	@Override
	public String makeMobName(char gender, int age)
	{
		return super.makeMobName('N', Race.AGE_MATURE);
	}
	@Override
	public String healthText(MOB viewer, MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name(viewer) + "^r is near banishment!^N";
		else
		if(pct<.20)
			return "^r" + mob.name(viewer) + "^r is massively weak and faded.^N";
		else
		if(pct<.30)
			return "^r" + mob.name(viewer) + "^r is very faded.^N";
		else
		if(pct<.40)
			return "^y" + mob.name(viewer) + "^y is somewhat faded.^N";
		else
		if(pct<.50)
			return "^y" + mob.name(viewer) + "^y is very weak and slightly faded.^N";
		else
		if(pct<.60)
			return "^p" + mob.name(viewer) + "^p has lost stability and is weak.^N";
		else
		if(pct<.70)
			return "^p" + mob.name(viewer) + "^p is unstable and slightly weak.^N";
		else
		if(pct<.80)
			return "^g" + mob.name(viewer) + "^g is unbalanced and unstable.^N";
		else
		if(pct<.90)
			return "^g" + mob.name(viewer) + "^g is somewhat unbalanced.^N";
		else
		if(pct<.99)
			return "^g" + mob.name(viewer) + "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}

	@Override 
	public DeadBody getCorpseContainer(MOB mob, Room room)
	{
		final DeadBody body = super.getCorpseContainer(mob, room);
		if(body != null)
		{
			body.setMaterial(RawMaterial.RESOURCE_AIR);
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
				("some "+name().toLowerCase()+" essence",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}

