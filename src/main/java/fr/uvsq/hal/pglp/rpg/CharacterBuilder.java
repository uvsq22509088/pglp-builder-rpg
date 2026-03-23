package fr.uvsq.hal.pglp.rpg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * La classe <code>CharacterBuilder</code> permet de créer un personnage.
 *
 * @author hal
 * @version 2022
 */
public class CharacterBuilder {
  /** Somme minimum des scores de caractéristiques pour la génération aléatoire. */
  public static final int MIN_SUM_SCORE = 60;

  /** Somme maximum des scores de caractéristiques pour la génération aléatoire. */
  public static final int MAX_SUM_SCORE = 80;

  /** Scores prédéfinis de caractéristiques pour la création non aléatoire. */
  private static final int[] PREDEFINED_SCORES = { 15, 14, 13, 12, 10, 8 };

  /** Bonus de maîtrise au niveau 1. */
  public static final int FIRST_LEVEL_PROFICIENCY_BONUS = 2;

  private static final String MSG_NAME_MANDATORY = "A character should have a name.";
  private static final String MSG_NAME_NOT_BLANK = "A character name should not be blank.";
  private static final String MSG_ORDER_MANDATORY = "The order between abilities have to be defined.";
  private static final String MSG_ORDER_VALID = "The abilities order should mention each ability once and only once.";
  private static final String MSG_SKILL_MANDATORY = "The skill is mandatory.";

  private final Logger logger = LoggerFactory.getLogger(CharacterBuilder.class);

  final String name;
  int proficiencyBonus;
  Map<Ability, AbilityScore> abilities;
  Set<Skill> skills;

  /**
   * Crée un personnage en générant les caractéristiques de manière aléatoire.
   * Les scores générés sont attribués en ordre décroissant dans les caractéristiques
   * (Force, Dextérité, Constitution, Intelligence, Sagesse, Charisme).
   *
   * @param name le nom du personnage
   */
  public CharacterBuilder(String name) {
    this(name, Ability.values());
  }

  /**
   * Crée un personnage en générant les caractéristiques de manière aléatoire.
   * Les scores générés sont attribués en ordre décroissant selon l'ordre
   * des caractéristiques fourni en paramètre.
   *
   * @param name le nom du personnage
   * @param abilitiesOrder l'ordre de préférence des caractéristiques
   *                       (chaque caractéristique doit être mentionnée une et une seule fois)
   */
  public CharacterBuilder(String name, Ability[] abilitiesOrder) {
    if (name == null) {
    throw new NullPointerException(MSG_NAME_MANDATORY);
    }   
    if (name.isBlank()) {
        throw new IllegalArgumentException(MSG_NAME_NOT_BLANK);
    }
    if (abilitiesOrder == null) {
        throw new IllegalArgumentException(MSG_ORDER_MANDATORY);
    }
    Set<Ability> seen = EnumSet.noneOf(Ability.class);
  for (Ability ability : abilitiesOrder) {
    if (!seen.add(ability)) {
        throw new IllegalArgumentException("duplicate element: " + ability);
    }
  }
    if (seen.size() != Ability.values().length) {
       throw new IllegalArgumentException(MSG_ORDER_VALID);
    }

    this.name = name;

    // Générer les stats — recommencer si somme pas entre 60 et 80
    AbilityScore[] scores;
    int sum;
    do {
        scores = new AbilityScore[Ability.values().length];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = new AbilityScore();
        }
        sum = Arrays.stream(scores)
            .mapToInt(AbilityScore::getScore)
            .sum();
        logger.debug("Sum of ability scores : {}", sum);
    } while (sum < MIN_SUM_SCORE || sum > MAX_SUM_SCORE);

    // Trier du plus grand au plus petit
    Arrays.sort(scores, Comparator.reverseOrder());

    // Attribuer selon l'ordre de préférence
    abilities = new EnumMap<>(Ability.class);
    for (int i = 0; i < abilitiesOrder.length; i++) {
        abilities.put(abilitiesOrder[i], scores[i]);
    }

    // Compétences vides pour l'instant
    skills = EnumSet.noneOf(Skill.class);

    // Bonus de maîtrise par défaut
    this.proficiencyBonus = FIRST_LEVEL_PROFICIENCY_BONUS;
}

  /**
   * Crée le personnage.
   *
   * @return le personnage
   */
  public Character build() {
    return new Character(this);
}

  /**
   * Affecte les scores prédéfinis aux caractéristiques dans l'ordre précisé.
   *
   * @param abilitiesOrder l'ordre de préférence des caractéristiques
   *                       (chaque caractéristique doit être mentionnée une et une seule fois)
   *
   * @return le builder
   */
  public CharacterBuilder nonRamdomAbilities(Ability[] abilitiesOrder) {
    for (int i = 0; i < abilitiesOrder.length; i++) {
        abilities.put(abilitiesOrder[i], new AbilityScore(PREDEFINED_SCORES[i]));
    }
    return this;
}

  /**
   * Fixe le score d'une caractéristique.
   *
   * @param ability la caractéristique concernée
   * @param score le score à affecter
   *
   * @return le builder
   */
  public CharacterBuilder setAbility(Ability ability, int score) {
    abilities.put(ability, new AbilityScore(score));
    return this;
}

  /**
   * Fixe le bonus de maîtrise du personnage.
   *
   * @param proficiencyBonus le bonus de maîtrise
   *
   * @return le builder
   */
  public CharacterBuilder setProficiencyBonus(int proficiencyBonus) {
    this.proficiencyBonus = proficiencyBonus;
    return this;
}

  /**
   * Indique les compétences que le personnage maîtrise.
   *
   * @param skills les compétences
   * @return le builder
   */
  public CharacterBuilder isProficientIn(Skill... skills) {
    for (Skill skill : skills) {
        this.skills.add(skill);
    }
    return this;
}
}
