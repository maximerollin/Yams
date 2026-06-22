# Redesign de l'animation de célébration (Yams / gros score)

Date : 2026-06-22
Module : `feature/game/play`

## Contexte

La célébration actuelle utilise compottie-lite avec deux assets Lottie
(`celebration_yams.json`, `celebration_big_score.json`). Un bug de painter figé
vient d'être corrigé (le progress doit être lu *dans* la lambda passée à
`rememberLottiePainter` — voir [GamePlayCelebrationOverlay.kt](../../../feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/components/GamePlayCelebrationOverlay.kt)).

Une fois l'animation visible, le rendu manque de panache : les cinq dés
convergent **tous vers le centre**, exactement là où la carte de score (Compose)
est posée, donc la carte les masque ; et la **sortie est un simple fondu** sans
temps fort. L'objectif est de refaire les assets pour un effet plus
spectaculaire, sans changer l'architecture (déclenchement, carte/texte Compose,
fallback, timing piloté par la fin réelle de l'animation).

## Objectifs

- Les dés ne sont plus masqués par la carte.
- Un vrai temps fort visuel (build-up → flash → halo qui s'épanouit).
- Yams = cinq dés **identiques** ; la face reflète la valeur réelle quand elle
  est connue, sinon une face par défaut.
- Sortie fluide en fondu, sans rotation en boucle ; l'écran de jeu reste
  légèrement visible derrière.

## Non-objectifs

- Pas de changement du déclenchement (Yams, ou score ≥ 30).
- Pas de confettis dans la célébration (réservés à l'écran de résultat).
- Pas de changement du fallback « animations réduites » (fondu + carte).
- Pas de dés 3D ni de rotations en boucle.
- Le gros score reste décoratif (pas de face dynamique).

## Direction artistique validée

- **Placement** : couronne — les dés se disposent en pentagone **autour** de la
  carte centrale, qui reste centrée et dégagée.
- **Temps fort** : build-up + flash + halo. Les dés déboulent vite depuis les
  bords, s'arrêtent net, puis un flash lumineux central et le halo doré
  s'épanouit (scale 0→1) avec une gerbe d'étincelles vers l'extérieur.
- **Faces** : cinq faces identiques pour le Yams.
- **Ajustement retenu** : davantage d'étincelles (nombre et portée) au moment de
  l'épanouissement. Timing, intensité du flash et taille des dés conservés tels
  que dans la maquette validée.

## Scène Yams (~1,8 s, canvas 540×540, 60 fps, 108 frames)

1. **Déboulé** (0 → ~0,55 s) : les 5 dés entrent depuis les bords vers leurs
   positions de couronne (pentagone, rayon ~210 px autour du centre), avec un
   léger overshoot à l'atterrissage (scale ~1,12 → 1,0). Décalages d'entrée
   échelonnés (~50 ms) entre les dés.
2. **Temps fort** (~0,55 → 0,8 s) : arrêt net ; flash radial blanc/or au centre
   (apparition/disparition rapide ~0,15 s) ; le halo doré s'épanouit
   (scale 0→1, fondu) ; les étincelles partent du centre vers l'extérieur.
3. **Tenue** (0,8 → 1,5 s) : halo posé, léger frémissement (bob/scale subtil)
   des dés ; les étincelles finissent leur course et s'estompent.
4. **Sortie** (1,5 → 1,8 s) : fondu propre de tous les éléments Lottie. Pas de
   rotation. La carte/texte Compose gère sa propre sortie via `AnimatedVisibility`.

Couronne (5 positions, rayon r autour du centre c) :
angles −90°, −18°, 54°, 126°, 198°. Le rayon est choisi pour que les dés restent
hors de l'emprise de la carte Compose centrée, y compris après mise à l'échelle
de l'`Image` sur mobile (voir « Intégration / layout »).

## Scène Gros score (~1,2 s, 540×540, 72 frames)

Version allégée : **2 dés** qui flanquent la carte (gauche/droite), même
build-up → flash mais plus court, **halo bleu/vert** (couleur `info`), **pas
d'étincelles**. Dés décoratifs, faces fixes (pas de dérivation dynamique).

## Faces dynamiques (Yams)

Six variantes générées : `celebration_yams_1.json` … `celebration_yams_6.json`,
identiques au pip-pattern près (les 5 dés affichent tous la face N).

Dérivation de la face au moment de la création de la célébration
(dans `submitScore`, [GamePlayScreen.kt](../../../feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/GamePlayScreen.kt)) :

- Cinq identiques **certain** dans une ligne du haut (`ONES`…`SIXES`,
  `fiveOfAKindDetection == CERTAIN`) → face = `upperRowDieValue(row.key)` (1–6).
- Sinon (case Yams dédiée `FIVE_OF_A_KIND`, bonus, ou cinq identiques non
  certain) → face par défaut.

Face par défaut : **5** (quinconce, look de dé classique). Ajustable si souhaité.

`GamePlayCelebration` gagne un champ `dieFace: Int?` (null = défaut). Le mapping
asset devient :

- `YAMS` → `files/celebration_yams_${face ?: DEFAULT_FACE}.json`
- `BIG_SCORE` → `files/celebration_big_score.json`

## Génération des assets

Un script générateur (Python, committé sous
`feature/game/play/tools/generate_celebration_lottie.py`) émet les 7 fichiers
dans `feature/game/play/src/commonMain/composeResources/files/`. Avantages :
keyframes calculés (positions de couronne, overshoot, halo, flash, étincelles),
régénération/tuning faciles, cohérence entre les 6 variantes de face. Les JSON
restent purement vectoriels (formes `el`/`rc`/`fl`), sans bitmap ni réseau,
compatibles compottie-lite (pas d'expressions, pas de gradients, pas de masques —
vérifié comme rendu correctement par le moteur).

## Intégration / layout

- `assetPath` (dans [GamePlayCelebrationOverlay.kt](../../../feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/components/GamePlayCelebrationOverlay.kt))
  devient fonction de `type` **et** `dieFace`.
- La carte Compose reste centrée ; la couronne la dégage par construction. On
  vérifiera le rayon vs largeur max de la carte après mise à l'échelle de
  l'`Image` (`fillMaxWidth().sizeIn(maxWidth=540.dp,…)`). Si nécessaire, réduire
  légèrement la largeur de la carte ou augmenter le rayon de couronne.
- Timing inchangé : `timeoutMillis` (YAMS 2200 ms, BIG_SCORE 1600 ms),
  `copyRevealProgress`. Arrêt piloté par la fin réelle (`isAtEnd`).
- Le composant reste dans son fichier dédié `GamePlayCelebrationOverlay.kt`.

## Tests & validation

- Tests unitaires existants des règles de déclenchement (conservés) + un test de
  la dérivation de face (`dieFace` selon `ScoreKey`/score) si la logique est
  extraite dans une fonction pure testable.
- Test asset : les 7 JSON parsent avec compottie et ont le bon `endFrame`
  (108 pour yams, 72 pour big score).
- Test de non-régression du painter (déjà ajouté :
  [GamePlayCelebrationLottieTest.kt](../../../feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/GamePlayCelebrationLottieTest.kt)).
- Previews `@YamsPhoneStoreScreenshotPreviews` : Yams (face par défaut + une face
  dérivée), gros score, fallback réduit.
- Compilation Android (`:feature:game:play:compileDebugKotlinAndroid`).
- Vérification visuelle d'au moins une frame animée rendue (rendu Skia réel).

## Risques

- Hand-tuning de l'esthétique Lottie : on itère via le script générateur.
- Rayon de couronne vs carte sur petits écrans : à valider visuellement.
- 6 variantes = +~6 fichiers JSON dans les ressources (taille acceptable,
  formes simples).
