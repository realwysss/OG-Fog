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
        vec3 h = normalize(vec3(dir.x, 0.03, dir.z));
        vec4 c = Proj * vec4((Rot * vec4(h, 0.0)).xyz, 1.0);
        vec2 hu = c.w > 0.0 ? clamp(c.xy / c.w * 0.5 + 0.5, 0.001, 0.999) : texCoord;
        col = texture(SkySampler, mix(hu, texCoord, t)).rgb;
    }
    fragColor = vec4(col, a);
}
