package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Safehouse extends ThiefSkill
{
	@Override public String ID() { return "Thief_Safehouse"; }
	private final static String localizedName = CMLib.lang().L("Safehouse");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Safehouse)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SAFEHOUSE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;

		if((msg.target()==affected)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(affected instanceof Room)
		&&(isLaw(msg.source())))
		{
			msg.source().tell(L("You don't think there's anything going on in there."));
			return false;
		}
		return true;
	}

	public boolean isLawHere(Room R)
	{
		if(R!=null)
		{
			final LegalBehavior law=CMLib.law().getLegalBehavior(R);
			if(law!=null)
			{
				final Area A=CMLib.law().getLegalObject(R);
				MOB M=null;
				for(int r=0;r<R.numInhabitants();r++)
				{
					M=R.fetchInhabitant(r);
					if((M!=null)&&(law.isAnyOfficer(A,M)||law.isJudge(A,M)))
						return true;
				}
			}
		}
		return false;
	}

	public boolean isLaw(MOB mob)
	{
		if(mob==null) return false;
		if(affected instanceof Room)
		{
			final LegalBehavior law=CMLib.law().getLegalBehavior((Room)affected);
			if(law!=null)
			{
				final Area A=CMLib.law().getLegalObject((Room)affected);
				if(law.isAnyOfficer(A,mob)||law.isJudge(A,mob))
					return true;
			}
		}
		return false;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((canBeUninvoked())&&(invoker()!=null)&&(invoker().location()!=affected))
			unInvoke();
	}

	public boolean isGoodSafehouse(Room target)
	{
		if(target==null) return false;
		if((target.domainType()==Room.DOMAIN_INDOORS_WOOD)||(target.domainType()==Room.DOMAIN_INDOORS_STONE))
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				final Room R=target.getRoomInDir(d);
				if((R!=null)&&(R.domainType()==Room.DOMAIN_OUTDOORS_CITY))
					return true;
			}
		return false;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		Room target=mob.location();
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof Room))
			target=(Room)givenTarget;
		final Ability A=target.fetchEffect(ID());
		if(A!=null)
		{
			mob.tell(L("This place is already a safehouse."));
			return false;
		}
		if((!auto)&&(CMLib.law().getLegalBehavior(target)==null))
		{
			mob.tell(L("There is no law here!"));
			return false;
		}
		if(!isGoodSafehouse(target))
		{
			TrackingLibrary.TrackingFlags flags;
			flags = new TrackingLibrary.TrackingFlags()
					.plus(TrackingLibrary.TrackingFlag.OPENONLY)
					.plus(TrackingLibrary.TrackingFlag.AREAONLY)
					.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
					.plus(TrackingLibrary.TrackingFlag.NOAIR)
					.plus(TrackingLibrary.TrackingFlag.NOWATER);
			List<Room> V=CMLib.tracking().getRadiantRooms(target,flags,50+(2*getXLEVELLevel(mob)));
			Room R=null;
			int v=0;
			for(;v<V.size();v++)
			{
				R=V.get(v);
				if((isGoodSafehouse(R))&&(!isLawHere(R)))
					break;
			}
			mob.tell(L("A place like this can't be a safehouse."));
			if((isGoodSafehouse(R))&&(!isLawHere(R)))
			{
				V=CMLib.tracking().findBastardTheBestWay(target,new XVector(R),flags,50+(2*getXLEVELLevel(mob)));
				final StringBuffer trail=new StringBuffer("");
				int dir=CMLib.tracking().trackNextDirectionFromHere(V,target,true);
				while(target!=R)
				{
					if((dir<0)||(dir>=Directions.NUM_DIRECTIONS())||(target==null)) break;
					trail.append(Directions.getDirectionName(dir));
					if(target.getRoomInDir(dir)!=R) trail.append(", ");
					target=target.getRoomInDir(dir);
					dir=CMLib.tracking().trackNextDirectionFromHere(V,target,true);
				}
				if(target==R)
					mob.tell(L("You happen to know of one nearby though.  Go: @x1",trail.toString()));
			}
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?"":L("<S-NAME> hide(s) out from the law here."));
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":L("<S-NAME> attempt(s) hide out from the law here, but things are just too hot."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,(CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)));
		}
		return success;
	}
}
