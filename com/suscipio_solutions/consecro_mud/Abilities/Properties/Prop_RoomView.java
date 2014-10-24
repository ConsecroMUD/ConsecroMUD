package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_RoomView extends Property
{
	@Override public String ID() { return "Prop_RoomView"; }
	@Override public String name(){ return "Different Room View";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	protected Room newRoom=null;

	@Override
	public String accountForYourself()
	{ return "Different View of "+text();	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((newRoom==null)
		||(newRoom.amDestroyed())
		||(!CMLib.map().getExtendedRoomID(newRoom).equalsIgnoreCase(text().trim())))
			newRoom=CMLib.map().getRoom(text());
		if(newRoom==null) return super.okMessage(myHost,msg);

		if((affected!=null)
		&&((affected instanceof Room)||(affected instanceof Exit)||(affected instanceof Item))
		&&(msg.amITarget(affected))
		&&(newRoom.fetchEffect(ID())==null)
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE)))
		{
			final CMMsg msg2=CMClass.getMsg(msg.source(),newRoom,msg.tool(),
						  msg.sourceCode(),msg.sourceMessage(),
						  msg.targetCode(),msg.targetMessage(),
						  msg.othersCode(),msg.othersMessage());
			if(newRoom.okMessage(msg.source(),msg2))
			{
				newRoom.executeMsg(msg.source(),msg2);
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

}
