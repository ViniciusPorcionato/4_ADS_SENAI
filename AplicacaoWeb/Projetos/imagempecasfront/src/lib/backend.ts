export const backendUrl = (
  process.env.IMAGES_API_URL || "http://localhost:8080"
).replace(/\/$/, "");

export function apiError(error: string, status = 502) {
  return Response.json({ error }, { status });
}

export async function backendFetch(path: string, init?: RequestInit) {
  return fetch(`${backendUrl}/v1/images${path}`, {
    ...init,
    cache: "no-store",
    signal: AbortSignal.timeout(45000),
  });
}
