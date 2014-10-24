package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.LinkedList;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MusicalInstrument;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Play_Cymbals extends Play_Instrument
{
	@Override public String ID() { return "Play_Cymbals"; }
	private final static String localizedName = CMLib.lang().L("Cymbals");
	@Override public String name() { return localizedName; }
	@Override protected int requiredInstrumentType(){return MusicalInstrument.TYPE_CYMBALS;}
	@Override public String mimicSpell(){return "Spell_Knock";}
	private static Ability theSpell=null;
	@Override
	protected Ability getSpell()
	{
		if(theSpell!=null) return theSpell;
		if(mimicSpell().length()==0) return null;
		theSpell=CMClass.getAbility(mimicSpell());
		return theSpell;
	}

	@Override
	protected void inpersistantAffect(MOB mob)
	{
		if(getSpell()!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				final List<Physical> knockables=new LinkedList<Physical>();
				int dirCode=-1;
				if(mob==invoker())
				{
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					{
						final Exit e=R.getExitInDir(d);
						if((e!=null)&&(e.hasADoor())&&(e.hasALock())&&(e.isLocked()))
						{
							knockables.add(e);
							dirCode=d;
						}
					}
					for(int i=0;i<R.numItems();i++)
					{
						final Item I=R.getItem(i);
						if((I!=null)&&(I instanceof Container)&&(I.container()==null))
						{
							final Container C=(Container)I;
							if(C.hasADoor()&&C.hasALock()&&C.isLocked())
								knockables.add(C);
						}
					}
				}
				for(int i=0;i<mob.numItems();i++)
				{
					final Item I=mob.getItem(i);
					if((I!=null)&&(I instanceof Container)&&(I.container()==null))
					{
						final Container C=(Container)I;
						if(C.hasADoor()&&C.hasALock()&&C.isLocked())
							knockables.add(C);
					}
				}
				for(final Physical P : knockables)
				{
					int levelDiff=P.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
					if(levelDiff<0) levelDiff=0;
					if(proficiencyCheck(mob,-(levelDiff*25),false))
					{
						CMMsg msg=CMClass.getMsg(mob,P,this,CMMsg.MSG_CAST_VERBAL_SPELL,L("@x1 begin(s) to glow!",P.name()));
						if(R.okMessage(mob,msg))
						{
							R.send(mob,msg);
							msg=CMClass.getMsg(mob,P,null,CMMsg.MSG_UNLOCK,null);
							CMLib.utensils().roomAffectFully(msg,R,dirCode);
							msg=CMClass.getMsg(mob,P,null,CMMsg.MSG_OPEN,L("<T-NAME> opens."));
							CMLib.utensils().roomAffectFully(msg,R,dirCode);
						}
					}
				}
			}
		}
	}
	@Override protected int canAffectCode(){return 0;}
}
