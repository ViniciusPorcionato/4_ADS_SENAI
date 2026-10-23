import Image from "next/image";
import Link from "next/link";

const sizes = {
  cog: 24,
  plus: 16,
  logout: 18,
  search: 16,
  chevron: 16,
  zoom: 14,
  upload: 32,
  alert: 16,
  spinner: 16,
  "spinner-blue": 24,
  empty: 36,
  download: 14,
  success: 20,
  close: 10,
  info: 14,
  "file-check": 18,
  refresh: 12,
};
export function Icon({
  name,
  spin = false,
}: {
  name: keyof typeof sizes;
  spin?: boolean;
}) {
  return (
    <Image
      src={`/design/${name}.svg`}
      alt=""
      width={sizes[name]}
      height={sizes[name]}
      className={spin ? "icon spin" : "icon"}
      unoptimized
    />
  );
}
export function Brand() {
  return (
    <Link href="/Galeria" className="brand" aria-label="NEXORA gallery">
      <Icon name="cog" />
      <span>NEXORA</span>
    </Link>
  );
}
export function Header() {
  return (
    <header className="site-header">
      <Brand />
      <nav aria-label="Main navigation">
        <Link className="button primary" href="/Formulario">
          <Icon name="plus" />
          Add New
        </Link>
        <Link className="logout" href="/login?loggedOut=1">
          Log Out
          <Icon name="logout" />
        </Link>
      </nav>
    </header>
  );
}
export function Alert({
  children,
  info = false,
}: {
  children: React.ReactNode;
  info?: boolean;
}) {
  return (
    <div
      className={`alert ${info ? "info" : "error"}`}
      role={info ? "status" : "alert"}
    >
      <Icon name={info ? "info" : "alert"} />
      <span>{children}</span>
    </div>
  );
}
export function FieldError({ id, message }: { id: string; message?: string }) {
  return message ? (
    <span id={id} className="field-error">
      <Icon name="alert" />
      {message}
    </span>
  ) : null;
}
