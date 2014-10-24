package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Shadowpass extends ThiefSkill
{
	@Override public String ID() { return "Thief_Shadowpass"; }
	private final static String localizedName = CMLib.lang().L("Shadowpass");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SHADOWPASS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public long flags(){return Ability.FLAG_TRANSPORTING|super.flags();}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Room R=mob.location();
		if((!auto)&&(!CMLib.flags().isInDark(R))||(R==null))
		{
			mob.tell(L("You can only shadowpass from the shadows to the shadows."));
			return false;
		}
		final List<Integer> trail=new Vector<Integer>();
		int v=0;
		for(;v<commands.size();v++)
		{
			int num=1;
			String s=(String)commands.elementAt(v);
			if(CMath.s_int(s)>0)
			{
				num=CMath.s_int(s);
				v++;
				if(v<commands.size())
					s=(String)commands.elementAt(v);
			}
			else
			if(CMath.isNumberFollowedByString(s))
			{
				final Entry<Integer,String> pair=CMath.getNumberFollowedByString(s);
				num=pair.getKey().intValue();
				s=pair.getValue();
			}

			final int direction=Directions.getGoodDirectionCode(s);
			if(direction<0) break;
			if((R.getRoomInDir(direction)==null)||(R.getExitInDir(direction)==null))
				break;
			for(int i=0;i<num;i++)
			{
				R=R.getRoomInDir(direction);
				if(!CMLib.flags().canAccess(mob,R)) break;
				trail.add(Integer.valueOf(direction));
			}
		}
		final boolean kaplah=((v==commands.size())&&(R!=null)&&(mob.location()!=R)&&(CMLib.flags().isInDark(R)));

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success && (mob.location()!=R))
		{
			final CMMsg msg=CMClass.getMsg(mob,R,this,auto?CMMsg.MSG_OK_VISUAL:CMMsg.MSG_DELICATE_HANDS_ACT,L("You begin the shadowpass ..."),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
			if((mob.location().okMessage(mob,msg))&&(R!=null)&&(R.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);
				msg.setSourceMessage(null);
				R.send(mob,msg);
				R=mob.location();
				for(int i=0;i<trail.size();i++)
				{
					final int dir=trail.get(i).intValue();
					if(!kaplah)
					{
						if((!CMLib.tracking().walk(mob,dir,false,true,true))||(!CMLib.flags().isInDark(mob.location())))
						{
							CMLib.commands().postLook(mob,true);
							return beneficialVisualFizzle(mob,null,L("<S-NAME> do(es) not know <S-HIS-HER> way through shadowpass."));
						}
						CMLib.combat().expendEnergy(mob,true);
					}
					else
					{
						R=R.getRoomInDir(dir);
						R.bringMobHere(mob,false);
					}
					CMLib.combat().expendEnergy(mob,true);
				}
				CMLib.commands().postLook(mob,true);
			}
		}
		else
		for(int i=0;i<trail.size();i++)
		{
			final int dir=trail.get(i).intValue();
			if((!CMLib.tracking().walk(mob,dir,false,true,true))||(!CMLib.flags().isInDark(mob.location())))
			{
				CMLib.commands().postLook(mob,true);
				return beneficialVisualFizzle(mob,null,L("<S-NAME> lose(s) <S-HIS-HER> way through shadowpass."));
			}
			CMLib.combat().expendEnergy(mob,true);
			CMLib.combat().expendEnergy(mob,true);
		}
		return success;
	}

}
