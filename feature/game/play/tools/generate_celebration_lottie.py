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
