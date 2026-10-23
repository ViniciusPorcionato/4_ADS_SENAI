import { Gallery } from "@/components/gallery";
export const metadata = { title: "Gallery" };
export default async function GalleryPage({
  searchParams,
}: {
  searchParams: Promise<{ demo?: string; published?: string }>;
}) {
  const params = await searchParams;
  return (
    <Gallery
      key={`${params.demo}-${params.published}`}
      demo={params.demo === "1"}
      published={params.published}
    />
  );
}
