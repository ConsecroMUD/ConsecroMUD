package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_MarkerSummoning extends Spell
{
	@Override public String ID() { return "Spell_MarkerSummoning"; }
	private final static String localizedName = CMLib.lang().L("Marker Summoning");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_TRANSPORTING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Room oldRoom=null;
		try
		{
			for(final Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
			{
				final Room R=(Room)r.nextElement();
				if(CMLib.flags().canAccess(mob,R))
					for(final Enumeration<Ability> a=R.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)
						&&(A.invoker()==mob))
						{
							if(A.ID().equals("Spell_SummonMarker"))
							{
								oldRoom=R;
								break;
							}
						}
					}
				if(oldRoom!=null) break;
			}
		}catch(final NoSuchElementException nse){}
		if(oldRoom==null)
		{
			mob.tell(L("You can't seem to focus on your marker.  Are you sure you've already summoned it?"));
			return false;
		}
		final Room newRoom=mob.location();
		if(oldRoom==newRoom)
		{
			mob.tell(L("But your marker is HERE!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final Vector inhabs=new Vector();
		int profNeg=0;
		for(int m=0;m<oldRoom.numInhabitants();m++)
		{
			final MOB M=oldRoom.fetchInhabitant(m);
			if(M!=null)
			{
				inhabs.addElement(M);
				final int adjustment=M.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob)));
				profNeg+=adjustment;
			}
		}
		profNeg+=newRoom.numItems();

		final boolean success=proficiencyCheck(mob,-(profNeg/2),auto);

		if((success)&&(inhabs.size()>0))
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MASK_MOVE|verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> summon(s) the power of <S-HIS-HER> marker energy!^?"));
			if((mob.location().okMessage(mob,msg))&&(oldRoom.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);
				for(int i=0;i<inhabs.size();i++)
				{
					final MOB follower=(MOB)inhabs.elementAt(i);
					final CMMsg enterMsg=CMClass.getMsg(follower,newRoom,this,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> appear(s) in a burst of light."));
					final CMMsg leaveMsg=CMClass.getMsg(follower,oldRoom,this,CMMsg.MSG_LEAVE|CMMsg.MASK_MAGIC,L("<S-NAME> disappear(s) in a great summoning swirl."));
					if(oldRoom.okMessage(follower,leaveMsg)&&newRoom.okMessage(follower,enterMsg))
					{
						follower.makePeace();
						oldRoom.send(follower,leaveMsg);
						newRoom.bringMobHere(follower,false);
						newRoom.send(follower,enterMsg);
						follower.tell(L("\n\r\n\r"));
						CMLib.commands().postLook(follower,true);
					}
				}
				final Vector items=new Vector();
				for(int i=oldRoom.numItems()-1;i>=0;i--)
				{
					final Item I=oldRoom.getItem(i);
					if(I!=null) items.addElement(I);
				}
				for(int i=0;i<items.size();i++)
				{
					final Item I=(Item)items.elementAt(i);
					oldRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 disappears in a summoning swirl!",I.name()));
					newRoom.moveItemTo(I);
					newRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 appears in a burst of light!",I.name()));
				}
			}

		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to summon <S-HIS-HER> marker energy, but fail(s)."));


		// return whether it worked
		return success;
	}
}
