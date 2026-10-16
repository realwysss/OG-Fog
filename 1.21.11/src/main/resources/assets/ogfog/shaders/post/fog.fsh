#version 330

uniform sampler2D SkySampler;
uniform sampler2D DepthSampler;

layout(std140) uniform FogInfo {
    mat4 InvProj;
    mat4 InvRot;
    mat4 Rot;
    mat4 Proj;
    vec4 A;
    vec4 B;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float d = texture(DepthSampler, texCoord).r;
    if (d >= 0.99999) {
        fragColor = vec4(0.0);
        return;
    }
    vec4 pv = InvProj * vec4(texCoord * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    pv /= pv.w;
    float a = clamp((length(pv.xyz) - A.x) / max(A.y - A.x, 0.001), 0.0, 1.0);
    if (A.w > 0.5) a = a * a * (3.0 - 2.0 * a);
    a *= A.z;
    if (a <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }
    vec3 col = B.yzw;
    if (B.x < 0.5) {
        vec3 dir = normalize((InvRot * vec4(pv.xyz, 0.0)).xyz);
        float t = clamp(dir.y / 0.03, 0.0, 1.0);
        vec2 hz = normalize(dir.xz + vec2(0.0001));
        vec3 acc = vec3(0.0);
        float ws = 0.0;
        for (int i = -4; i <= 4; i++) {
            float an = float(i) * 0.14;
            float cs = cos(an);
            float sn = sin(an);
            vec2 r = vec2(hz.x * cs - hz.y * sn, hz.x * sn + hz.y * cs);
            vec3 h = normalize(vec3(r.x, 0.03, r.y));
            vec4 c = Proj * vec4((Rot * vec4(h, 0.0)).xyz, 1.0);
            if (c.w <= 0.0) continue;
            vec2 n = c.xy / c.w;
            if (abs(n.x) > 0.98 || abs(n.y) > 0.98) continue;
            acc += texture(SkySampler, n * 0.5 + 0.5).rgb;
            ws += 1.0;
        }
        vec3 hor = mix(B.yzw, ws > 0.0 ? acc / ws : B.yzw, ws / 9.0);
        col = mix(hor, texture(SkySampler, texCoord).rgb, t);
    }
    fragColor = vec4(col, a);
}
