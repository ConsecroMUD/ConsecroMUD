package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.ReadOnlyVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_SenseDisease extends Prayer
{
	@Override public String ID() { return "Prayer_SenseDisease"; }
	private final static String localizedName = CMLib.lang().L("Sense Disease");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Sense Disease)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public int enchantQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	protected Room lastRoom=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			lastRoom=null;
			mob.tell(L("Your disease sensations fade."));
		}
	}

	public Ability getDisease(Physical mob)
	{
		for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_DISEASE)
				return A;
		}
		return null;
	}
	private static final Vector empty=new ReadOnlyVector();
	public Vector diseased(MOB mob, Room R)
	{
		if(R==null) return empty;
		Vector V=null;
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((M!=null)&&(M!=mob)&&(getDisease(M)!=null))
			{
				if(V==null) V=new Vector();
				V.addElement(M);
			}
		}
		for(int i=0;i<R.numItems();i++)
		{
			final Item I=R.getItem(i);
			if((I!=null)
			&&(I.container()==null)
			&&(getDisease(I)!=null))
			{
				if(V==null) V=new Vector();
				V.addElement(I);
			}
		}
		if(V!=null)
			return V;
		return empty;
	}

	public void messageTo(MOB mob)
	{
		String last="";
		String dirs="";
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			final Room R=mob.location().getRoomInDir(d);
			final Exit E=mob.location().getExitInDir(d);
			if((R!=null)&&(E!=null)&&(diseased(mob,R).size()>0))
			{
				if(last.length()>0)
					dirs+=", "+last;
				last=Directions.getFromDirectionName(d);
			}
		}
		final Vector V=diseased(mob,mob.location());
		if(V.size()>0)
		{
			boolean didSomething=false;
			for(int v=0;v<V.size();v++)
			{
				final Environmental E=(Environmental)V.elementAt(v);
				if(CMLib.flags().canBeSeenBy(E,mob))
				{
					didSomething=true;
					if(last.length()>0)
						dirs+=", "+last;
					last=E.name();
				}
			}
			if(!didSomething)
			{
				if(last.length()>0)
					dirs+=", "+last;
				last="here";
			}
		}

		if((dirs.length()==0)&&(last.length()==0))
			mob.tell(L("You do not sense any disease."));
		else
		if(dirs.length()==0)
			mob.tell(L("You sense disease coming from @x1.",last));
		else
			mob.tell(L("You sense disease coming from @x1, and @x2.",dirs.substring(2),last));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_MOB)
		   &&(affected!=null)
		   &&(affected instanceof MOB)
		   &&(((MOB)affected).location()!=null)
		   &&((lastRoom==null)||(((MOB)affected).location()!=lastRoom)))
		{
			lastRoom=((MOB)affected).location();
			messageTo((MOB)affected);
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		Physical target=mob;
		if((auto)&&(givenTarget!=null)) target=givenTarget;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> attain(s) disease senses!"):L("^S<S-NAME> listen(s) for a message from @x1.^?",hisHerDiety(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> listen(s) to @x1 for a message, but there is no answer.",hisHerDiety(mob)));


		// return whether it worked
		return success;
	}
}
