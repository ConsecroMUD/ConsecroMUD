package com.suscipio_solutions.consecro_mud.Abilities.interfaces;
import java.util.List;
import java.util.Map;



/**
 * A Language ability represents both the ability to understand one or more
 * spoken or written languages, and the ability to speak one or more spoken
 * languages.  A single ability usually represents a single language, but
 * may support multiple simultaneously.
 */
public interface Language extends Ability
{
	/**
	 * Returns the name of this language when it is in written form.
	 * This is usually the same as the spoken form.
	 * @return the name of this language when it is in written form.
	 */
	public String writtenName();
	/**
	 * Returns a Vector of the languages understood by this ability
	 * @return vector of language ids supported (usually 1 element == ID())
	 */
	public List<String> languagesSupported();
	/**
	 * Returns whether the given language is translated by this one
	 * @return true if this language translates (usually ID() == language)
	 */
	public boolean translatesLanguage(String language);
	/**
	 * Returns the understanding profficiency in the given supported language
	 * @param language the language to test for (usually ID())
	 * @return the profficiency of this ability in the language (0-100)
	 */
	public int getProficiency(String language);
	/**
	 * Returns whether this language is currently being spoken
	 * @param language the language to test for (usually ID())
	 * @return true if spoken
	 */
	public boolean beingSpoken(String language);
	/**
	 * Changes whether this language is currently being spoken
	 * @param language the language to set (usually ID())
	 * @param beingSpoken whether it is being spoken
	 */
	public void setBeingSpoken(String language, boolean beingSpoken);
	/**
	 * Returns the direct word<->word translation hashtable
	 * @param language the language to translate directory (usually ID())
	 * @return the hashtable of word-word translations
	 */
	public Map<String, String> translationHash(String language);
	/**
	 * Returns the word-length rough-translation vector of string arrays for the given language
	 * The first string array in the vector represents 1 letter words, the second 2,
	 * and so forth.
	 * @param language the language to return the vector for (usually ID())
	 * @return the vector of word-length rough translation string arrays
	 */
	public List<String[]> translationVector(String language);
	/**
	 * Returns a language translation of the given word in the given language
	 * @param language the language to use (usually ID())
	 * @param word the word to translate
	 * @return the translated word
	 */
	public String translate(String language, String word);
}
