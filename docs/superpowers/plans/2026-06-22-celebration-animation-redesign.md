# Redesign de l'animation de célébration — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refaire les assets Lottie de célébration (Yams en couronne + build-up/flash/halo, gros score allégé) et brancher une face de dé dynamique, sans changer l'architecture.

**Architecture:** Un script Python génère 6 variantes `celebration_yams_{1..6}.json` + `celebration_big_score.json` (formes vectorielles, compatibles compottie-lite). Le modèle gagne `dieFace: Int?`, dérivé de la ligne de score au déclenchement ; l'overlay choisit l'asset selon `(type, dieFace)`. La carte/texte restent en Compose.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform 1.10.1, compottie-lite 2.0.2, Python 3 (génération d'assets, dev-only).

## Global Constraints

- Canvas Lottie : 540×540, 60 fps. Yams = 108 frames (`op` 108), gros score = 72 frames (`op` 72). (Les tests d'asset vérifient `endFrame` = 108 / 72.)
- Assets purement vectoriels : formes `el`/`rc`/`fl` uniquement, `bm` = 0, pas de gradients, masques, track mattes, images bitmap, ni chargement réseau (contraintes compottie-lite).
- Couleurs Lottie en RGBA normalisé 0–1.
- Face Yams par défaut = 5 (`DefaultYamsDieFace`), bornée 1..6.
- Ne pas changer : déclenchement (Yams ou score ≥ 30), timings (`timeoutMillis` YAMS 2200 ms / BIG_SCORE 1600 ms, `copyRevealProgress`), fallback « animations réduites », arrêt piloté par `isAtEnd`.
- Le correctif du painter figé est déjà en place (`progress = { previewProgress ?: animationState.progress }`) — ne pas régresser.
- Répertoire assets : `feature/game/play/src/commonMain/composeResources/files/`.

---

## File Structure

- Create: `feature/game/play/tools/generate_celebration_lottie.py` — générateur dev-only des 7 JSON.
- Create: `feature/game/play/src/commonMain/composeResources/files/celebration_yams_1.json` … `_6.json` (générés).
- Modify (overwrite, généré): `feature/game/play/src/commonMain/composeResources/files/celebration_big_score.json`.
- Delete: `feature/game/play/src/commonMain/composeResources/files/celebration_yams.json` (remplacé par les 6 variantes).
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/model/GamePlayCelebration.kt` — champ `dieFace`, `DefaultYamsDieFace` n'est PAS ici (voir overlay), fonction pure `fiveOfAKindDieFace`.
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/components/GamePlayCelebrationOverlay.kt` — `assetPathFor(type, dieFace)`, `DefaultYamsDieFace`, clé de composition incluant `dieFace`.
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/GamePlayScreen.kt` — calcul de `dieFace` dans `submitScore`.
- Modify (tests): `feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/GamePlayCelebrationAssetTest.kt`.
- Create (tests): `feature/game/play/src/commonTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/FiveOfAKindDieFaceTest.kt`.

---

## Task 1: Générateur Lottie + assets

**Files:**
- Create: `feature/game/play/tools/generate_celebration_lottie.py`
- Create/Modify: les 7 JSON dans `feature/game/play/src/commonMain/composeResources/files/`
- Delete: `feature/game/play/src/commonMain/composeResources/files/celebration_yams.json`

**Interfaces:**
- Produces: les fichiers `celebration_yams_{1..6}.json` (op 108) et `celebration_big_score.json` (op 72), consommés par l'overlay (Task 4) et les tests (Task 2).

- [ ] **Step 1: Écrire le générateur**

Create `feature/game/play/tools/generate_celebration_lottie.py`:

```python
#!/usr/bin/env python3
"""Generate the celebration Lottie assets (dev-only).

Run from the repo root:  python3 feature/game/play/tools/generate_celebration_lottie.py
Tweak the CONSTANTS block and re-run to adjust the feel.
"""
import json, math, os

OUT = "feature/game/play/src/commonMain/composeResources/files"
C = 270.0  # canvas center (540/2)

# --- CONSTANTS (tune & re-run) ---
YAMS_OP = 108
BIG_OP = 72
YAMS_RADIUS = 188.0
DIE = 88
DIE_R = 17
PIP = 13
PIP_G = 22
ENTRY_END = 33
STAGGER = 3
FADE_START = 96
FADE_END = 106
HALO_START = 30
SPARK_COUNT = 20

CREAM = [1, 0.980, 0.945, 1]
PIPC = [0.294, 0.188, 0.145, 1]
GOLD_OUT = [0.906, 0.780, 0.400, 1]
GOLD_IN = [0.953, 0.871, 0.612, 1]
INFO_OUT = [0.215, 0.541, 0.866, 1]
INFO_IN = [0.498, 0.827, 0.690, 1]
GOLD_SPARK = [0.898, 0.725, 0.298, 1]
TEAL_SPARK = [0.498, 0.827, 0.690, 1]
WHITE = [1, 0.992, 0.961, 1]

EASE_OUT = (0.0, 0.0, 0.3, 1.0)
EASE_IN = (0.7, 0.0, 1.0, 1.0)
EASE_IO = (0.42, 0.0, 0.58, 1.0)

PIP_OFFSETS = {
    1: [(0, 0)],
    2: [(-PIP_G, -PIP_G), (PIP_G, PIP_G)],
    3: [(-PIP_G, -PIP_G), (0, 0), (PIP_G, PIP_G)],
    4: [(-PIP_G, -PIP_G), (PIP_G, -PIP_G), (-PIP_G, PIP_G), (PIP_G, PIP_G)],
    5: [(-PIP_G, -PIP_G), (PIP_G, -PIP_G), (0, 0), (-PIP_G, PIP_G), (PIP_G, PIP_G)],
    6: [(-PIP_G, -PIP_G), (PIP_G, -PIP_G), (-PIP_G, 0), (PIP_G, 0), (-PIP_G, PIP_G), (PIP_G, PIP_G)],
}


def kf(frames):
    """frames: list of (t, value_list, ease_tuple?). Last frame: no ease."""
    out = []
    for i, fr in enumerate(frames):
        t, s = fr[0], fr[1]
        node = {"t": t, "s": list(s)}
        if i < len(frames) - 1:
            e = fr[2] if len(fr) > 2 else EASE_IO
            nxt = frames[i + 1][1]
            node["e"] = list(nxt)
            node["o"] = {"x": [e[0]], "y": [e[1]]}
            node["i"] = {"x": [e[2]], "y": [e[3]]}
        out.append(node)
    return {"a": 1, "k": out}


def static(v):
    return {"a": 0, "k": v}


def fill(color, opacity=100):
    return {"ty": "fl", "c": static(color), "o": static(opacity), "nm": "fl"}


def el(pos, size):
    return {"ty": "el", "p": static(list(pos)), "s": static(list(size)), "nm": "el"}


def rc(pos, size, r):
    return {"ty": "rc", "p": static(list(pos)), "s": static(list(size)), "r": static(r), "nm": "rc"}


def layer(ind, nm, shapes, p, s, o, ip, op):
    return {
        "ddd": 0, "ind": ind, "ty": 4, "nm": nm, "sr": 1,
        "ks": {"o": o, "r": static(0), "p": p, "a": static([0, 0, 0]), "s": s},
        "ao": 0, "shapes": shapes, "ip": ip, "op": op, "st": 0, "bm": 0,
    }


def die_transform(target, t0):
    tx, ty = target
    sx = C + (tx - C) * 2.6
    sy = C + (ty - C) * 2.6
    p = kf([(t0, [sx, sy, 0], EASE_OUT), (ENTRY_END, [tx, ty, 0])])
    s = kf([
        (t0, [50, 50, 100], EASE_OUT),
        (ENTRY_END - 3, [112, 112, 100], EASE_IO),
        (ENTRY_END + 3, [100, 100, 100]),
    ])
    o = kf([
        (t0, [0], EASE_OUT), (t0 + 8, [100], EASE_IO),
        (FADE_START, [100], EASE_IN), (FADE_END, [0]),
    ])
    return p, s, o


def die_layers(start_ind, base_name, face, target, t0, op):
    p, s, o = die_transform(target, t0)
    pips = [el((dx, dy), (PIP, PIP)) for (dx, dy) in PIP_OFFSETS[face]] + [fill(PIPC)]
    body = [rc((0, 0), (DIE, DIE), DIE_R), fill(CREAM)]
    return [
        layer(start_ind, base_name + "_pips", pips, p, s, o, t0, op),
        layer(start_ind + 1, base_name, body, p, s, o, t0, op),
    ]


def halo_layers(start_ind, outer_c, inner_c, op):
    o1 = kf([(HALO_START, [0], EASE_OUT), (HALO_START + 10, [60], EASE_IN),
             (FADE_START - 4, [55], EASE_IN), (FADE_END, [0])])
    s1 = kf([(HALO_START, [0, 0, 100], EASE_OUT), (HALO_START + 12, [106, 106, 100], EASE_IO),
             (HALO_START + 20, [100, 100, 100])])
    o2 = kf([(HALO_START, [0], EASE_OUT), (HALO_START + 10, [70], EASE_IN),
             (FADE_START - 4, [60], EASE_IN), (FADE_END, [0])])
    s2 = kf([(HALO_START, [0, 0, 100], EASE_OUT), (HALO_START + 12, [104, 104, 100], EASE_IO),
             (HALO_START + 20, [100, 100, 100])])
    return [
        layer(start_ind, "halo_outer", [el((0, 0), (300, 300)), fill(outer_c)],
              static([C, C, 0]), s1, o1, HALO_START, op),
        layer(start_ind + 1, "halo_inner", [el((0, 0), (200, 200)), fill(inner_c)],
              static([C, C, 0]), s2, o2, HALO_START, op),
    ]


def flash_layer(ind, op):
    o = kf([(ENTRY_END - 3, [0], EASE_OUT), (ENTRY_END + 1, [90], EASE_IN), (ENTRY_END + 9, [0])])
    s = kf([(ENTRY_END - 3, [20, 20, 100], EASE_OUT), (ENTRY_END + 9, [170, 170, 100])])
    return layer(ind, "flash", [el((0, 0), (150, 150)), fill(WHITE)],
                 static([C, C, 0]), s, o, ENTRY_END - 3, op)


def spark_layers(start_ind, count, op):
    out = []
    for j in range(count):
        ang = math.radians(j * (360.0 / count))
        dist = 160 + (34 if j % 2 else 0)
        ex, ey = C + dist * math.cos(ang), C + dist * math.sin(ang)
        f0 = ENTRY_END + 1
        col = GOLD_SPARK if j % 3 else TEAL_SPARK
        p = kf([(f0, [C, C, 0], EASE_OUT), (f0 + 26, [ex, ey, 0])])
        o = kf([(f0, [0], EASE_OUT), (f0 + 5, [100], EASE_IN), (f0 + 26, [0])])
        s = kf([(f0, [40, 40, 100], EASE_OUT), (f0 + 26, [100, 100, 100])])
        out.append(layer(start_ind + j, "spark_%d" % (j + 1),
                          [el((0, 0), (11, 11)), fill(col)], p, s, o, f0, min(f0 + 28, op)))
    return out


def comp(name, op, layers):
    return {"v": "5.12.2", "fr": 60, "ip": 0, "op": op, "w": 540, "h": 540,
            "nm": name, "ddd": 0, "assets": [], "layers": layers}


def build_yams(face):
    crown = []
    for k in range(5):
        a = math.radians(-90 + k * 72)
        crown.append((C + YAMS_RADIUS * math.cos(a), C + YAMS_RADIUS * math.sin(a)))
    layers = []
    ind = 1
    for k, tgt in enumerate(crown):
        layers += die_layers(ind, "die%d" % (k + 1), face, tgt, k * STAGGER, YAMS_OP)
        ind += 2
    layers += halo_layers(ind, GOLD_OUT, GOLD_IN, YAMS_OP); ind += 2
    layers.append(flash_layer(ind, YAMS_OP)); ind += 1
    layers += spark_layers(ind, SPARK_COUNT, YAMS_OP)
    return comp("Yams %d" % face, YAMS_OP, layers)


def build_big_score():
    targets = [(C - 160, C - 8), (C + 160, C - 8)]
    faces = [6, 6]
    layers = []
    ind = 1
    for k, tgt in enumerate(targets):
        layers += die_layers(ind, "die%d" % (k + 1), faces[k], tgt, k * STAGGER, BIG_OP)
        ind += 2
    layers += halo_layers(ind, INFO_OUT, INFO_IN, BIG_OP); ind += 2
    layers.append(flash_layer(ind, BIG_OP))
    return comp("Big score", BIG_OP, layers)


def main():
    os.makedirs(OUT, exist_ok=True)
    for face in range(1, 7):
        path = os.path.join(OUT, "celebration_yams_%d.json" % face)
        with open(path, "w") as f:
            json.dump(build_yams(face), f, separators=(",", ":"))
        print("wrote", path)
    big = os.path.join(OUT, "celebration_big_score.json")
    with open(big, "w") as f:
        json.dump(build_big_score(), f, separators=(",", ":"))
    print("wrote", big)


if __name__ == "__main__":
    main()
```

- [ ] **Step 2: Générer les assets et supprimer l'ancien**

Run:
```bash
cd /Users/maximerollin/Documents/Perso/Yams
python3 feature/game/play/tools/generate_celebration_lottie.py
git rm feature/game/play/src/commonMain/composeResources/files/celebration_yams.json
```
Expected: 7 lignes `wrote …` ; l'ancien `celebration_yams.json` supprimé.

- [ ] **Step 3: Vérifier rapidement la validité JSON et `op`**

Run:
```bash
python3 - <<'PY'
import json, glob
for p in sorted(glob.glob("feature/game/play/src/commonMain/composeResources/files/celebration_*.json")):
    d = json.load(open(p))
    print(p.split("/")[-1], "op=", d["op"], "layers=", len(d["layers"]))
PY
```
Expected: `celebration_yams_1.json … _6.json` avec `op= 108`, `celebration_big_score.json` avec `op= 72`.

- [ ] **Step 4: Commit**

```bash
git add feature/game/play/tools/generate_celebration_lottie.py \
        feature/game/play/src/commonMain/composeResources/files/celebration_yams_*.json \
        feature/game/play/src/commonMain/composeResources/files/celebration_big_score.json
git commit -m "feat(play): generate redesigned celebration Lottie assets"
```

---

## Task 2: Test de parsing des assets

**Files:**
- Modify: `feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/GamePlayCelebrationAssetTest.kt`

**Interfaces:**
- Consumes: les 7 JSON de Task 1, `io.github.alexzhirkevich.compottie.LottieComposition.parse`.

- [ ] **Step 1: Réécrire le test pour les 7 assets**

Replace the whole file content with:

```kotlin
package io.github.maximerollin.yams.feature.game.play.model

import io.github.alexzhirkevich.compottie.LottieComposition
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePlayCelebrationAssetTest {
    private fun parse(name: String): LottieComposition =
        LottieComposition.parse(
            File("src/commonMain/composeResources/files/$name").readText(),
        )

    @Test
    fun allYamsFaceVariantsParseWith108Frames() {
        for (face in 1..6) {
            val composition = parse("celebration_yams_$face.json")
            assertEquals(108f, composition.endFrame, "face $face endFrame")
        }
    }

    @Test
    fun bigScoreAssetParsesWith72Frames() {
        val composition = parse("celebration_big_score.json")
        assertEquals(72f, composition.endFrame)
    }
}
```

- [ ] **Step 2: Lancer le test (doit passer)**

Run: `./gradlew :feature:game:play:jvmTest --tests "io.github.maximerollin.yams.feature.game.play.model.GamePlayCelebrationAssetTest"`
Expected: PASS (2 tests).

- [ ] **Step 3: Commit**

```bash
git add feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/GamePlayCelebrationAssetTest.kt
git commit -m "test(play): parse all celebration asset variants"
```

---

## Task 3: Modèle — `dieFace` + dérivation de face

**Files:**
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/model/GamePlayCelebration.kt`
- Create: `feature/game/play/src/commonTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/FiveOfAKindDieFaceTest.kt`

**Interfaces:**
- Produces: `GamePlayCelebration.dieFace: Int?` (défaut `null`) ; `fun fiveOfAKindDieFace(scoreKey: ScoreKey, score: Int): Int?` (internal, pure). Consommés par Task 4 (screen) et Task 5 (overlay).

- [ ] **Step 1: Écrire le test de dérivation (échoue)**

Create `feature/game/play/src/commonTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/FiveOfAKindDieFaceTest.kt`:

```kotlin
package io.github.maximerollin.yams.feature.game.play.model

import io.github.maximerollin.yams.core.model.ScoreKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FiveOfAKindDieFaceTest {
    @Test
    fun upperRowFiveOfAKindReturnsItsFace() {
        assertEquals(5, fiveOfAKindDieFace(ScoreKey.FIVES, score = 25))
        assertEquals(6, fiveOfAKindDieFace(ScoreKey.SIXES, score = 30))
        assertEquals(1, fiveOfAKindDieFace(ScoreKey.ONES, score = 5))
    }

    @Test
    fun upperRowWithNonMatchingScoreReturnsNull() {
        assertNull(fiveOfAKindDieFace(ScoreKey.FIVES, score = 20))
    }

    @Test
    fun dedicatedYamsCellReturnsNull() {
        assertNull(fiveOfAKindDieFace(ScoreKey.FIVE_OF_A_KIND, score = 50))
    }
}
```

- [ ] **Step 2: Lancer (doit échouer)**

Run: `./gradlew :feature:game:play:jvmTest --tests "io.github.maximerollin.yams.feature.game.play.model.FiveOfAKindDieFaceTest"`
Expected: FAIL compilation — `fiveOfAKindDieFace` non défini.

- [ ] **Step 3: Ajouter le champ et la fonction**

In `GamePlayCelebration.kt`, add the import at the top (after the package line):

```kotlin
import io.github.maximerollin.yams.core.model.ScoreKey
```

Add `dieFace` to the data class (with default for previews/tests):

```kotlin
internal data class GamePlayCelebration(
    val id: Int,
    val type: GamePlayCelebrationType,
    val playerName: String,
    val score: Int,
    val dieFace: Int? = null,
)
```

Add the pure function at the end of the file:

```kotlin
internal fun fiveOfAKindDieFace(scoreKey: ScoreKey, score: Int): Int? {
    val value = when (scoreKey) {
        ScoreKey.ONES -> 1
        ScoreKey.TWOS -> 2
        ScoreKey.THREES -> 3
        ScoreKey.FOURS -> 4
        ScoreKey.FIVES -> 5
        ScoreKey.SIXES -> 6
        else -> return null
    }
    return value.takeIf { score == value * 5 }
}
```

- [ ] **Step 4: Lancer (doit passer)**

Run: `./gradlew :feature:game:play:jvmTest --tests "io.github.maximerollin.yams.feature.game.play.model.FiveOfAKindDieFaceTest"`
Expected: PASS (3 tests).

- [ ] **Step 5: Commit**

```bash
git add feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/model/GamePlayCelebration.kt \
        feature/game/play/src/commonTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/FiveOfAKindDieFaceTest.kt
git commit -m "feat(play): derive celebration die face from score row"
```

---

## Task 4: Overlay — sélection d'asset par `(type, dieFace)`

**Files:**
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/components/GamePlayCelebrationOverlay.kt`

**Interfaces:**
- Consumes: `GamePlayCelebration.dieFace` (Task 3), `GamePlayCelebrationType`.
- Produces: `private const val DefaultYamsDieFace = 5`, `private fun assetPathFor(type, dieFace): String`.

- [ ] **Step 1: Remplacer l'extension `assetPath` par `assetPathFor`**

In `GamePlayCelebrationOverlay.kt`, remove this existing block:

```kotlin
private val GamePlayCelebrationType.assetPath: String
    get() = when (this) {
        GamePlayCelebrationType.YAMS -> "files/celebration_yams.json"
        GamePlayCelebrationType.BIG_SCORE -> "files/celebration_big_score.json"
    }
```

and replace it with:

```kotlin
private const val DefaultYamsDieFace = 5

private fun assetPathFor(
    type: GamePlayCelebrationType,
    dieFace: Int?,
): String = when (type) {
    GamePlayCelebrationType.YAMS ->
        "files/celebration_yams_${(dieFace ?: DefaultYamsDieFace).coerceIn(1, 6)}.json"
    GamePlayCelebrationType.BIG_SCORE -> "files/celebration_big_score.json"
}
```

- [ ] **Step 2: Utiliser `assetPathFor` et inclure `dieFace` dans la clé de composition**

Change this line:

```kotlin
    val assetPath = celebration.type.assetPath
```

to:

```kotlin
    val assetPath = assetPathFor(celebration.type, celebration.dieFace)
```

And change the composition key so a different face reloads the asset:

```kotlin
        rememberLottieComposition(celebration.type) {
```

to:

```kotlin
        rememberLottieComposition(celebration.type, celebration.dieFace) {
```

- [ ] **Step 3: Compiler le module (JVM)**

Run: `./gradlew :feature:game:play:compileKotlinJvm`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/components/GamePlayCelebrationOverlay.kt
git commit -m "feat(play): pick celebration asset by type and die face"
```

---

## Task 5: Écran — calcul de `dieFace` au déclenchement

**Files:**
- Modify: `feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/GamePlayScreen.kt` (fonction `submitScore`, autour des lignes 253-269)

**Interfaces:**
- Consumes: `fiveOfAKindDieFace` (Task 3), `GamePlayCelebration(dieFace=…)` (Task 3).

- [ ] **Step 1: Ajouter les imports manquants**

In `GamePlayScreen.kt`, add these imports alongside the existing `io.github.maximerollin.yams.feature.game.play.model.*` imports (around line 62-68):

```kotlin
import io.github.maximerollin.yams.feature.game.play.model.GamePlayCelebrationType
import io.github.maximerollin.yams.feature.game.play.model.fiveOfAKindDieFace
```

- [ ] **Step 2: Restructurer la construction de la célébration pour garder `row` en scope**

In `submitScore`, replace this block:

```kotlin
        allRows
            .firstOrNull { row -> row.key == cell.key }
            ?.let { row ->
                celebrationTypeFor(
                    score = option.score,
                    isYams = row.isYamsCelebration(option = option, settings = settings),
                )
            }
            ?.let { type ->
                celebrationId += 1
                celebration = GamePlayCelebration(
                    id = celebrationId,
                    type = type,
                    playerName = selectedPlayer.player.name,
                    score = option.score,
                )
            }
```

with:

```kotlin
        allRows
            .firstOrNull { row -> row.key == cell.key }
            ?.let { row ->
                val type = celebrationTypeFor(
                    score = option.score,
                    isYams = row.isYamsCelebration(option = option, settings = settings),
                ) ?: return@let
                celebrationId += 1
                celebration = GamePlayCelebration(
                    id = celebrationId,
                    type = type,
                    playerName = selectedPlayer.player.name,
                    score = option.score,
                    dieFace = if (type == GamePlayCelebrationType.YAMS) {
                        fiveOfAKindDieFace(row.key, option.score)
                    } else {
                        null
                    },
                )
            }
```

- [ ] **Step 3: Lancer les tests de déclenchement existants (doivent passer)**

Run: `./gradlew :feature:game:play:jvmTest --tests "io.github.maximerollin.yams.feature.game.play.model.GamePlayCelebrationTest"`
Expected: PASS (7 tests) — la logique de déclenchement est inchangée.

- [ ] **Step 4: Commit**

```bash
git add feature/game/play/src/commonMain/kotlin/io.github.maximerollin.yams.feature.game.play/GamePlayScreen.kt
git commit -m "feat(play): pass derived die face into celebration"
```

---

## Task 6: Vérification visuelle + Android

**Files:**
- Create (temporaire, supprimé en fin de tâche): `feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/CelebrationRenderCheck.kt`

**Interfaces:**
- Consumes: les assets (Task 1), l'overlay (Task 4).

- [ ] **Step 1: Harnais de rendu (dev-only) — rend 0.25/0.5/0.75 de chaque variante**

Create `feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/CelebrationRenderCheck.kt`:

```kotlin
package io.github.maximerollin.yams.feature.game.play.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import java.io.File
import kotlin.test.Test

class CelebrationRenderCheck {
    @Test
    fun renderFrames() {
        if (System.getenv("RENDER_CHECK") == null) return
        val out = File("/private/tmp/claude-501/celebration-render").apply { mkdirs() }
        val assets = (1..6).map { "celebration_yams_$it.json" } + "celebration_big_score.json"
        for (asset in assets) {
            val json = File("src/commonMain/composeResources/files/$asset").readText()
            for (p in listOf(0.25f, 0.5f, 0.75f)) {
                val scene = ImageComposeScene(540, 540, Density(1f)) {
                    Image(
                        painter = rememberLottiePainter(
                            composition = rememberLottieComposition {
                                LottieCompositionSpec.JsonString(json)
                            }.value,
                            progress = { p },
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                repeat(60) { scene.render(it * 16_666_666L) }
                val img = scene.render(60 * 16_666_666L)
                File(out, asset.removeSuffix(".json") + "_${(p * 100).toInt()}.png")
                    .writeBytes(img.encodeToData()!!.bytes)
                scene.close()
            }
        }
        println("rendered to ${out.absolutePath}")
    }
}
```

- [ ] **Step 2: Générer les PNG et les inspecter visuellement**

Run: `RENDER_CHECK=1 ./gradlew :feature:game:play:jvmTest --tests "io.github.maximerollin.yams.feature.game.play.model.CelebrationRenderCheck" --rerun-tasks`
Then open `/private/tmp/claude-501/celebration-render/`.
Expected: pour chaque variante Yams, les dés en couronne (face correspondante) dégagés du centre, halo doré, étincelles ; gros score = 2 dés + halo bleu/vert. Si l'esthétique ne convient pas, ajuster les CONSTANTS de `generate_celebration_lottie.py`, relancer Task 1 Step 2-3, puis ce step (boucle de tuning).

- [ ] **Step 3: Supprimer le harnais temporaire**

Run:
```bash
rm feature/game/play/src/jvmTest/kotlin/io/github/maximerollin/yams/feature/game/play/model/CelebrationRenderCheck.kt
```

- [ ] **Step 4: Compilation Android**

Run: `./gradlew :feature:game:play:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Suite de tests complète du module**

Run: `./gradlew :feature:game:play:jvmTest`
Expected: PASS (asset, face, déclenchement, painter non figé).

- [ ] **Step 6: Commit**

```bash
git commit -m "chore(play): verify redesigned celebration renders" --allow-empty
```

---

## Notes d'exécution

- Les previews `@YamsPhoneStoreScreenshotPreviews` de `GamePlayCelebrationOverlay.kt` construisent `GamePlayCelebration` sans `dieFace` → `null` → face par défaut 5 ; elles compilent sans changement grâce au défaut du champ.
- Le fallback « animations réduites » et le timing restent inchangés (aucune modification requise).
- Esthétique Lottie : toute retouche passe par les CONSTANTS du générateur, jamais par l'édition manuelle des JSON.
