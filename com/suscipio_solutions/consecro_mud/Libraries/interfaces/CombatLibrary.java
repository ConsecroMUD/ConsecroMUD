package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

public interface CombatLibrary extends CMLibrary
{
	public static final int COMBAT_DEFAULT=0;
	public static final int COMBAT_QUEUE=1;
	public static final int COMBAT_MANUAL=2;
	public static final int COMBAT_TURNBASED=3;

	public Set<MOB> allPossibleCombatants(MOB mob, boolean beRuthless);
	public Set<MOB> properTargets(Ability A, MOB caster, boolean beRuthless);
	public int adjustedArmor(MOB mob);
	public int adjustedAttackBonus(MOB mob, MOB target);
	public int adjustedDamage(MOB mob, Weapon weapon, MOB target, int bonusDamage, boolean allowCrits);
	public boolean rollToHit(MOB attacker, MOB defender);
	public boolean rollToHit(int attack, int defence, int adjustment);
	public Set<MOB> allCombatants(MOB mob);
	public void makePeaceInGroup(MOB mob);
	public void forcePeaceAllFightingAgainst(final MOB mob, final Set<MOB> exceptionSet);
	public Set<MOB> getAllFightingAgainst(final MOB mob, Set<MOB> set);
	public void postPanic(MOB mob, CMMsg addHere);
	public void postDeath(MOB killerM, MOB deadM, CMMsg addHere);
	public boolean postAttack(MOB attacker, MOB target, Item weapon);
	public boolean postHealing(MOB healer, MOB target, Environmental tool, int messageCode, int healing, String allDisplayMessage);
	public String replaceDamageTag(String str, int damage, int damageType, char sourceTargetSTO);
	public void postDamage(MOB attacker, MOB target, Environmental weapon, int damage, int messageCode, int damageType, String allDisplayMessage);
	public void postWeaponDamage(MOB source, MOB target, Item item, boolean success);
	public void postItemDamage(MOB mob, Item I, Environmental tool, int damageAmount, int messageType, String message);
	public void processFormation(List<MOB>[] done, MOB leader, int level);
	public MOB getFollowedLeader(MOB mob);
	public List<MOB>[] getFormation(MOB mob);
	public List<MOB> getFormationFollowed(MOB mob);
	public int getFormationAbsOrder(MOB mob);
	public CharClass getCombatDominantClass(MOB killer, MOB killed);
	public Set<MOB> getCombatDividers(MOB killer, MOB killed, CharClass combatCharClass);
	public Set<MOB> getCombatBeneficiaries(MOB killer, MOB killed, CharClass combatCharClass);
	public DeadBody justDie(MOB source, MOB target);
	public String armorStr(MOB mob);
	public String standardHitWord(int type, int damage);
	public String fightingProwessStr(MOB mob);
	public String standardMissString(int weaponType, int weaponClassification, String weaponName, boolean useExtendedMissString);
	public String standardHitString(int weaponType, int weaponClass, int damageAmount,  String weaponName);
	public String standardMobCondition(MOB viewer, MOB mob);
	public void resistanceMsgs(CMMsg msg, MOB source, MOB target);
	public void establishRange(MOB source, MOB target, Environmental tool);
	public void makeFollowersFight(MOB observer, MOB target, MOB source);
	public void handleBeingHealed(CMMsg msg);
	public void handleBeingDamaged(CMMsg msg);
	public void handleBeingAssaulted(CMMsg msg);
	public void handleDeath(CMMsg msg);
	public void doDeathPostProcessing(CMMsg msg);
	public void handleObserveDeath(MOB observer, MOB fighting, CMMsg msg);
	public boolean isKnockedOutUponDeath(MOB mob, MOB fighting);
	public boolean handleConsequences(MOB mob, MOB fighting, String[] commands, int[] lostExperience, String message);
	public void tickCombat(MOB fighter);
	public void recoverTick(MOB mob);
	public boolean doTurnBasedCombat(final MOB mob, final Room R, final Area A);
	public void expendEnergy(final MOB mob, final boolean expendMovement);

	/**
	 * For a valid set of killers who are benefitting from having killed the given killed mob,
	 * this method will make repeated postExperience calls after having calculated their
	 * exp bounty for the kill.
	 * @see ExpLevelLibrary#postExperience(MOB, MOB, String, int, boolean)
	 * @param killers a set of mobs to benefit from the kill
	 * @param dividers a set of mobs who must divide the xp.. usually subset of killers
	 * @param killed the mob killed
	 */
	public void dispenseExperience(Set<MOB> killers, Set<MOB> dividers, MOB killed);
}
