package com.suscipio_solutions.consecro_mud.Common.interfaces;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


/**
 * A LegalWarrant is an important object in the triad of LegalBehavior
 * and Law objects, as a part of the CoffeeMud legal system.  A LegalWarrant
 * is created by the LegalBehavior in response to the Law definitions, when
 * it has determined that a mob or player has broken one of the Laws.  The
 * Warrant is then used as a state object following the specific circumstances
 * around the violated law, and its adjudication.  After adjudication, the
 * object is transferred into an Old Warrants cue under the Law to use as
 * a reference for repeat offenders.
 *
 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior
 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law
 */
public interface LegalWarrant extends CMCommon
{
	/**
	 * If the criminal has been identified by an otherwise unoccupied
	 * officer, this will be that officer.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setArrestingOfficer(Area, MOB)
	 *
	 * @return the actual living mob officer who will enforce the warrant
	 */
	public MOB arrestingOfficer();

	/**
	 * If the criminal has been identified by an otherwise unoccupied
	 * officer, this will set that officer.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#arrestingOfficer()
	 *
	 * @param legalArea the Area governing the law
	 * @param mob the actual living mob officer who will enforce the warrant
	 */
	public void setArrestingOfficer(Area legalArea, MOB mob);

	/**
	 * The actual player/mob criminal accused of the crime.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setCriminal(MOB)
	 *
	 * @return the actual player/mob criminal accused of the crime.
	 */
	public MOB criminal();

	/**
	 * Sets the actual player/mob criminal accused of the crime.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#criminal()
	 *
	 * @param mob the actual player/mob criminal accused of the crime.
	 */
	public void setCriminal(MOB mob);

	/**
	 * If applicable, the victim of the crime (or null)
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setVictim(MOB)
	 *
	 * @return the victim of the crime, or null
	 */
	public MOB victim();

	/**
	 * If applicable, sets the victim of the crime (or null)
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#victim()
	 *
	 * @param mob the victim of the crime, or null
	 */
	public void setVictim(MOB mob);

	/**
	 * If applicable, gets the witness of the crime (or null)
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setWitness(MOB)
	 *
	 * @return the witness of the crime or null
	 */
	public MOB witness();

	/**
	 * If applicable, sets the witness of the crime (or null)
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#witness()
	 *
	 * @param mob the witness of the crime (or null)
	 */
	public void setWitness(MOB mob);

	/**
	 * The crime name
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setCrime(String)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#BIT_CRIMENAME
	 *
	 * @return the crime name
	 */
	public String crime();

	/**
	 * Sets the crime name
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#crime()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#BIT_CRIMENAME
	 *
	 * @param crime the crime name
	 */
	public void setCrime(String crime);

	/**
	 * Gets the full punishment code for the crime
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setPunishment(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#getPunishmentParm(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENT_DESCS
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENTMASK_DESCS
	 *
	 * @return the full punishment code for the crime
	 */
	public int punishment();

	/**
	 * Sets the full punishment code for the crime
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#punishment()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#getPunishmentParm(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENT_DESCS
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENTMASK_DESCS
	 *
	 * @param code the full punishment code for the crime
	 */
	public void setPunishment(int code);

	/**
	 * If applicable, returns any parameters associated with a particular
	 * bitmask on the punishment.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#punishment()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setPunishment(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENTMASK_DESCS
	 *
	 * @param code the punishment mask bitmap code
	 * @return any parameters needed, or null
	 */
	public String getPunishmentParm(int code);

	/**
	 * If applicable, sets any parameters associated with a particular
	 * bitmask on the punishment.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#getPunishmentParm(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#punishment()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setPunishment(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#PUNISHMENTMASK_DESCS
	 *
	 * @param code the punishment mask bitmap code
	 * @param parm the parameter to set
	 */
	public void addPunishmentParm(int code, String parm);

	/**
	 * If applicable, returns the number of ticks the punishment calls
	 * for the criminal to remain in jail.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setJailTime(int)
	 *
	 * @return the number of ticks to be jailed for
	 */
	public int jailTime();

	/**
	 * If applicable, sets the number of ticks the punishment calls
	 * for the criminal to remain in jail.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#jailTime()
	 *
	 * @param time the number of ticks to be jailed for
	 */
	public void setJailTime(int time);

	/**
	 * Returns the current state of adjudication.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setState(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#STATE_ARRESTING
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#STATE_SEEKING
	 *
	 * @return the current state of adjudication
	 */
	public int state();

	/**
	 * Sets the current state of adjudication.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#state()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#STATE_DETAINING
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#STATE_EXECUTING
	 *
	 * @param state the current state of adjudication.
	 */
	public void setState(int state);

	/**
	 * Returns the number of times this criminal has done this crime.  This
	 * is usually 0 unless it also exists in the old warrants list.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setOffenses(int)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#oldWarrants()
	 *
	 * @return the number of times this criminal has committed this crime.
	 */
	public int offenses();

	/**
	 * Sets the number of times this criminal has done this crime.  This
	 * is usually 0 unless it also exists in the old warrants list.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#offenses()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#oldWarrants()
	 *
	 * @param num the number of times this criminal has committed this crime.
	 */
	public void setOffenses(int num);

	/**
	 * Returns the last time, in miliseconds since 1970, the criminal has
	 * committed this crime.  Very useful in oldWarrants
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setLastOffense(long)
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#oldWarrants()
	 *
	 * @return the last time this crime was done, in milis
	 */
	public long lastOffense();

	/**
	 * Sets the last time, in miliseconds since 1970, the criminal has
	 * committed this crime.  Very useful in oldWarrants
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#lastOffense()
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.Law#oldWarrants()
	 *
	 * @param last the last time this crime was done, in milis
	 */
	public void setLastOffense(long last);

	/**
	 * Returns the time, in milis since 1970, that a trip to the judge
	 * or to the jail was started.  For housekeeping purposes.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setTravelAttemptTime(long)
	 *
	 * @return the milis time since the trip began
	 */
	public long travelAttemptTime();

	/**
	 * Sets the time, in milis since 1970, that a trip to the judge
	 * or to the jail was started.  For housekeeping purposes.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#travelAttemptTime()
	 *
	 * @param time the milis time since the trip began
	 */
	public void setTravelAttemptTime(long time);

	/**
	 * Returns the warning message given to the criminal by the officer.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setWarnMsg(String)
	 *
	 * @return the warning msg given
	 */
	public String warnMsg();

	/**
	 * Sets the warning message given to the criminal by the officer.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#warnMsg()
	 *
	 * @param msg the warning msg given
	 */
	public void setWarnMsg(String msg);

	/**
	 * Sets the room into which this criminal will be jailed.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#jail()
	 *
	 * @param R the room into which this criminal will be jailed.
	 */
	public void setJail(Room R);

	/**
	 * Returns the room into which this criminal will be jailed.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setJail(Room)
	 *
	 * @return the room into which this criminal will be jailed.
	 */
	public Room jail();

	/**
	 * Returns the room into which this criminal will be released after jail.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#setReleaseRoom(Room)
	 *
	 * @return the room into which this criminal will be released after jail.
	 */
	public Room releaseRoom();

	/**
	 * Sets the room into which this criminal will be released after jail.
	 *
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant#releaseRoom()
	 *
	 * @param R the room into which this criminal will be released after jail.
	 */
	public void setReleaseRoom(Room R);
}
