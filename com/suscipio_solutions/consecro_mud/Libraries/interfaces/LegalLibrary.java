package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Law;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

public interface LegalLibrary extends CMLibrary
{
	public Law getTheLaw(Room R, MOB mob);
	public LegalBehavior getLegalBehavior(Area A);
	public LegalBehavior getLegalBehavior(Room R);
	public Area getLegalObject(Area A);
	public Area getLegalObject(Room R);
	public String getLandOwnerName(Room room);

	public LandTitle getLandTitle(Area area);
	public LandTitle getLandTitle(Room room);
	public boolean isRoomSimilarlyTitled(LandTitle title, Room R);
	public Set<Room> getHomePeersOnThisFloor(Room room, Set<Room> doneRooms);
	public boolean isHomeRoomDownstairs(Room room);
	public boolean isHomeRoomUpstairs(Room room);
	public boolean doesHavePriviledgesHere(MOB mob, Room room);
	public boolean doesAnyoneHavePrivilegesHere(MOB mob, String overrideID, Room R);
	public boolean doesHavePriviledgesInThisDirection(MOB mob, Room room, Exit exit);
	public boolean doesOwnThisProperty(String name, Room room);
	public boolean doesOwnThisProperty(MOB mob, Room room);
	public List<LandTitle> getAllUniqueTitles(Enumeration<Room> e, String owner, boolean includeRentals);
	public Ability getClericInfusion(Physical room);
	public Deity getClericInfused(Room room);

	public boolean isLegalOfficerHere(MOB mob);
	public boolean isLegalJudgeHere(MOB mob);
	public boolean isLegalOfficialHere(MOB mob);

	public boolean isACity(Area A);
}
