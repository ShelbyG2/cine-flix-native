#!/usr/bin/env python3
"""Generate the Cineflix launcher icon.

Design: a smooth-edged (rounded-corner) white play button with a drop shadow,
floating on a solid black background that has a subtle radial gradient giving
it a 3D dome feel.

Produces:
  * path data for the adaptive-icon vector layers (printed with --path)
  * legacy .webp launcher icons for API 24/25 in every density bucket
  * a PNG preview for visual inspection

Usage:
  python3 tools/generate_launcher_icons.py --path   # print SVG path data
  python3 tools/generate_launcher_icons.py          # render webp icons + preview
"""

import argparse
import math
import os

from PIL import Image, ImageDraw

# ---------------------------------------------------------------------------
# Geometry -- shared between the vector drawable (108x108 viewport) and the
# raster renderer so legacy webp icons match the adaptive icon exactly.
# ---------------------------------------------------------------------------

VIEWPORT = 108.0

# Play triangle vertices, clockwise in screen coordinates (y grows down).
TRIANGLE = [(41.0, 36.0), (73.0, 54.0), (41.0, 72.0)]
CORNER_RADIUS = 6.0  # generous rounding -> "smooth edged" look

# Drop shadow offset behind the button (viewport units).
SHADOW_OFFSET = (0.0, 2.5)


def _sub(a, b):
    return (a[0] - b[0], a[1] - b[1])


def _add(a, b):
    return (a[0] + b[0], a[1] + b[1])


def _mul(a, s):
    return (a[0] * s, a[1] * s)


def _norm(a):
    m = math.hypot(a[0], a[1])
    return (a[0] / m, a[1] / m)


def rounded_polygon_tangents(verts, radius):
    """For each vertex return (tangent_toward_prev, tangent_toward_next, center, a0, a1)."""
    out = []
    n = len(verts)
    for i, v in enumerate(verts):
        p = verts[(i - 1) % n]
        q = verts[(i + 1) % n]
        u = _norm(_sub(p, v))  # unit toward previous vertex
        w = _norm(_sub(q, v))  # unit toward next vertex
        # half-angle between the two edges
        cos_t = max(-1.0, min(1.0, u[0] * w[0] + u[1] * w[1]))
        half = math.acos(cos_t) / 2.0
        d = radius / math.tan(half)  # distance vertex -> tangent point
        t_prev = _add(v, _mul(u, d))
        t_next = _add(v, _mul(w, d))
        c = _add(v, _mul(_norm(_add(u, w)), radius / math.sin(half)))  # arc center
        a0 = math.atan2(t_prev[1] - c[1], t_prev[0] - c[0])
        a1 = math.atan2(t_next[1] - c[1], t_next[0] - c[0])
        out.append((t_prev, t_next, c, a0, a1))
    return out


def svg_path_data(verts, radius):
    """SVG/Android pathData string for a clockwise rounded polygon."""
    tans = rounded_polygon_tangents(verts, radius)
    n = len(tans)
    parts = ["M%.3f,%.3f" % tans[0][1]]  # start at first vertex's tangent-toward-next
    for i in range(n):
        t_prev, t_next, _, _, _ = tans[(i + 1) % n]
        parts.append("L%.3f,%.3f" % t_prev)
        parts.append("A%.1f,%.1f 0 0 1 %.3f,%.3f" % (radius, radius, t_next[0], t_next[1]))
    parts.append("Z")
    return "".join(parts)


def rounded_polygon_outline(verts, radius, arc_steps=48):
    """Dense point list approximating the rounded polygon outline (for PIL fill)."""
    tans = rounded_polygon_tangents(verts, radius)
    pts = []
    for t_prev, t_next, c, a0, a1 in tans:
        pts.append(t_prev)
        delta = (a1 - a0) % (2.0 * math.pi)  # clockwise in y-down coords
        for s in range(1, arc_steps):
            a = a0 + delta * s / arc_steps
            pts.append((c[0] + radius * math.cos(a), c[1] + radius * math.sin(a)))
        pts.append(t_next)
    return pts


# ---------------------------------------------------------------------------
# Raster rendering (legacy webp icons)
# ---------------------------------------------------------------------------

SS = 4  # supersampling factor for antialiasing

BG_CENTER = (0x26, 0x26, 0x26)  # center of the black dome
BG_EDGE = (0x00, 0x00, 0x00)
BTN_TOP = (255, 255, 255)
BTN_BOTTOM = (0xD6, 0xD6, 0xD6)
SHADOW_ALPHA = 0x4D  # 30% black


def _radial_background(size):
    """Solid black background with a subtle radial highlight -> 3D dome.

    Mirrors the vector layer: linear radial gradient from BG_CENTER at the
    center to BG_EDGE at gradientRadius = 54 viewport units (= icon radius),
    clamped to black beyond.
    """
    import numpy as np
    yy, xx = np.mgrid[0:size, 0:size].astype(np.float32)
    c = (size - 1) / 2.0
    dist = np.hypot(xx - c, yy - c)
    t = np.clip(1.0 - dist / (size / 2.0), 0.0, 1.0)[:, :, None]
    arr = (np.asarray(BG_CENTER, dtype=np.float32)[None, None, :] * t).astype(np.uint8)
    return Image.fromarray(arr, "RGB")


def _vertical_gradient(size, top, bottom):
    img = Image.new("RGB", (1, size))
    img.putdata([(int(top[0] + (bottom[0] - top[0]) * y / max(1, size - 1)),
                  int(top[1] + (bottom[1] - top[1]) * y / max(1, size - 1)),
                  int(top[2] + (bottom[2] - top[2]) * y / max(1, size - 1))) for y in range(size)])
    return img.resize((size, size), Image.BILINEAR)


def render_icon(final_size, round_mask=False):
    size = final_size * SS
    scale = size / VIEWPORT

    def to_px(pts):
        return [(x * scale, y * scale) for x, y in pts]

    icon = _radial_background(size)

    # Drop shadow (slightly larger + offset down for depth)
    shadow_pts = to_px([(x + SHADOW_OFFSET[0], y + SHADOW_OFFSET[1]) for x, y in
                        rounded_polygon_outline(TRIANGLE, CORNER_RADIUS + 0.8)])
    shadow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ImageDraw.Draw(shadow).polygon(shadow_pts, fill=(10, 10, 10, SHADOW_ALPHA))
    icon = Image.alpha_composite(icon.convert("RGBA"), shadow)

    # Play button body with vertical gradient
    btn_pts = to_px(rounded_polygon_outline(TRIANGLE, CORNER_RADIUS))
    btn_mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(btn_mask).polygon(btn_pts, fill=255)
    body = _vertical_gradient(size, BTN_TOP, BTN_BOTTOM).convert("RGBA")
    body.putalpha(btn_mask)
    icon = Image.alpha_composite(icon, body)

    if round_mask:
        mask = Image.new("L", (size, size), 0)
        ImageDraw.Draw(mask).ellipse((0, 0, size - 1, size - 1), fill=255)
        out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        out.paste(icon, (0, 0), mask)
        icon = out

    return icon.resize((final_size, final_size), Image.LANCZOS)


DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--path", action="store_true", help="print SVG path data and exit")
    ap.add_argument("--res", default="app/src/main/res", help="android res directory")
    ap.add_argument("--preview", default="/tmp/ic_launcher_preview.png")
    args = ap.parse_args()

    if args.path:
        print("TRIANGLE:", svg_path_data(TRIANGLE, CORNER_RADIUS))
        sh = [(x + SHADOW_OFFSET[0], y + SHADOW_OFFSET[1]) for x, y in TRIANGLE]
        print("SHADOW:  ", svg_path_data(sh, CORNER_RADIUS + 0.8))
        return

    for name, size in DENSITIES.items():
        d = os.path.join(args.res, "mipmap-" + name)
        square = render_icon(size, round_mask=False)
        rnd = render_icon(size, round_mask=True)
        square.save(os.path.join(d, "ic_launcher.webp"), "WEBP", lossless=True, quality=100)
        rnd.save(os.path.join(d, "ic_launcher_round.webp"), "WEBP", lossless=True, quality=100)
        print("wrote", d)

    render_icon(192).save(args.preview)
    print("preview:", args.preview)


if __name__ == "__main__":
    main()
