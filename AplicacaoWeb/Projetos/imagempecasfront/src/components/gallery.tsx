"use client";
import Image from "next/image";
import Link from "next/link";
import {
  useCallback,
  useEffect,
  useRef,
  useState,
  type FormEvent,
} from "react";
import { demoImages, formatSize, type PartImage } from "@/lib/images";
import { Alert, Header, Icon } from "./ui";

async function fetchImages(
  search: string,
  format: string,
  signal: AbortSignal,
): Promise<PartImage[]> {
  const response = await fetch(
    `/api/images?${new URLSearchParams({ query: search, extension: format })}`,
    { signal },
  );
  const result = await response.json();
  if (!response.ok)
    throw new Error(result.error || "Images could not be loaded.");
  return result;
}

function ImageViewer({ item, close }: { item: PartImage; close: () => void }) {
  const dialog = useRef<HTMLDialogElement>(null);
  const [dimensions, setDimensions] = useState("");
  const [failed, setFailed] = useState(false);
  useEffect(() => {
    const element = dialog.current;
    const previouslyFocused = document.activeElement as HTMLElement | null;
    element?.showModal();
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      element?.close();
      document.body.style.overflow = previousOverflow;
      previouslyFocused?.focus();
    };
  }, []);
  return (
    <dialog
      ref={dialog}
      className="image-viewer"
      aria-label={item.name}
      onCancel={close}
    >
      <div className="viewer-toolbar">
        <div className="viewer-details">
          <strong>{item.name}</strong>
          <span>
            Size: <b>{formatSize(item.size)}</b>
          </span>
          {dimensions && (
            <span>
              Dimensions: <b>{dimensions}</b>
            </span>
          )}
        </div>
        <div className="viewer-actions">
          <a
            href={item.url}
            download={`${item.name}.${item.url.startsWith("/design/") ? "png" : item.extension.toLowerCase()}`}
            className="download-link"
          >
            <span>Download image</span>
            <Icon name="download" />
          </a>
          <button
            type="button"
            className="viewer-close"
            onClick={close}
            aria-label="Close image"
          >
            ×
          </button>
        </div>
      </div>
      <div
        className="viewer-canvas"
        onClick={(event) => {
          if (event.target === event.currentTarget) close();
        }}
      >
        {failed ? (
          <p role="alert">
            This image could not be loaded. Close the viewer and try again.
          </p>
        ) : (
          <Image
            src={item.url}
            alt={item.name}
            width={1000}
            height={562}
            unoptimized
            className="original-image"
            onLoad={(event) =>
              setDimensions(
                `${event.currentTarget.naturalWidth} × ${event.currentTarget.naturalHeight}`,
              )
            }
            onError={() => setFailed(true)}
          />
        )}
      </div>
    </dialog>
  );
}

function ImageCard({
  item,
  open,
  fresh,
}: {
  item: PartImage;
  open: () => void;
  fresh: boolean;
}) {
  const [failed, setFailed] = useState(false);
  return (
    <article className={`image-card ${fresh ? "new-image" : ""}`}>
      <button
        className="thumbnail"
        onClick={open}
        aria-label={`View ${item.name} full size`}
      >
        {failed ? (
          <span className="image-unavailable">Image unavailable</span>
        ) : (
          <Image
            src={item.url}
            alt={item.name}
            fill
            sizes="(max-width: 640px) 100vw, (max-width: 1000px) 50vw, 33vw"
            unoptimized
            onError={() => setFailed(true)}
          />
        )}
        {fresh && <span className="new-badge">Newly published</span>}
        <span className="zoom-hint">
          <Icon name="zoom" />
          Click to view full size
        </span>
      </button>
      <div className="card-details">
        <h2>{item.name}</h2>
        <div className="tags">
          {item.tags?.map((tag) => (
            <span key={tag}>{tag}</span>
          ))}
        </div>
        <div className="card-rule" aria-hidden="true" />
        <div className="image-meta">
          <span>
            Format: <b>{item.extension}</b>
          </span>
          <span>
            Size: <b>{formatSize(item.size)}</b>
          </span>
          <span>
            Date: <b>{item.uploadDate}</b>
          </span>
        </div>
      </div>
    </article>
  );
}

export function Gallery({
  demo = false,
  published,
}: {
  demo?: boolean;
  published?: string;
}) {
  const [items, setItems] = useState<PartImage[]>(demo ? demoImages : []);
  const [query, setQuery] = useState("");
  const [extension, setExtension] = useState("");
  const [loading, setLoading] = useState(!demo);
  const [error, setError] = useState("");
  const [selected, setSelected] = useState<PartImage | null>(null);
  const [toast, setToast] = useState(!!published);
  const request = useRef<AbortController | null>(null);
  const load = useCallback(async (search = "", format = "") => {
    request.current?.abort();
    const controller = new AbortController();
    request.current = controller;
    try {
      const result = await fetchImages(search, format, controller.signal);
      setItems(result);
    } catch (err) {
      if (!controller.signal.aborted)
        setError(
          err instanceof Error ? err.message : "Images could not be loaded.",
        );
    } finally {
      if (!controller.signal.aborted) setLoading(false);
    }
  }, []);
  useEffect(() => {
    if (demo) return;
    const controller = new AbortController();
    request.current = controller;
    fetchImages("", "", controller.signal)
      .then((result) => {
        if (!controller.signal.aborted) setItems(result);
      })
      .catch((err) => {
        if (!controller.signal.aborted)
          setError(
            err instanceof Error ? err.message : "Images could not be loaded.",
          );
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => {
      controller.abort();
      request.current?.abort();
    };
  }, [demo]);
  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(false), 6000);
    return () => window.clearTimeout(timer);
  }, [toast]);
  function search(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (demo) {
      const term = query.trim().toLowerCase();
      setItems(
        demoImages.filter(
          (item) =>
            (!extension || item.extension === extension) &&
            [item.name, ...(item.tags || [])].some((value) =>
              value.toLowerCase().includes(term),
            ),
        ),
      );
    } else {
      setLoading(true);
      setError("");
      void load(query.trim(), extension);
    }
  }
  return (
    <>
      <Header />
      <main className="gallery-page">
        <h1 className="sr-only">Industrial parts image gallery</h1>
        <form className="search-bar" role="search" onSubmit={search}>
          <div className="search-input">
            <Icon name="search" />
            <input
              aria-label="Search by name or tag"
              placeholder="Search by name or tag..."
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
          </div>
          <div className="extension-select">
            <select
              aria-label="Image extension"
              value={extension}
              onChange={(event) => setExtension(event.target.value)}
            >
              <option value="">Extension: All</option>
              <option>PNG</option>
              <option>JPEG</option>
              <option>GIF</option>
            </select>
            <Icon name="chevron" />
          </div>
          <button className="button navy" disabled={loading}>
            {loading ? "Searching..." : "Search"}
          </button>
        </form>
        {demo && (
          <aside className="demo-notice">
            Design demo · Sample images from Figma.{" "}
            <Link href="/Galeria">Open live gallery</Link>
          </aside>
        )}
        {loading ? (
          <div className="empty-state" role="status">
            <Icon name="spinner-blue" spin />
            <p>Searching images...</p>
          </div>
        ) : error ? (
          <div className="gallery-error">
            <Alert>{error}</Alert>
            <div className="error-actions">
              <button
                className="button primary"
                onClick={() => {
                  setLoading(true);
                  setError("");
                  void load(query, extension);
                }}
              >
                Try again
              </button>
              <Link href="/Galeria?demo=1" className="button secondary">
                Explore design demo
              </Link>
            </div>
          </div>
        ) : items.length ? (
          <div className="gallery-grid">
            {[...items]
              .sort(
                (a, b) =>
                  Number(b.id === published) - Number(a.id === published),
              )
              .map((item) => (
                <ImageCard
                  key={item.id}
                  item={item}
                  fresh={item.id === published}
                  open={() => setSelected(item)}
                />
              ))}
          </div>
        ) : (
          <div className="empty-state" role="status">
            <div className="empty-icon">
              <Icon name="empty" />
            </div>
            <div>
              <h2>No results found!</h2>
              <p>Try adjusting your search terms or filters.</p>
            </div>
            <Link href="/Formulario" className="text-link">
              Publish a new image
            </Link>
          </div>
        )}
        {toast && (
          <div className="success-toast" role="status">
            <div>
              <Icon name="success" />
              <strong>Image published successfully!</strong>
              <button
                aria-label="Dismiss notification"
                onClick={() => setToast(false)}
              >
                <Icon name="close" />
              </button>
            </div>
            <div className="toast-track">
              <span />
            </div>
          </div>
        )}
      </main>
      {selected && (
        <ImageViewer item={selected} close={() => setSelected(null)} />
      )}
    </>
  );
}
