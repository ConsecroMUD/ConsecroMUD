package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_Map extends StdSkill
{
	@Override public String ID() { return "Skill_Map"; }
	private final static String localizedName = CMLib.lang().L("Make Maps");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Mapping)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"MAP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_CALLIGRAPHY;}

	Vector roomsMappedAlready=new Vector();
	protected Item map=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("You stop mapping."));
		map=null;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if((map.owner()==null)
		||(map.owner()!=mob))
			unInvoke();
		else
		if((msg.amISource(mob))
		&&(map!=null)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.target()!=null)
		&&(msg.target() instanceof Room)
		&&(CMLib.flags().canBeSeenBy(msg.target(),msg.source()))
		&&(!roomsMappedAlready.contains(msg.target()))
		&&(!CMath.bset(((Room)msg.target()).phyStats().sensesMask(),PhyStats.SENSE_ROOMUNMAPPABLE)))
		{
			roomsMappedAlready.addElement(msg.target());
			map.setReadableText(map.readableText()+";"+CMLib.map().getExtendedRoomID((Room)msg.target()));
			if(map instanceof com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)
				((com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)map).doMapArea();
		}

		super.executeMsg(myHost,msg);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Ability A=mob.fetchEffect(ID());
		if(A!=null)
		{
			A.unInvoke();
			return true;
		}
		if(mob.charStats().getStat(CharStats.STAT_INTELLIGENCE)<5)
		{
			mob.tell(L("You are too stupid to actually make a map."));
			return false;
		}
		final Item target=getTarget(mob,null,givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null)return false;

		Item item=target;
		if(!item.isReadable())
		{
			mob.tell(L("You can't map on that."));
			return false;
		}

		if(item instanceof Scroll)
		{
			mob.tell(L("You can't map on a scroll."));
			return false;
		}

		if(item instanceof com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)
		{
			if(!item.ID().equals("BardMap"))
			{
				mob.tell(L("There's no more room to add to that map."));
				return false;
			}
		}
		else
		if(item.readableText().length()>0)
		{
			mob.tell(L("There's no more room to map on that."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_WRITE,L("<S-NAME> start(s) mapping on <T-NAMESELF>."),CMMsg.MSG_WRITE,";",CMMsg.MSG_WRITE,L("<S-NAME> start(s) mapping on <T-NAMESELF>."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(!item.ID().equals("BardMap"))
				{
					final Item B=CMClass.getItem("BardMap");
					B.setContainer(item.container());
					B.setName(item.Name());
					B.setBasePhyStats(item.basePhyStats());
					B.setBaseValue(item.baseGoldValue()*2);
					B.setDescription(item.description());
					B.setDisplayText(item.displayText());
					B.setMaterial(item.material());
					B.setRawLogicalAnd(item.rawLogicalAnd());
					B.setRawProperLocationBitmap(item.rawProperLocationBitmap());
					B.setSecretIdentity(item.secretIdentity());
					CMLib.flags().setRemovable(B,CMLib.flags().isRemovable(item));
					B.setUsesRemaining(item.usesRemaining());
					item.destroy();
					mob.addItem(B);
					item=B;
				}
				map=item;
				if(!roomsMappedAlready.contains(mob.location()))
				{
					roomsMappedAlready.addElement(mob.location());
					map.setReadableText(map.readableText()+";"+CMLib.map().getExtendedRoomID(mob.location()));
					if(map instanceof com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)
						((com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)map).doMapArea();
				}
				String rooms=item.readableText();
				int x=rooms.indexOf(';');
				while(x>=0)
				{
					final String roomID=rooms.substring(0,x);
					final Room room=CMLib.map().getRoom(roomID);
					if(room!=null)
						if(!roomsMappedAlready.contains(room))
							roomsMappedAlready.addElement(room);
					rooms=rooms.substring(x+1);
					x=rooms.indexOf(';');
				}
				beneficialAffect(mob,mob,asLevel,0);
			}
		}
		else
			mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<S-NAME> attempt(s) to start mapping on <T-NAMESELF>, but mess(es) up."));
		return success;
	}

}
