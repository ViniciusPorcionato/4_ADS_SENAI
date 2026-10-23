export type PartImage = {
  id: string;
  url: string;
  name: string;
  extension: string;
  size: number;
  uploadDate: string;
  tags?: string[];
};
export const MAX_FILE_SIZE = 4 * 1024 * 1024;
export const ACCEPTED_TYPES = ["image/png", "image/jpeg", "image/gif"];
export function formatSize(size: number) {
  return size >= 1024 * 1024
    ? `${(size / (1024 * 1024)).toFixed(1)} MB`
    : `${Math.max(1, Math.round(size / 1024))} KB`;
}
export function validateImage(file: { size: number; type: string } | null) {
  if (!file) return "Please select a file";
  if (!ACCEPTED_TYPES.includes(file.type))
    return "Invalid format. Accepted: PNG, JPEG, GIF";
  if (file.size === 0) return "The selected file is empty";
  if (file.size > MAX_FILE_SIZE) return "File size must not exceed 4 MB";
  return "";
}
export function validateDetails(name: string, tags: string) {
  return {
    name: !name.trim()
      ? "Image name is required"
      : name.trim().length > 50
        ? "Name must not exceed 50 characters"
        : "",
    tags: !tags.split(",").some((tag) => tag.trim())
      ? "Tags are required"
      : tags.trim().length > 50
        ? "Tags must not exceed 50 characters"
        : "",
  };
}
// Original Figma examples; never substituted for a failed or empty API response.
export const demoImages: PartImage[] = [
  {
    id: "gear",
    name: "Gear Assembly Unit",
    extension: "PNG",
    size: 2.4 * 1048576,
    uploadDate: "2025-07-15",
    tags: ["gear", "motor", "steel"],
  },
  {
    id: "board",
    name: "Controller Motherboard",
    extension: "PNG",
    size: 3.1 * 1048576,
    uploadDate: "2025-07-10",
    tags: ["circuit", "electronic"],
  },
  {
    id: "bolt",
    name: "Threaded Anchor Bolt",
    extension: "JPEG",
    size: 1.8 * 1048576,
    uploadDate: "2025-07-08",
    tags: ["hardware", "bolt"],
  },
  {
    id: "valve",
    name: "Hydraulic Valve System",
    extension: "PNG",
    size: 4 * 1048576,
    uploadDate: "2025-06-30",
    tags: ["hydraulic", "valve"],
  },
  {
    id: "coil",
    name: "Copper Inductor Coil",
    extension: "JPEG",
    size: 1.5 * 1048576,
    uploadDate: "2025-06-25",
    tags: ["electrical", "coil"],
  },
  {
    id: "hose",
    name: "Flexible Braided Hose",
    extension: "GIF",
    size: 3.8 * 1048576,
    uploadDate: "2025-06-18",
    tags: ["hose", "braided"],
  },
].map((item) => ({ ...item, url: `/design/${item.id}.png` }));
