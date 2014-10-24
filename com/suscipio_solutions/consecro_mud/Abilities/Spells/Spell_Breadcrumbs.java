package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_Breadcrumbs extends Spell
{
	@Override public String ID() { return "Spell_Breadcrumbs"; }
	private final static String localizedName = CMLib.lang().L("Breadcrumbs");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	public Vector trail=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your breadcrumbs fade away."));
		trail=null;
	}

	@Override
	public String displayText()
	{
		final StringBuffer str=new StringBuffer(L("(Breadcrumb Trail: "));
		if(trail!=null)
		synchronized(trail)
		{
			Room lastRoom=null;
			for(int v=trail.size()-1;v>=0;v--)
			{
				final Room R=(Room)trail.elementAt(v);
				if(lastRoom!=null)
				{
					int dir=-1;
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					{
						if(lastRoom.getRoomInDir(d)==R)
						{ dir=d; break;}
					}
					if(dir>=0)
						str.append(Directions.getDirectionName(dir)+" ");
					else
						str.append(L("Unknown "));
				}
				lastRoom=R;
			}
		}
		return str.toString()+")";
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(trail!=null)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.target()!=null)
		&&(msg.target() instanceof Room))
		{
			final Room newRoom=(Room)msg.target();
			boolean kill=false;
			int t=0;
			while(t<trail.size())
			{
				if(kill) trail.removeElement(trail.elementAt(t));
				else
				{
					final Room R=(Room)trail.elementAt(t);
					if(R==newRoom)
						kill=true;
					t++;
				}
			}
			if(kill) return;
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				final Room adjacentRoom=newRoom.getRoomInDir(d);
				if((adjacentRoom!=null)
				   &&(newRoom.getExitInDir(d)!=null))
				{
					kill=false;
					t=0;
					while(t<trail.size())
					{
						if(kill) trail.removeElement(trail.elementAt(t));
						else
						{
							final Room R=(Room)trail.elementAt(t);
							if(R==adjacentRoom)
								kill=true;
							t++;
						}
					}
				}
			}
			trail.addElement(newRoom);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if(target==null) return false;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already dropping breadcrumbs."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> attain(s) mysterious breadcrumbs."):L("^S<S-NAME> invoke(s) the mystical breadcrumbs.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				trail=new Vector();
				trail.addElement(mob.location());
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke breadcrumbs, but fail(s)."));

		// return whether it worked
		return success;
	}
}
