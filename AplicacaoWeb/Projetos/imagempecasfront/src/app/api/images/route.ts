import { apiError, backendFetch } from "@/lib/backend";
import { MAX_FILE_SIZE, validateDetails, validateImage } from "@/lib/images";

export async function GET(request: Request) {
  const url = new URL(request.url);
  const query = url.searchParams.get("query") || "";
  const extension = url.searchParams.get("extension") || "";
  if (extension && !["PNG", "JPEG", "GIF"].includes(extension))
    return apiError("Invalid image extension.", 400);
  try {
    const response = await backendFetch(
      `?${new URLSearchParams({ query, extension })}`,
    );
    if (!response.ok)
      return apiError(
        "The image service could not complete the search. Please try again.",
        response.status >= 500 ? 502 : response.status,
      );
    const data = await response.json();
    if (!Array.isArray(data))
      return apiError("The image service returned an unexpected response.");
    return Response.json(
      data.map(
        (item: {
          url: string;
          name: string;
          extension: string;
          size: number;
          uploadDate: string;
          tags?: string[] | string;
        }) => {
          const id = new URL(item.url).pathname
            .split("/")
            .filter(Boolean)
            .pop()!;
          return {
            id,
            url: `/api/images/${encodeURIComponent(id)}`,
            name: item.name,
            extension: item.extension,
            size: item.size,
            uploadDate: item.uploadDate,
            tags: Array.isArray(item.tags)
              ? item.tags
              : item.tags
                  ?.split(",")
                  .map((tag) => tag.trim())
                  .filter(Boolean),
          };
        },
      ),
    );
  } catch {
    return apiError(
      "The image service is unavailable. Please try again when the server is online.",
      503,
    );
  }
}

export async function POST(request: Request) {
  if (Number(request.headers.get("content-length")) > MAX_FILE_SIZE + 65536)
    return apiError("File size must not exceed 4 MB.", 413);
  let data: FormData;
  try {
    data = await request.formData();
  } catch {
    return apiError("Invalid upload. Select a file and try again.", 400);
  }
  const file = data.get("file");
  const name = String(data.get("name") || "").trim();
  const tags = String(data.get("tags") || "").trim();
  const details = validateDetails(name, tags);
  if (details.name || details.tags)
    return apiError(details.name || details.tags, 400);
  if (!(file instanceof File)) return apiError("Please select a file.", 400);
  const validation = validateImage(file);
  if (validation) return apiError(validation, 400);
  const bytes = new Uint8Array(await file.slice(0, 12).arrayBuffer());
  const validSignature =
    file.type === "image/png"
      ? [137, 80, 78, 71, 13, 10, 26, 10].every(
          (value, index) => bytes[index] === value,
        )
      : file.type === "image/jpeg"
        ? bytes[0] === 255 && bytes[1] === 216 && bytes[2] === 255
        : ["GIF87a", "GIF89a"].includes(
            new TextDecoder().decode(bytes.slice(0, 6)),
          );
  if (!validSignature)
    return apiError("The file contents do not match its image format.", 400);
  const body = new FormData();
  body.set("name", name);
  body.set("file", file);
  tags
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean)
    .forEach((tag) => body.append("tags", tag));
  try {
    const response = await backendFetch("", { method: "POST", body });
    if (!response.ok)
      return apiError(
        response.status === 413
          ? "File size exceeds the server limit."
          : "The image could not be saved. Please try again.",
        response.status >= 500 ? 502 : response.status,
      );
    const location = response.headers.get("location");
    const id = location
      ? new URL(location, "http://localhost").pathname
          .split("/")
          .filter(Boolean)
          .pop()
      : undefined;
    return Response.json({ id }, { status: 201 });
  } catch {
    return apiError(
      "The image service is unavailable or timed out. Check the gallery before retrying.",
      503,
    );
  }
}
