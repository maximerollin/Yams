package io.github.maximerollin.yams.feature.game.play

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.feature.game.play.model.PlayerState

internal val AllFiveDiceScoreOptions: List<Int> = listOf(0) + (5..30).toList()
internal val MatchingThreeDiceScoreOptions: List<Int> = listOf(0) + (1..6).map { it * 3 }
internal val MatchingFourDiceScoreOptions: List<Int> = listOf(0) + (1..6).map { it * 4 }
internal val ThreeOfAKindAllDiceScoreOptions: List<Int> =
    possibleAllDiceScoreOptions(minMatchingDiceCount = 3)
internal val FourOfAKindAllDiceScoreOptions: List<Int> =
    possibleAllDiceScoreOptions(minMatchingDiceCount = 4)

private fun possibleAllDiceScoreOptions(minMatchingDiceCount: Int): List<Int> = buildSet {
    add(0)
    for (firstDie in 1..6) {
        for (secondDie in 1..6) {
            for (thirdDie in 1..6) {
                for (fourthDie in 1..6) {
                    for (fifthDie in 1..6) {
                        val dice = listOf(firstDie, secondDie, thirdDie, fourthDie, fifthDie)
                        if (dice.groupingBy { it }.eachCount().values.any { it >= minMatchingDiceCount }) {
                            add(dice.sum())
                        }
                    }
                }
            }
        }
    }
}.toList().sorted()

internal fun fixedScore(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
): Int? = when (scoring) {
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> fixedValue ?: 0

    else -> null
}

internal fun upperScoreOptions(dieValue: Int): List<Int> =
    (0..5).map { count -> count * dieValue }

internal fun selectableScoreOptions(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
    allFiveDiceOptions: List<Int>,
): List<Int> = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> allFiveDiceOptions
    GameSettings.SettingsScoring.SUM_MATCHING_THREE -> MatchingThreeDiceScoreOptions
    GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> MatchingFourDiceScoreOptions
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> listOf(0, fixedValue ?: 0).distinct().sorted()

    null -> emptyList()
}

internal fun scoreSelectionOptions(
    row: ScoreRowUi,
    columnIndex: Int,
    selectedPlayer: PlayerState,
    settings: GameSettings,
): List<ScoreSelectionOption> {
    val isExtraFiveOfAKindBonusAvailable =
        settings.isExtraFiveOfAKindEnabled &&
            settings.extraFiveOfAKindValue != null &&
            row.key != ScoreKey.EXTRA_FIVE_OF_A_KIND &&
            row.key != ScoreKey.FIVE_OF_A_KIND &&
            (selectedPlayer.valuesFor(ScoreKey.FIVE_OF_A_KIND, settings.columnCount.coerceAtLeast(1))
                .getOrNull(columnIndex) ?: 0) > 0

    return row.scoreOptions.flatMap { score ->
        when {
            !isExtraFiveOfAKindBonusAvailable -> {
                listOf(ScoreSelectionOption(score = score))
            }

            fiveOfAKindDetection(row, score, settings) == FiveOfAKindDetection.CERTAIN -> {
                listOf(
                    ScoreSelectionOption(
                        score = score,
                        awardsExtraFiveOfAKindBonus = true,
                    )
                )
            }

            fiveOfAKindDetection(row, score, settings) == FiveOfAKindDetection.POSSIBLE -> {
                listOf(
                    ScoreSelectionOption(score = score),
                    ScoreSelectionOption(
                        score = score,
                        awardsExtraFiveOfAKindBonus = true,
                    ),
                )
            }

            else -> {
                listOf(ScoreSelectionOption(score = score))
            }
        }
    }
}

internal fun fiveOfAKindDetection(
    row: ScoreRowUi,
    score: Int,
    settings: GameSettings,
): FiveOfAKindDetection {
    if (score <= 0) return FiveOfAKindDetection.NONE

    return when (row.key) {
        ScoreKey.ONES,
        ScoreKey.TWOS,
        ScoreKey.THREES,
        ScoreKey.FOURS,
        ScoreKey.FIVES,
        ScoreKey.SIXES -> {
            val dieValue = upperRowDieValue(row.key) ?: return FiveOfAKindDetection.NONE
            if (score == dieValue * 5) {
                FiveOfAKindDetection.CERTAIN
            } else {
                FiveOfAKindDetection.NONE
            }
        }

        ScoreKey.THREE_OF_A_KIND -> scoringFiveOfAKindDetection(
            scoring = settings.threeOfAKindScoring,
            fixedValue = settings.threeOfAKindValue,
            score = score,
            allowFixedScore = settings.jokerRule,
        )

        ScoreKey.FOUR_OF_A_KIND -> scoringFiveOfAKindDetection(
            scoring = settings.fourOfAKindScoring,
            fixedValue = settings.fourOfAKindValue,
            score = score,
            allowFixedScore = settings.jokerRule,
        )

        ScoreKey.FULL_HOUSE ->
            if (settings.jokerRule && score == settings.fullHouseValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.SMALL_STRAIGHT ->
            if (settings.jokerRule && score == settings.smallStraightValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.LARGE_STRAIGHT ->
            if (settings.jokerRule && score == settings.largeStraightValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.CHANCE -> scoringFiveOfAKindDetection(
            scoring = settings.chanceValue,
            fixedValue = null,
            score = score,
            allowFixedScore = false,
        )

        else -> settings.customGameSettings
            .firstOrNull { ScoreKey.custom(it.id) == row.key }
            ?.let { rule ->
                scoringFiveOfAKindDetection(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                    score = score,
                    allowFixedScore = true,
                )
            }
            ?: FiveOfAKindDetection.NONE
    }
}

private fun scoringFiveOfAKindDetection(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
    score: Int,
    allowFixedScore: Boolean,
): FiveOfAKindDetection = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE ->
        if (score in AllFiveDiceScoreOptions && score % 5 == 0) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.SUM_MATCHING_THREE ->
        if (score in MatchingThreeDiceScoreOptions) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.SUM_MATCHING_FOUR ->
        if (score in MatchingFourDiceScoreOptions) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM ->
        if (allowFixedScore && score == fixedValue) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    null -> FiveOfAKindDetection.NONE
}

internal fun upperRowDieValue(key: ScoreKey): Int? = when (key) {
    ScoreKey.ONES -> 1
    ScoreKey.TWOS -> 2
    ScoreKey.THREES -> 3
    ScoreKey.FOURS -> 4
    ScoreKey.FIVES -> 5
    ScoreKey.SIXES -> 6
    else -> null
}

internal fun upperBonus(subtotal: Int, settings: GameSettings): Int {
    if (!settings.isUpperBonusEnabled) return 0
    return if (subtotal >= settings.upperBonusThreshold) settings.upperBonusValue else 0
}

internal enum class FiveOfAKindDetection {
    NONE,
    POSSIBLE,
    CERTAIN,
}

internal data class DiceScoreCell(
    val key: ScoreKey,
    val columnIndex: Int,
    val score: Int,
    val valueWithBonuses: Int,
    val awardsExtraFiveOfAKindBonus: Boolean,
)

internal data class DiceScoreContext(
    val settings: GameSettings,
    val scoreEntries: Map<ScoreKey, List<Int?>>,
    val extraFiveOfAKindScores: List<Int> = emptyList(),
)

internal fun scoreDiceForOpenCells(
    dice: List<Int>,
    context: DiceScoreContext,
): List<DiceScoreCell> {
    if (dice.size != 5 || dice.any { it !in 1..6 }) return emptyList()

    val settings = context.settings
    val columnCount = settings.columnCount.coerceAtLeast(1)
    return scoreRules(settings).flatMap { rule ->
        (0 until columnCount).mapNotNull { columnIndex ->
            if (context.scoreEntries[rule.key]?.getOrNull(columnIndex) != null) {
                return@mapNotNull null
            }

            val score = scoreDiceForRule(dice = dice, rule = rule, settings = settings)
            val upperBonusDelta = upperBonusDelta(
                diceScore = score,
                rule = rule,
                columnIndex = columnIndex,
                context = context,
            )
            val awardsExtraFiveOfAKindBonus = awardsExtraFiveOfAKindBonus(
                dice = dice,
                rule = rule,
                columnIndex = columnIndex,
                context = context,
            )
            DiceScoreCell(
                key = rule.key,
                columnIndex = columnIndex,
                score = score,
                valueWithBonuses = score +
                    upperBonusDelta +
                    if (awardsExtraFiveOfAKindBonus) settings.extraFiveOfAKindValue ?: 0 else 0,
                awardsExtraFiveOfAKindBonus = awardsExtraFiveOfAKindBonus,
            )
        }
    }
}

private data class DiceScoreRule(
    val key: ScoreKey,
    val kind: DiceScoreRuleKind,
    val scoring: GameSettings.SettingsScoring? = null,
    val fixedValue: Int? = null,
)

private enum class DiceScoreRuleKind {
    UPPER,
    THREE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    SMALL_STRAIGHT,
    LARGE_STRAIGHT,
    FIVE_OF_A_KIND,
    CHANCE,
}

private fun scoreRules(settings: GameSettings): List<DiceScoreRule> = buildList {
    add(DiceScoreRule(key = ScoreKey.ONES, kind = DiceScoreRuleKind.UPPER))
    add(DiceScoreRule(key = ScoreKey.TWOS, kind = DiceScoreRuleKind.UPPER))
    add(DiceScoreRule(key = ScoreKey.THREES, kind = DiceScoreRuleKind.UPPER))
    add(DiceScoreRule(key = ScoreKey.FOURS, kind = DiceScoreRuleKind.UPPER))
    add(DiceScoreRule(key = ScoreKey.FIVES, kind = DiceScoreRuleKind.UPPER))
    add(DiceScoreRule(key = ScoreKey.SIXES, kind = DiceScoreRuleKind.UPPER))

    if (settings.isThreeOfAKindEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.THREE_OF_A_KIND,
                kind = DiceScoreRuleKind.THREE_OF_A_KIND,
                scoring = settings.threeOfAKindScoring,
                fixedValue = settings.threeOfAKindValue,
            )
        )
    }
    if (settings.isFourOfAKindEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.FOUR_OF_A_KIND,
                kind = DiceScoreRuleKind.FOUR_OF_A_KIND,
                scoring = settings.fourOfAKindScoring,
                fixedValue = settings.fourOfAKindValue,
            )
        )
    }
    if (settings.isFullHouseEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.FULL_HOUSE,
                kind = DiceScoreRuleKind.FULL_HOUSE,
                fixedValue = settings.fullHouseValue,
            )
        )
    }
    if (settings.isSmallStraightEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.SMALL_STRAIGHT,
                kind = DiceScoreRuleKind.SMALL_STRAIGHT,
                fixedValue = settings.smallStraightValue ?: 0,
            )
        )
    }
    if (settings.isLargeStraightEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.LARGE_STRAIGHT,
                kind = DiceScoreRuleKind.LARGE_STRAIGHT,
                fixedValue = settings.largeStraightValue ?: 0,
            )
        )
    }
    if (settings.isFiveOfAKindEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.FIVE_OF_A_KIND,
                kind = DiceScoreRuleKind.FIVE_OF_A_KIND,
                fixedValue = settings.fiveOfAKindValue,
            )
        )
    }
    if (settings.isChanceEnabled) {
        add(
            DiceScoreRule(
                key = ScoreKey.CHANCE,
                kind = DiceScoreRuleKind.CHANCE,
                scoring = settings.chanceValue,
            )
        )
    }
}

private fun scoreDiceForRule(
    dice: List<Int>,
    rule: DiceScoreRule,
    settings: GameSettings,
): Int = when (rule.kind) {
    DiceScoreRuleKind.UPPER -> {
        val dieValue = upperRowDieValue(rule.key) ?: 0
        dice.count { it == dieValue } * dieValue
    }

    DiceScoreRuleKind.THREE_OF_A_KIND ->
        scoreMatchingDice(
            dice = dice,
            requiredCount = 3,
            scoring = rule.scoring,
            fixedValue = rule.fixedValue,
        )

    DiceScoreRuleKind.FOUR_OF_A_KIND ->
        scoreMatchingDice(
            dice = dice,
            requiredCount = 4,
            scoring = rule.scoring,
            fixedValue = rule.fixedValue,
        )

    DiceScoreRuleKind.FULL_HOUSE ->
        if (isFullHouse(dice) || (settings.jokerRule && isFiveOfAKind(dice))) {
            rule.fixedValue ?: 0
        } else {
            0
        }

    DiceScoreRuleKind.SMALL_STRAIGHT ->
        if (isSmallStraight(dice) || (settings.jokerRule && isFiveOfAKind(dice))) {
            rule.fixedValue ?: 0
        } else {
            0
        }

    DiceScoreRuleKind.LARGE_STRAIGHT ->
        if (isLargeStraight(dice) || (settings.jokerRule && isFiveOfAKind(dice))) {
            rule.fixedValue ?: 0
        } else {
            0
        }

    DiceScoreRuleKind.FIVE_OF_A_KIND ->
        if (isFiveOfAKind(dice)) rule.fixedValue ?: 0 else 0

    DiceScoreRuleKind.CHANCE -> scoreBySettingsScoring(
        dice = dice,
        scoring = rule.scoring,
        fixedValue = rule.fixedValue,
        matchingCount = null,
    )
}

private fun scoreMatchingDice(
    dice: List<Int>,
    requiredCount: Int,
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
): Int {
    val counts = dice.groupingBy { it }.eachCount()
    if (counts.values.none { it >= requiredCount }) return 0
    return scoreBySettingsScoring(
        dice = dice,
        scoring = scoring,
        fixedValue = fixedValue,
        matchingCount = requiredCount,
    )
}

private fun scoreBySettingsScoring(
    dice: List<Int>,
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
    matchingCount: Int?,
): Int = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> dice.sum()
    GameSettings.SettingsScoring.SUM_MATCHING_THREE ->
        matchingScore(dice = dice, matchingCount = matchingCount ?: 3)
    GameSettings.SettingsScoring.SUM_MATCHING_FOUR ->
        matchingScore(dice = dice, matchingCount = matchingCount ?: 4)
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> fixedValue ?: 0
    null -> 0
}

private fun matchingScore(dice: List<Int>, matchingCount: Int): Int =
    dice.groupingBy { it }
        .eachCount()
        .filterValues { it >= matchingCount }
        .keys
        .maxOrNull()
        ?.let { it * matchingCount }
        ?: 0

private fun upperBonusDelta(
    diceScore: Int,
    rule: DiceScoreRule,
    columnIndex: Int,
    context: DiceScoreContext,
): Int {
    if (rule.kind != DiceScoreRuleKind.UPPER) return 0
    val settings = context.settings
    if (!settings.isUpperBonusEnabled) return 0
    val subtotalBefore = upperScoreKeys.sumOf { key ->
        context.scoreEntries[key]?.getOrNull(columnIndex) ?: 0
    }
    return upperBonus(subtotalBefore + diceScore, settings) - upperBonus(subtotalBefore, settings)
}

private fun awardsExtraFiveOfAKindBonus(
    dice: List<Int>,
    rule: DiceScoreRule,
    columnIndex: Int,
    context: DiceScoreContext,
): Boolean {
    val settings = context.settings
    if (!settings.isExtraFiveOfAKindEnabled || settings.extraFiveOfAKindValue == null) return false
    if (!isFiveOfAKind(dice)) return false
    if (rule.key == ScoreKey.FIVE_OF_A_KIND || rule.key == ScoreKey.EXTRA_FIVE_OF_A_KIND) return false
    return (context.scoreEntries[ScoreKey.FIVE_OF_A_KIND]?.getOrNull(columnIndex) ?: 0) > 0
}

private val upperScoreKeys: List<ScoreKey> = listOf(
    ScoreKey.ONES,
    ScoreKey.TWOS,
    ScoreKey.THREES,
    ScoreKey.FOURS,
    ScoreKey.FIVES,
    ScoreKey.SIXES,
)

private fun isFiveOfAKind(dice: List<Int>): Boolean =
    dice.distinct().size == 1

private fun isFullHouse(dice: List<Int>): Boolean =
    dice.groupingBy { it }.eachCount().values.sorted() == listOf(2, 3)

private fun isSmallStraight(dice: List<Int>): Boolean {
    val values = dice.toSet()
    return setOf(1, 2, 3, 4).all(values::contains) ||
        setOf(2, 3, 4, 5).all(values::contains) ||
        setOf(3, 4, 5, 6).all(values::contains)
}

private fun isLargeStraight(dice: List<Int>): Boolean {
    val values = dice.toSet()
    return values == setOf(1, 2, 3, 4, 5) || values == setOf(2, 3, 4, 5, 6)
}
