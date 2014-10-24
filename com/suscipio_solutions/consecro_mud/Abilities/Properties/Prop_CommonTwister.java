package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_CommonTwister extends Property
{
	@Override public String ID() { return "Prop_CommonTwister"; }
	@Override public String name(){ return "Common Twister";}
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_ITEMS|Ability.CAN_MOBS;}
	protected DVector changes=new DVector(3);

	@Override
	public String accountForYourself()
	{ return "Twists around what the gathering common skills gives you.";	}

	@Override
	public void setMiscText(String text)
	{
		super.setMiscText(text);
		changes.clear();
		final List<String> V=CMParms.parseSemicolons(text,true);
		for(int v=0;v<V.size();v++)
		{
			final String s=V.get(v);
			final String skill=CMParms.getParmStr(s,"SKILL","");
			final String mask=CMParms.getParmStr(s,"MASK","");
			if((skill.length()>0)&&(mask.length()>0))
				changes.addElement(skill,mask,s);
		}

	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(msg.tool() instanceof Ability)
		&&(msg.target()!=null)
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_COMMON_SKILL)
		&&((affected instanceof Room)||(affected instanceof Exit)||(affected instanceof Area)
		   ||((affected instanceof Item)&&(msg.source().isMine(affected)))
		   ||((affected instanceof MOB)&&(msg.source()==affected))))
		{
			final Vector poss=new Vector();
			final int randomResource=CMLib.dice().roll(1,RawMaterial.CODES.TOTAL()-1,0);
			if(text().length()==0)
			{
				final Item I=CMLib.materials().makeItemResource(randomResource);
				msg.target().setName(I.Name());
				msg.target().setDisplayText(I.displayText());
				if(msg.target() instanceof Item)
					((Item)msg.target()).setMaterial(I.material());
			}
			else
			for(int v=0;v<changes.size();v++)
			{
				if(((String)changes.elementAt(v,1)).equals("*")
				||(((String)changes.elementAt(v,1)).equalsIgnoreCase(msg.tool().ID())))
				{
					final String two=(String)changes.elementAt(v,2);
					if(two.equals("*")
					||(CMLib.english().containsString(msg.target().name(),two)))
						poss.addElement(changes.elementAt(v,3));
				}
			}
			if(poss.size()==0) return true;
			final String var=(String)poss.elementAt(CMLib.dice().roll(1,poss.size(),-1));
			final String newname=CMParms.getParmStr(var,"NAME","");
			final String newdisp=CMParms.getParmStr(var,"DISPLAY","");
			final String newmat=CMParms.getParmStr(var,"MATERIAL","");

			if(newname.length()>0)
			{
				if(newname.equals("*"))
				{
					final Item I=CMLib.materials().makeItemResource(randomResource);
					msg.target().setName(I.Name());
				}
				else
					msg.target().setName(newname);
			}
			if(newdisp.length()>0)
			{
				if(newdisp.equals("*"))
				{
					final Item I=CMLib.materials().makeItemResource(randomResource);
					msg.target().setDisplayText(I.displayText());
				}
				else
					msg.target().setDisplayText(newdisp);
			}
			if((newmat.length()>0)&&(msg.target() instanceof Item))
			{
				final String oldMatName=RawMaterial.CODES.NAME(((Item)msg.target()).material()).toLowerCase();
				int newMatCode=-1;
				if(newmat.equals("*"))
					newMatCode=randomResource;
				else
				{
					newMatCode=CMLib.materials().getResourceCode(newmat,false);
					if(newMatCode<0)
					{
						newMatCode=CMLib.materials().getMaterialCode(newmat,false);
						if(newMatCode>0) newMatCode=CMLib.materials().getRandomResourceOfMaterial(newMatCode);
					}
					if(newMatCode>=0)
					{
						((Item)msg.target()).setMaterial(newMatCode);
						final String newMatName=RawMaterial.CODES.NAME(newMatCode).toLowerCase();
						msg.target().setName(CMStrings.replaceAll(msg.target().name(),oldMatName,newMatName));
						msg.target().setDisplayText(CMStrings.replaceAll(msg.target().name(),oldMatName,newMatName));
						msg.target().setName(CMStrings.replaceAll(msg.target().name(),CMStrings.capitalizeAndLower(oldMatName),CMStrings.capitalizeAndLower(newMatName)));
						msg.target().setDisplayText(CMStrings.replaceAll(msg.target().name(),CMStrings.capitalizeAndLower(oldMatName),CMStrings.capitalizeAndLower(newMatName)));
						msg.target().setName(CMStrings.replaceAll(msg.target().name(),oldMatName.toUpperCase(),newMatName.toUpperCase()));
						msg.target().setDisplayText(CMStrings.replaceAll(msg.target().name(),oldMatName.toUpperCase(),newMatName.toUpperCase()));
					}
				}
			}
		}
		return super.okMessage(myHost,msg);
	}
}
