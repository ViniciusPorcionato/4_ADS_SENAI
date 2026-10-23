import { AuthForm } from "@/components/auth-form";
export default async function LoginPage({
  searchParams,
}: {
  searchParams: Promise<{ loggedOut?: string }>;
}) {
  const params = await searchParams;
  return <AuthForm loggedOut={params.loggedOut === "1"} />;
}
