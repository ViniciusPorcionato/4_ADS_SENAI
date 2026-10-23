"use client";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState, type FormEvent } from "react";
import { formatSize, validateDetails, validateImage } from "@/lib/images";
import { Alert, FieldError, Header, Icon } from "./ui";

export function UploadForm() {
  const router = useRouter();
  const input = useRef<HTMLInputElement>(null);
  const activeRequest = useRef<XMLHttpRequest | null>(null);
  const [name, setName] = useState("");
  const [tags, setTags] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState("");
  const [dragging, setDragging] = useState(false);
  const [saving, setSaving] = useState(false);
  const [progress, setProgress] = useState(0);
  useEffect(
    () => () => {
      const xhr = activeRequest.current;
      if (xhr) {
        xhr.onload = null;
        xhr.onerror = null;
        xhr.ontimeout = null;
        xhr.abort();
      }
    },
    [],
  );
  useEffect(
    () => () => {
      if (preview) URL.revokeObjectURL(preview);
    },
    [preview],
  );
  function choose(selected?: File) {
    if (saving || !selected) return;
    const message = validateImage(selected);
    setErrors((previous) => ({ ...previous, file: message }));
    setError("");
    setFile(message ? null : selected);
    setPreview(message ? "" : URL.createObjectURL(selected));
    if (input.current) input.current.value = "";
  }
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (saving) return;
    const next = { ...validateDetails(name, tags), file: validateImage(file) };
    setErrors(next);
    setError("");
    if (Object.values(next).some(Boolean) || !file) {
      if (next.name) document.getElementById("image-name")?.focus();
      else if (next.tags) document.getElementById("image-tags")?.focus();
      else document.getElementById("select-image")?.focus();
      return;
    }
    const data = new FormData();
    data.set("name", name.trim());
    data.set("tags", tags.trim());
    data.set("file", file);
    const xhr = new XMLHttpRequest();
    activeRequest.current = xhr;
    xhr.open("POST", "/api/images");
    xhr.timeout = 60000;
    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable)
        setProgress(Math.round((event.loaded / event.total) * 100));
    };
    xhr.onload = () => {
      setSaving(false);
      let result: { id?: string; error?: string } = {};
      try {
        result = JSON.parse(xhr.responseText);
      } catch {
        /* Handled by the generic error below. */
      }
      if (xhr.status >= 200 && xhr.status < 300)
        router.push(
          `/Galeria?published=${encodeURIComponent(result.id || "new")}`,
        );
      else
        setError(
          result.error || "The image could not be saved. Please try again.",
        );
    };
    xhr.onerror = () => {
      setSaving(false);
      setError("Connection interrupted. Please try again.");
    };
    xhr.ontimeout = () => {
      setSaving(false);
      setError("Upload timed out. Check the gallery before retrying.");
    };
    setSaving(true);
    setProgress(0);
    xhr.send(data);
  }
  return (
    <>
      <Header />
      <main className="upload-page">
        <section className="panel upload-card">
          <h1>Publish New Image</h1>
          <form
            onSubmit={submit}
            noValidate
            className="upload-form"
            aria-busy={saving}
          >
            <div className="field">
              <label htmlFor="image-name">Image Name</label>
              <input
                id="image-name"
                placeholder="Enter image name"
                value={name}
                disabled={saving}
                onChange={(event) => {
                  setName(event.target.value);
                  setErrors((previous) => ({ ...previous, name: "" }));
                }}
                aria-invalid={!!errors.name}
                aria-describedby="name-hint name-error"
                required
              />
              <small id="name-hint">Max 50 characters</small>
              <FieldError id="name-error" message={errors.name} />
            </div>
            <div className="field">
              <label htmlFor="image-tags">Tags</label>
              <input
                id="image-tags"
                placeholder="e.g. motor, industrial, HD"
                value={tags}
                disabled={saving}
                onChange={(event) => {
                  setTags(event.target.value);
                  setErrors((previous) => ({ ...previous, tags: "" }));
                }}
                aria-invalid={!!errors.tags}
                aria-describedby="tags-hint tags-error"
                required
              />
              <small id="tags-hint">Max 50 characters</small>
              <FieldError id="tags-error" message={errors.tags} />
            </div>
            <div className="field">
              <label htmlFor="file-input">Upload Image</label>
              <input
                className="sr-only"
                tabIndex={-1}
                ref={input}
                id="file-input"
                type="file"
                accept="image/png,image/jpeg,image/gif"
                disabled={saving}
                onChange={(event) => choose(event.target.files?.[0])}
              />
              {file ? (
                <div className="file-ready">
                  <span>
                    <Icon name="file-check" />
                    <strong>{file.name}</strong>
                  </span>
                  <button
                    type="button"
                    disabled={saving}
                    className="text-link"
                    onClick={() => input.current?.click()}
                  >
                    Change file
                    <Icon name="refresh" />
                  </button>
                </div>
              ) : (
                <button
                  id="select-image"
                  type="button"
                  disabled={saving}
                  className={`dropzone ${dragging ? "dragging" : ""} ${errors.file ? "invalid" : ""}`}
                  onClick={() => input.current?.click()}
                  onDragOver={(event) => {
                    event.preventDefault();
                    setDragging(true);
                  }}
                  onDragLeave={() => setDragging(false)}
                  onDrop={(event) => {
                    event.preventDefault();
                    setDragging(false);
                    choose(event.dataTransfer.files[0]);
                  }}
                  aria-describedby="file-error"
                >
                  <Icon name="upload" />
                  <span>
                    <strong>Click to select or drag an image</strong>
                    <small>PNG, JPEG or GIF — Max 4 MB</small>
                  </span>
                </button>
              )}
              <FieldError id="file-error" message={errors.file} />
            </div>
            {file && preview && (
              <div className="field preview-field">
                <span className="field-label">Image Preview</span>
                <div className="preview-border">
                  <Image
                    src={preview}
                    alt={`Preview of ${file.name}`}
                    width={536}
                    height={240}
                    unoptimized
                    className="upload-preview"
                    onError={() => {
                      setFile(null);
                      setErrors((previous) => ({
                        ...previous,
                        file: "This file could not be read as an image",
                      }));
                    }}
                  />
                </div>
                <div className="preview-meta">
                  <span>
                    Filename: <b>{file.name}</b>
                  </span>
                  <span>
                    Size: <b>{formatSize(file.size)}</b>
                  </span>
                  <span>
                    Format: <b>{file.type.split("/")[1].toUpperCase()}</b>
                  </span>
                </div>
              </div>
            )}
            {saving && (
              <div className="upload-progress" role="status">
                <div>
                  <strong>
                    {progress === 100
                      ? "Saving image..."
                      : "Uploading image..."}
                  </strong>
                  <span>{progress}%</span>
                </div>
                <progress
                  value={progress}
                  max={100}
                  aria-label="Upload progress"
                />
              </div>
            )}
            {error && <Alert>{error}</Alert>}
            <div className="form-actions">
              {saving ? (
                <button type="button" className="button secondary" disabled>
                  Cancel
                </button>
              ) : (
                <Link href="/Galeria" className="button secondary">
                  Cancel
                </Link>
              )}
              <button
                type="submit"
                className="button primary"
                disabled={saving}
              >
                {saving && <Icon name="spinner" spin />}
                {saving ? "Saving..." : "Save"}
              </button>
            </div>
          </form>
        </section>
      </main>
    </>
  );
}
