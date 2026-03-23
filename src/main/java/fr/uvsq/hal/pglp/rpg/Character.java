package fr.uvsq.hal.pglp.rpg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Set;
import java.util.*;


import static fr.uvsq.hal.pglp.rpg.Dice.d20;

/**
 * La classe <code>Character</code> représente un personnage de JdR.
 *
 * @author hal
 * @version 2022
 */
public class Character {
  private final Logger logger = LoggerFactory.getLogger(Character.class);

  /** Nom. */
  private final String name;

  /** Bonus de maîtrise. */
  private final int proficiencyBonus;

  /** Les scores de caractéristiques. */
  Map<Ability, AbilityScore> abilities;

  /** Les compétences maîtrisées. */
  Set<Skill> skills;

  /**
   * Construit un personnage à partir d'un builder.
   *
   * @param builder le builder
   */
  Character(CharacterBuilder builder) {
    this.name = builder.name;
    this.proficiencyBonus = builder.proficiencyBonus;
    this.abilities = builder.abilities;
    this.skills = builder.skills;
}

  /**
   * Retourne le nom du personnage.
   *
   * @return le nom
   */
  public String getName() {
    return name;
  }

  /**
   * Retourne le score d'une caractéristique.
   *
   * @param ability la caractéristique
   *
   * @return le score
   */
  public AbilityScore get(Ability ability) {
    return abilities.get(ability);
}

  /**
   * Retourne le bonus de maîtrise.
   *
   * @return le bonus de maîtrise
   */
  public int getProficiencyBonus() {
    return proficiencyBonus;
  }

  /**
   * Retourne le bonus de maîtrise spécifique à une compétence.
   *
   * @param skill la compétence
   * @return le bonus
   */
  public int getProficiencyBonusIn(Skill skill) {
    int modifier = abilities.get(skill.getAbility()).getModifier();
    if (isProficientIn(skill)) {
        return modifier + proficiencyBonus;
    }
    return modifier;
}

  /**
   * Vérifie si le personnage maîtrise la compétence.
   *
   * @param skill la compétence
   * @return true si le personnage maîtrise la compétence, false sinon
   */
  public boolean isProficientIn(Skill skill) {
    return skills.contains(skill);
}
}
