package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_Shove extends Spell
{
	@Override public String ID() { return "Spell_Shove"; }
	private final static String localizedName = CMLib.lang().L("Shove");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Shoved Down)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(4);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Tickable.TICKID_MOB;}
	public boolean doneTicking=false;
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		int dir=-1;
		if(commands.size()>0)
		{
			dir=Directions.getGoodDirectionCode((String)commands.lastElement());
			commands.removeElementAt(commands.size()-1);
		}
		if(dir<0)
		{
			if(mob.isMonster())
			{
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					if((mob.location().getRoomInDir(d)!=null)
					&&(mob.location().getExitInDir(d)!=null)
					&&(mob.location().getExitInDir(d).isOpen()))
						dir=d;
			}
			if(dir<0)
			{
				mob.tell(L("Shove whom which direction?  Try north, south, east, or west..."));
				return false;
			}
		}
		if((mob.location().getRoomInDir(dir)==null)
		   ||(mob.location().getExitInDir(dir)==null)
		   ||(!mob.location().getExitInDir(dir).isOpen()))
		{
			mob.tell(L("You can't shove anyone that way!"));
			return false;
		}

		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> get(s) shoved back!"):L("<S-NAME> incant(s) and shove(s) at <T-NAMESELF>."));
			if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
			{
				if((msg.value()<=0)&&(target.location()==mob.location()))
				{
					mob.location().send(mob,msg);
					target.makePeace();
					final Room newRoom=mob.location().getRoomInDir(dir);
					final Room thisRoom=mob.location();
					final CMMsg enterMsg=CMClass.getMsg(target,newRoom,this,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> fly(s) in from @x1.",Directions.getFromDirectionName(Directions.getOpDirectionCode(dir))));
					final CMMsg leaveMsg=CMClass.getMsg(target,thisRoom,this,CMMsg.MSG_LEAVE|CMMsg.MASK_MAGIC,L("<S-NAME> <S-IS-ARE> shoved forcefully into the air and out @x1.",Directions.getInDirectionName(dir)));
					if(thisRoom.okMessage(target,leaveMsg)&&newRoom.okMessage(target,enterMsg))
					{
						thisRoom.send(target,leaveMsg);
						newRoom.bringMobHere(target,false);
						newRoom.send(target,enterMsg);
						target.tell(L("\n\r\n\r"));
						CMLib.commands().postLook(target,true);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> incant(s), but nothing seems to happen."));


		// return whether it worked
		return success;
	}
}
