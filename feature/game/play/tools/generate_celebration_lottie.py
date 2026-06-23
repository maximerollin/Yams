#!/usr/bin/env python3
"""Generate the celebration Lottie assets (dev-only).

Run from the repo root:  python3 feature/game/play/tools/generate_celebration_lottie.py
Tweak the CONSTANTS block and re-run to adjust the feel.

Timing is proportional to each scene's `op` (see `timing()`), so changing
`YAMS_OP` / `BIG_OP` rescales the whole animation (lower = faster/snappier).
"""
import json, math, os

OUT = "feature/game/play/src/commonMain/composeResources/files"
C = 270.0  # canvas center (540/2)

# --- CONSTANTS (tune & re-run) ---
YAMS_OP = 80          # total frames @60fps (~1.33s) — lower = faster
BIG_OP = 54           # big score (~0.9s)
YAMS_RADIUS = 206.0   # crown radius around the centered card
ORBIT = 150.0         # degrees the crown sweeps around the centre during the hold
DIE = 88
DIE_R = 17
PIP = 13
PIP_G = 22
SPARK_COUNT = 34

CREAM = [1, 0.980, 0.945, 1]
PIPC = [0.294, 0.188, 0.145, 1]
GOLD_GLOW = [0.965, 0.835, 0.470, 1]
GOLD_OUT = [0.918, 0.741, 0.318, 1]
GOLD_IN = [0.992, 0.902, 0.604, 1]
INFO_GLOW = [0.553, 0.788, 0.925, 1]
INFO_OUT = [0.255, 0.580, 0.878, 1]
INFO_IN = [0.553, 0.831, 0.722, 1]
GOLD_SPARK = [0.898, 0.725, 0.298, 1]
TEAL_SPARK = [0.498, 0.827, 0.690, 1]
CREAM_SPARK = [1, 0.945, 0.776, 1]
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


def timing(op):
    entry = max(8, round(op * 0.26))   # frame where dice have landed
    return {
        "entry": entry,
        "stagger": 2,
        "halo": max(4, round(op * 0.17)),
        "flash_in": entry - 3,
        "fade_start": op - 11,
        "fade_end": op - 3,
        "spark_in": entry + 1,
        "spark_span": round(op * 0.36),
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


def layer(ind, nm, shapes, p, r, s, o, ip, op):
    return {
        "ddd": 0, "ind": ind, "ty": 4, "nm": nm, "sr": 1,
        "ks": {"o": o, "r": r, "p": p, "a": static([0, 0, 0]), "s": s},
        "ao": 0, "shapes": shapes, "ip": ip, "op": op, "st": 0, "bm": 0,
    }


def die_transform(target, t0, tm, spin=0.0, drift=None):
    tx, ty = target
    sx = C + (tx - C) * 2.6
    sy = C + (ty - C) * 2.6
    e = tm["entry"]
    p_frames = [(t0, [sx, sy, 0], EASE_OUT), (e, [tx, ty, 0], EASE_OUT)]
    if drift is not None:
        p_frames.append((tm["fade_start"], [drift[0], drift[1], 0]))
    p = kf(p_frames)
    r = kf([(t0, [spin], EASE_OUT), (e, [0.0])]) if spin else static(0)
    s = kf([
        (t0, [46, 46, 100], EASE_OUT),
        (e - 3, [113, 113, 100], EASE_IO),
        (e + 3, [100, 100, 100]),
    ])
    o = kf([
        (t0, [0], EASE_OUT), (t0 + 6, [100], EASE_IO),
        (tm["fade_start"], [100], EASE_IN), (tm["fade_end"], [0]),
    ])
    return p, r, s, o


def die_layers(start_ind, base_name, face, target, t0, tm, op, spin=0.0, drift=None):
    p, r, s, o = die_transform(target, t0, tm, spin, drift)
    pips = [el((dx, dy), (PIP, PIP)) for (dx, dy) in PIP_OFFSETS[face]] + [fill(PIPC)]
    body = [rc((0, 0), (DIE, DIE), DIE_R), fill(CREAM)]
    return [
        layer(start_ind, base_name + "_pips", pips, p, r, s, o, t0, op),
        layer(start_ind + 1, base_name, body, p, r, s, o, t0, op),
    ]


def ring_layer(ind, nm, size, color, ip0, peak_op, tm, op):
    fs, fe = tm["fade_start"], tm["fade_end"]
    o = kf([(ip0, [0], EASE_OUT), (ip0 + 8, [peak_op], EASE_IN),
            (fs, [max(peak_op - 8, 0)], EASE_IN), (fe, [0])])
    s = kf([(ip0, [0, 0, 100], EASE_OUT), (ip0 + 10, [109, 109, 100], EASE_IO),
            (ip0 + 18, [100, 100, 100])])
    return layer(ind, nm, [el((0, 0), (size, size)), fill(color)],
                 static([C, C, 0]), static(0), s, o, ip0, op)


def halo_layers(start_ind, glow_c, outer_c, inner_c, tm, op):
    h = tm["halo"]
    # array order = top-first: inner on top, glow at the back
    return [
        ring_layer(start_ind, "halo_inner", 200, inner_c, h, 90, tm, op),
        ring_layer(start_ind + 1, "halo_outer", 318, outer_c, h, 74, tm, op),
        ring_layer(start_ind + 2, "halo_glow", 392, glow_c, h + 3, 38, tm, op),
    ]


def flash_layer(ind, tm, op):
    fi = tm["flash_in"]
    o = kf([(fi, [0], EASE_OUT), (fi + 4, [96], EASE_IN), (fi + 12, [0])])
    s = kf([(fi, [22, 22, 100], EASE_OUT), (fi + 12, [195, 195, 100])])
    return layer(ind, "flash", [el((0, 0), (150, 150)), fill(WHITE)],
                 static([C, C, 0]), static(0), s, o, fi, op)


def spark_layers(start_ind, count, tm, op):
    f0, span = tm["spark_in"], tm["spark_span"]
    cols = [GOLD_SPARK, TEAL_SPARK, CREAM_SPARK]
    out = []
    for j in range(count):
        ang = math.radians(j * (360.0 / count) + (9 if j % 2 else 0))
        ring = j % 3
        dist = 182 + ring * 30
        sz = 20 if j % 4 == 0 else (16 if j % 2 else 12)
        ex, ey = C + dist * math.cos(ang), C + dist * math.sin(ang)
        col = cols[j % 3]
        p = kf([(f0, [C, C, 0], EASE_OUT), (f0 + span, [ex, ey, 0])])
        o = kf([(f0, [0], EASE_OUT), (f0 + 4, [100], EASE_IN), (f0 + span, [0])])
        s = kf([(f0, [35, 35, 100], EASE_OUT), (f0 + span, [100, 100, 100])])
        out.append(layer(start_ind + j, "spark_%d" % (j + 1),
                          [el((0, 0), (sz, sz)), fill(col)], p, static(0), s, o, f0, min(f0 + span + 2, op)))
    return out


def comp(name, op, layers):
    return {"v": "5.12.2", "fr": 60, "ip": 0, "op": op, "w": 540, "h": 540,
            "nm": name, "ddd": 0, "assets": [], "layers": layers}


def build_yams(face):
    tm = timing(YAMS_OP)
    spins = [-38.0, 30.0, -26.0, 34.0, -30.0]
    layers = []
    ind = 1
    for k in range(5):
        a = -90 + k * 72
        land = (C + YAMS_RADIUS * math.cos(math.radians(a)),
                C + YAMS_RADIUS * math.sin(math.radians(a)))
        drift = (C + YAMS_RADIUS * math.cos(math.radians(a + ORBIT)),
                 C + YAMS_RADIUS * math.sin(math.radians(a + ORBIT)))
        layers += die_layers(ind, "die%d" % (k + 1), face, land, k * tm["stagger"], tm,
                             YAMS_OP, spin=spins[k], drift=drift)
        ind += 2
    layers.append(flash_layer(ind, tm, YAMS_OP)); ind += 1
    layers += halo_layers(ind, GOLD_GLOW, GOLD_OUT, GOLD_IN, tm, YAMS_OP); ind += 3
    layers += spark_layers(ind, SPARK_COUNT, tm, YAMS_OP)
    return comp("Yams %d" % face, YAMS_OP, layers)


def build_big_score():
    tm = timing(BIG_OP)
    targets = [(C - 168, C - 6), (C + 168, C - 6)]
    faces = [6, 6]
    spins = [-22.0, 22.0]
    layers = []
    ind = 1
    for k, tgt in enumerate(targets):
        layers += die_layers(ind, "die%d" % (k + 1), faces[k], tgt, k * tm["stagger"], tm,
                             BIG_OP, spin=spins[k])
        ind += 2
    layers.append(flash_layer(ind, tm, BIG_OP)); ind += 1
    layers += halo_layers(ind, INFO_GLOW, INFO_OUT, INFO_IN, tm, BIG_OP)
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
