package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.ReadOnlyVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_SenseLaw extends ThiefSkill
{
	@Override public String ID() { return "Thief_SenseLaw"; }
	private final static String localizedName = CMLib.lang().L("Sense Law");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	public static final Vector empty=new ReadOnlyVector();
	protected Room oldroom=null;
	protected String lastReport="";
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	public Vector getLawMen(Area legalObject, Room room, LegalBehavior B)
	{
		if(room==null) return empty;
		if(room.numInhabitants()==0) return empty;
		if(B==null) return empty;
		final Vector V=new Vector();
		for(int m=0;m<room.numInhabitants();m++)
		{
			final MOB M=room.fetchInhabitant(m);
			if((M!=null)&&(M.isMonster())&&(B.isElligibleOfficer(legalObject,M)))
				V.addElement(M);
		}
		return V;
	}

	public boolean findLaw(Room R, int depth, int maxDepth)
	{
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((mob.location()!=null)&&(!mob.isMonster()))
			{
				final LegalBehavior B=CMLib.law().getLegalBehavior(mob.location());
				if(B==null)
					return super.tick(ticking,tickID);
				final StringBuffer buf=new StringBuffer("");
				Vector V=getLawMen(CMLib.law().getLegalObject(mob.location()),mob.location(),B);
				for(int l=0;l<V.size();l++)
				{
					final MOB M=(MOB)V.elementAt(l);
					if(CMLib.flags().canBeSeenBy(M,mob))
						buf.append(L("@x1 is an officer of the law.  ",M.name(mob)));
					else
						buf.append(L("There is an officer of the law here.  "));
				}
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room R=mob.location().getRoomInDir(d);
					final Exit E=mob.location().getExitInDir(d);
					if((R!=null)&&(E!=null)&&(E.isOpen()))
					{
						V=getLawMen(mob.location().getArea(),R,B);
						if((V!=null)&&(V.size()>0))
							buf.append(L("There is an officer of the law @x1.  ",Directions.getInDirectionName(d)));
					}
				}
				if((buf.length()>0)
				&&((mob.location()!=oldroom)||(!buf.toString().equals(lastReport)))
				&&((mob.fetchAbility(ID())==null)||proficiencyCheck(mob,0,false)))
				{
					mob.tell(L("You sense: @x1",buf.toString()));
					oldroom=mob.location();
					helpProficiency(mob, 0);
					lastReport=buf.toString();
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean autoInvocation(MOB mob)
	{
		if(mob.charStats().getCurrentClass().ID().equals("Immortal"))
			return false;
		return super.autoInvocation(mob);
	}
}
