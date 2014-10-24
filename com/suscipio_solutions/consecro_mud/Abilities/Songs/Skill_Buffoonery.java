package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_Buffoonery extends BardSkill
{
	@Override public String ID() { return "Skill_Buffoonery"; }
	private final static String localizedName = CMLib.lang().L("Buffoonery");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"BUFFOONERY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}
	@Override public int usageType(){return USAGE_MOVEMENT;}


	protected Vector getFreeWearingPositions(MOB target)
	{
		final Vector V=new Vector();
		final Wearable.CODES codes = Wearable.CODES.instance();
		final boolean[] pos=new boolean[codes.all_ordered().length];

		for(int i=0;i<pos.length;i++)
			if(target.freeWearPositions(codes.all_ordered()[i],(short)0,(short)0)>0)
				pos[i]=false;
			else
				pos[i]=true;

		for(int i=0;i<pos.length;i++)
			if(!pos[i])
				V.addElement(Long.valueOf(codes.all_ordered()[i]));
		return V;
	}

	protected boolean freePosition(MOB target)
	{
		return getFreeWearingPositions(target).size()>0;
	}

	public String correctItem(MOB mob)
	{
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((I!=null)
			&&(CMLib.flags().canBeSeenBy(I,mob))
			&&(I.amWearingAt(Wearable.IN_INVENTORY))
			&&(!((((I instanceof Armor)&&(I.basePhyStats().armor()>1))
				||((I instanceof Weapon)&&(I.basePhyStats().damage()>1))))))
				return I.Name();
		}
		return null;
	}

	public Item targetItem(MOB target)
	{
		final Vector V=new Vector();
		for(int i=0;i<target.numItems();i++)
		{
			final Item I2=target.getItem(i);
			if((!I2.amWearingAt(Wearable.IN_INVENTORY))
			&&(((I2 instanceof Weapon)&&(I2.basePhyStats().damage()>1))
			   ||((I2 instanceof Armor)&&(I2.basePhyStats().armor()>1)))
			&&(I2.container()==null))
				V.addElement(I2);
		}
		if(V.size()>0)
			return (Item)V.elementAt(CMLib.dice().roll(1,V.size(),-1));
		return null;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final String parm=correctItem(mob);
			if(parm==null)
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				final Item targetItem=targetItem((MOB)target);
				if(targetItem==null)
				{
					if(!freePosition((MOB)target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			if(mob.isMonster()&&(commands.size()==1))
			{
				final String parm=correctItem(mob);
				if(parm!=null)
					commands.addElement(parm);
			}
			if(commands.size()<2)
			{
				mob.tell(L("You must specify a target, and what item to swap on the target!"));
				return false;
			}
		}
		final Item I=mob.findItem(null,(String)commands.lastElement());
		if((I==null)||(!CMLib.flags().canBeSeenBy(I,mob)))
		{
			mob.tell(L("You don't seem to have '@x1'.",((String)commands.lastElement())));
			return false;
		}
		if(((I instanceof Armor)&&(I.basePhyStats().armor()>1))
		||((I instanceof Weapon)&&(I.basePhyStats().damage()>1)))
		{
			mob.tell(L("@x1 is not buffoonish enough!",I.name(mob)));
			return false;
		}
		commands.removeElementAt(commands.size()-1);

		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		final Item targetItem=targetItem(target);
		if(targetItem==null)
		{
			if(!freePosition(target))
			{
				mob.tell(L("@x1 has no free wearing positions!",target.name(mob)));
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-mob.phyStats().level();

		final boolean success=proficiencyCheck(mob,0,auto);
		if(levelDiff>0)
			levelDiff=-(levelDiff*((!CMLib.flags().canBeSeenBy(mob,target))?5:15));
		else
			levelDiff=-(levelDiff*((!CMLib.flags().canBeSeenBy(mob,target))?1:2));

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,(CMMsg.MSG_NOISYMOVEMENT|CMMsg.MASK_DELICATE|CMMsg.MASK_MALICIOUS)|(auto?CMMsg.MASK_ALWAYS:0),auto?"":L("<S-NAME> do(es) buffoonery to <T-NAMESELF>."));			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				long position=-1;
				if(targetItem!=null)
				{
					position=targetItem.rawWornCode();
					targetItem.unWear();
				}
				else
				{
					final Vector free=getFreeWearingPositions(target);
					if(free.size()<1)
					{
						mob.tell(L("@x1 has no free wearing positions!",target.name(mob)));
						return false;
					}
					if((free.contains(Long.valueOf(Wearable.WORN_WIELD)))
					&&((I instanceof Weapon)||(!(I instanceof Armor))))
						position=Wearable.WORN_WIELD;
					else
						position=((Long)free.elementAt(CMLib.dice().roll(1,free.size(),-1))).longValue();
				}
				if(position>=0)
				{
					I.unWear();
					target.moveItemTo(I);
					I.wearAt(position);
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) buffoonery on <T-NAMESELF>, but fail(s)."));

		return success;
	}

}
