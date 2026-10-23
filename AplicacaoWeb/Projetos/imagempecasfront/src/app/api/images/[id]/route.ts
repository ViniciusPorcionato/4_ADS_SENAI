import { apiError, backendFetch } from "@/lib/backend";

export async function GET(
  _request: Request,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  if (!/^[a-zA-Z0-9-]+$/.test(id)) return apiError("Invalid image ID.", 400);
  try {
    const response = await backendFetch(`/${encodeURIComponent(id)}`);
    if (!response.ok)
      return apiError("Image not found.", response.status === 404 ? 404 : 502);
    const type = response.headers.get("content-type")?.split(";")[0] || "";
    if (!["image/png", "image/jpeg", "image/gif"].includes(type))
      return apiError("Unsupported image format.");
    return new Response(response.body, {
      headers: {
        "Content-Type": type,
        "X-Content-Type-Options": "nosniff",
        "Cache-Control": "private, max-age=300",
      },
    });
  } catch {
    return apiError("Image service unavailable.", 503);
  }
}
