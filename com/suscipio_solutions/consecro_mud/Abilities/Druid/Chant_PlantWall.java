package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_PlantWall extends Chant
{
	@Override public String ID() { return "Chant_PlantWall"; }
	private final static String localizedName = CMLib.lang().L("Plant Wall");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Plant Wall)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(10);}
	@Override public int minRange(){return 1;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}

	protected int amountRemaining=0;
	protected Item theWall=null;
	protected String deathNotice="";

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Item)))
			return true;

		final MOB mob=msg.source();

		if((invoker!=null)
		&&(mob.isInCombat())
		&&(mob!=invoker)
		&&(mob.getVictim()==invoker))
		{
			if((msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
			&&(mob.rangeToTarget()>0)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Weapon)
			&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_RANGED)
			&&(msg.tool().maxRange()>0))
			{
				final CMMsg msg2=CMClass.getMsg(mob,null,CMMsg.MSG_WEAPONATTACK,L("^F^<FIGHT^><S-NAME> fire(s) at the plant wall with @x1.^</FIGHT^>^?",msg.tool().name()));
				CMLib.color().fixSourceFightColor(msg2);
				if(mob.location().okMessage(mob,msg2))
				{
					mob.location().send(mob,msg2);
					amountRemaining-=mob.phyStats().damage();
					if(amountRemaining<0)
					{
						deathNotice="The plant wall is destroyed!";
						((Item)affected).destroy();
					}
				}
				return false;
			}
			else
			if((mob.rangeToTarget()==1)&&(msg.sourceMinor()==CMMsg.TYP_ADVANCE))
			{
				Item w=mob.fetchWieldedItem();
				if(w==null) w=mob.myNaturalWeapon();
				if(w==null) return false;
				final CMMsg msg2=CMClass.getMsg(mob,null,CMMsg.MSG_WEAPONATTACK,L("^F<S-NAME> hack(s) at the plant wall with @x1.^?",w.name()));
				CMLib.color().fixSourceFightColor(msg2);
				if(mob.location().okMessage(mob,msg2))
				{
					mob.location().send(mob,msg2);
					amountRemaining-=mob.phyStats().damage();
					if(amountRemaining<0)
					{
						deathNotice="The plant wall is destroyed!";
						((Item)affected).destroy();
					}
				}
				return false;
			}
			else
			if((mob.rangeToTarget()>0)
			&&(msg.targetMinor()==CMMsg.TYP_CAST_SPELL)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(msg.tool().maxRange()>0))
			{
				final CMMsg msg2=CMClass.getMsg(mob,null,msg.tool(),CMMsg.MSG_OK_VISUAL,L("^F^<FIGHT^>The plant wall absorbs <O-NAME> from <S-NAME>.^</FIGHT^>^?"));
				CMLib.color().fixSourceFightColor(msg2);
				if(mob.location().okMessage(mob,msg2))
					mob.location().send(mob,msg2);
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		super.unInvoke();
		if(canBeUninvoked())
		{
			if((theWall!=null)
			&&(theWall.owner()!=null)
			&&(theWall.owner() instanceof Room)
			&&(((Room)theWall.owner()).isContent(theWall)))
			{
				final MOB actorM=(invoker!=null)? invoker : CMLib.map().deity();
				((Room)theWall.owner()).show(actorM,null,CMMsg.MSG_OK_VISUAL,deathNotice);
				final Item wall=theWall;
				theWall=null;
				wall.destroy();
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
		{
			if((invoker!=null)
			   &&(theWall!=null)
			   &&(invoker.location()!=null)
			   &&(!invoker.location().isContent(theWall)))
				unInvoke();
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(((mob.location().domainType()&Room.INDOORS)>0)&&(!auto))
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if((!mob.isInCombat())||(mob.rangeToTarget()<1))
		{
			mob.tell(L("You really should be in ranged combat to use this chant."));
			return false;
		}
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)&&(I.fetchEffect(ID())!=null))
			{
				mob.tell(L("There is already a plant wall here."));
				return false;
			}
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A plant wall appears!"):L("^S<S-NAME> chant(s) for a plant wall!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				amountRemaining=(mob.baseState().getHitPoints()/6)+(2*(super.getX1Level(invoker())+super.getXLEVELLevel(invoker())));
				final Item I=CMClass.getItem("GenItem");
				I.setName(L("a plant wall"));
				I.setDisplayText(L("a writhing plant wall has grown here"));
				I.setDescription(L("The wall is thick and stringy."));
				I.setMaterial(RawMaterial.RESOURCE_GREENS);
				CMLib.flags().setGettable(I,false);
				I.recoverPhyStats();
				mob.location().addItem(I);
				theWall=I;
				deathNotice="The plant wall withers away!";
				beneficialAffect(mob,I,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> incant(s), but the magic fizzles."));

		// return whether it worked
		return success;
	}
}
