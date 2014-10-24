package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NewDeathMsg extends Property
{
	@Override public String ID() { return "Prop_NewDeathMsg"; }
	@Override public String name(){ return "NewDeathMsg";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}

	@Override
	public String accountForYourself()
	{ return "Changed death msg";	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==msg.source())
		&&(msg.targetMessage()==null)
		&&(msg.othersMinor()==CMMsg.TYP_DEATH)
		&&(text().length()>0)
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().toUpperCase().indexOf("<S-NAME> IS DEAD")>0))
		{
			final int x=msg.othersMessage().indexOf("\n\r");
			if(x>=0)
			{
				msg.modify(msg.source(),msg.target(),msg.tool(),msg.sourceCode(),text()+msg.othersMessage().substring(x),
						   msg.targetCode(),msg.targetMessage(),
						   msg.othersCode(),text()+msg.othersMessage().substring(x));
			}
		}
		return super.okMessage(myHost,msg);
	}
}
