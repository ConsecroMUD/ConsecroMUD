package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_SweetScent extends Chant
{
	@Override public String ID() { return "Chant_SweetScent"; }
	private final static String localizedName = CMLib.lang().L("Sweet Scent");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((affected!=null)&&(affected instanceof Item))
		{
			final Item I=(Item)affected;
			if(I.owner() instanceof Room)
			{
				final Room room=(Room)I.owner();
				final Vector rooms=new Vector();
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.OPENONLY);
				CMLib.tracking().getRadiantRooms(room,rooms,flags,null,10,null);
				for(int i=0;i<room.numInhabitants();i++)
				{
					final MOB M=room.fetchInhabitant(i);
					if((M!=null)
					&&(CMLib.flags().isAnimalIntelligence(M))
					&&(CMLib.flags().canSmell(M)))
						M.tell(M,I,null,L("<T-NAME> smell(s) absolutely intoxicating!"));
				}
				for(int r=0;r<rooms.size();r++)
				{
					final Room R=(Room)rooms.elementAt(r);
					if(R!=room)
					{
						final int dir=CMLib.tracking().radiatesFromDir(R,rooms);
						if(dir>=0)
						{
							for(int i=0;i<R.numInhabitants();i++)
							{
								final MOB M=R.fetchInhabitant(i);
								if((M!=null)
								&&(CMLib.flags().isAnimalIntelligence(M))
								&&(!M.isInCombat())
								&&((!M.isMonster())||(CMLib.flags().isMobile(M)))
								&&(CMLib.flags().canSmell(M)))
								{
									M.tell(M,null,null,L("You smell something irresistable @x1.",Directions.getInDirectionName(dir)));
									if(CMLib.dice().rollPercentage()>M.charStats().getSave(CharStats.STAT_SAVE_MIND))
										CMLib.tracking().walk(M,dir,false,false);
								}
							}
						}
					}

				}
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_SNIFF)
		&&(CMLib.flags().canSmell(msg.source())))
			msg.source().tell(msg.source(),affected,null,L("<T-NAME> smell(s) absolutely intoxicating!"));
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(
		  (mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR)
		||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
		||(mob.location().domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
		||(mob.location().domainType()==Room.DOMAIN_INDOORS_AIR)
		||(mob.location().domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
		   )
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}

		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		if(!Druid_MyPlants.isMyPlant(target,mob))
		{
			mob.tell(L("@x1 is not one of your plants!",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to the <T-NAMESELF>, but nothing happens."));

		// return whether it worked
		return success;
	}
}
