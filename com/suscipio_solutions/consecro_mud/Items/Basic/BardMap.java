package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Hashtable;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings({"unchecked","rawtypes"})
public class BardMap extends GenMap
{
	@Override public String ID(){	return "BardMap";}
	public BardMap()
	{
		super();
		setName("a map");
		basePhyStats.setWeight(0);
		setDisplayText("a map is rolled up here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(3);
		setMaterial(RawMaterial.RESOURCE_PAPER);
		recoverPhyStats();
	}

	@Override
	public void doMapArea()
	{
		//myMap=null;
	}

	@Override
	public StringBuffer[][] getMyMappedRoom(int width)
	{
		StringBuffer[][] myMap=null;
		//if(myMap!=null)	return myMap;
		myMap=finishMapMaking(width);
		return myMap;
	}

	@Override
	public Hashtable makeMapRooms(int width)
	{
		final String newText=getMapArea();
		final List<String> mapAreas=CMParms.parseSemicolons(newText,true);
		final Hashtable mapRooms=new Hashtable();
		for(int a=0;a<mapAreas.size();a++)
		{
			final String area=mapAreas.get(a);
			final Room room=CMLib.map().getRoom(area);
			if(room!=null)
			{
				final MapRoom mr=new MapRoom();
				mr.r=room;
				mapRooms.put(room,mr);
			}
		}
		super.clearTheSkys(mapRooms);
		return mapRooms;
	}
}
