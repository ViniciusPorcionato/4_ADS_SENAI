"use client";
import Link from "next/link";
import { useState, type FormEvent } from "react";
import { Alert, Brand, FieldError } from "./ui";

export function AuthForm({
  signup = false,
  loggedOut = false,
}: {
  signup?: boolean;
  loggedOut?: boolean;
}) {
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [message, setMessage] = useState("");
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = String(data.get("email") || "").trim();
    const password = String(data.get("password") || "");
    const next: Record<string, string> = {};
    if (signup && !String(data.get("name") || "").trim())
      next.name = "Full name is required";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
      next.email = "Invalid email format";
    if (!password) next.password = "Password is required";
    else if (signup && password.length < 8)
      next.password = "Password must be at least 8 characters";
    if (signup && password !== data.get("confirm"))
      next.confirm = "Passwords do not match";
    setErrors(next);
    setMessage(
      Object.keys(next).length
        ? ""
        : "Account access is not available yet. You can browse the public gallery below.",
    );
    if (Object.keys(next).length)
      event.currentTarget
        .querySelector<HTMLInputElement>(`[name="${Object.keys(next)[0]}"]`)
        ?.focus();
  }
  const fields = [
    ...(signup
      ? [
          {
            name: "name",
            label: "Full Name",
            type: "text",
            placeholder: "Enter your full name",
            complete: "name",
          },
        ]
      : []),
    {
      name: "email",
      label: "Email Address",
      type: "email",
      placeholder: "Enter your email address",
      complete: "email",
    },
    {
      name: "password",
      label: "Password",
      type: "password",
      placeholder: "••••••••",
      complete: signup ? "new-password" : "current-password",
    },
    ...(signup
      ? [
          {
            name: "confirm",
            label: "Confirm Password",
            type: "password",
            placeholder: "••••••••",
            complete: "new-password",
          },
        ]
      : []),
  ];
  return (
    <main className="auth-page">
      {loggedOut && (
        <div className="auth-status">
          <Alert info>You have left the gallery.</Alert>
        </div>
      )}
      <section
        className={`auth-card panel ${signup ? "signup-card" : ""}`}
        aria-label={signup ? "Sign up" : "Log in"}
      >
        <div className="auth-heading">
          <Brand />
          <h1>
            {signup
              ? "Create your contributor account"
              : "Access the industrial parts database"}
          </h1>
        </div>
        <form className="auth-form" noValidate onSubmit={submit}>
          {fields.map((field) => (
            <div className="field" key={field.name}>
              <label htmlFor={field.name}>{field.label}</label>
              <input
                id={field.name}
                name={field.name}
                type={field.type}
                placeholder={field.placeholder}
                autoComplete={field.complete}
                required
                aria-invalid={!!errors[field.name]}
                aria-describedby={
                  errors[field.name] ? `${field.name}-error` : undefined
                }
                onChange={() => {
                  setErrors((previous) => ({ ...previous, [field.name]: "" }));
                  setMessage("");
                }}
              />
              <FieldError
                id={`${field.name}-error`}
                message={errors[field.name]}
              />
            </div>
          ))}
          {message && <Alert info>{message}</Alert>}
          <div className={signup ? "auth-actions" : ""}>
            {signup && (
              <Link href="/" className="button secondary">
                Cancel
              </Link>
            )}
            <button className="button primary auth-submit" type="submit">
              {signup ? "Sign Up" : "Log In"}
            </button>
          </div>
        </form>
        {!signup && (
          <p className="signup-prompt">
            Don&apos;t have an account? <Link href="/cadastro">Sign Up</Link>
          </p>
        )}
      </section>
      <aside className="access-note">
        <p>Account access is not available yet.</p>
        <Link href="/Galeria">Browse public gallery</Link>
        <span aria-hidden="true"> · </span>
        <Link href="/Galeria?demo=1">Explore design demo</Link>
      </aside>
    </main>
  );
}
