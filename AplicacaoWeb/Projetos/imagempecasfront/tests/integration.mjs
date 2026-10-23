import assert from "node:assert/strict";
import { createServer } from "node:http";
import { readFile } from "node:fs/promises";
import { spawn } from "node:child_process";
import { once } from "node:events";

// Exercise the real Next.js route handlers against an isolated Spring-compatible
// HTTP fixture. No user database or running application is modified.
const fixture = await readFile(
  new URL("../public/design/gear.png", import.meta.url),
);
let uploaded = false;
let lastQuery;
let upstreamStatus = 200;
const mock = createServer(async (request, response) => {
  const url = new URL(request.url, "http://localhost");
  if (upstreamStatus !== 200) {
    response.writeHead(upstreamStatus).end();
    return;
  }
  if (request.method === "POST" && url.pathname === "/v1/images") {
    const chunks = [];
    for await (const chunk of request) chunks.push(chunk);
    const body = Buffer.concat(chunks).toString("latin1");
    assert.match(
      request.headers["content-type"],
      /multipart\/form-data; boundary=/,
    );
    assert.match(body, /name="name"\r\n\r\nTest gear/);
    assert.match(body, /name="tags"\r\n\r\ngear/);
    assert.match(body, /name="tags"\r\n\r\nsteel/);
    assert.match(body, /name="file"; filename="gear.png"/);
    uploaded = true;
    response
      .writeHead(201, { Location: "http://localhost/v1/images/test-image" })
      .end();
    return;
  }
  if (url.pathname === "/v1/images/test-image") {
    response.writeHead(200, { "Content-Type": "image/png" }).end(fixture);
    return;
  }
  lastQuery = url.searchParams;
  response
    .writeHead(200, { "Content-Type": "application/json" })
    .end(
      JSON.stringify(
        uploaded
          ? [
              {
                url: "http://localhost/v1/images/test-image",
                name: "Test gear",
                extension: "PNG",
                size: fixture.length,
                uploadDate: "2026-10-22",
              },
            ]
          : [],
      ),
    );
});
mock.listen(0, "127.0.0.1");
await once(mock, "listening");
const portProbe = createServer();
portProbe.listen(0, "127.0.0.1");
await once(portProbe, "listening");
const appPort = portProbe.address().port;
await new Promise((resolve) => portProbe.close(resolve));
let logs = "";
const app = spawn(
  process.execPath,
  ["node_modules/next/dist/bin/next", "start", "-p", String(appPort)],
  {
    env: {
      ...process.env,
      IMAGES_API_URL: `http://127.0.0.1:${mock.address().port}`,
    },
    stdio: ["ignore", "pipe", "pipe"],
    windowsHide: true,
  },
);
app.stdout.on("data", (data) => {
  logs += data;
});
app.stderr.on("data", (data) => {
  logs += data;
});
const base = `http://127.0.0.1:${appPort}`;
try {
  let ready = false;
  for (let attempt = 0; attempt < 60; attempt++) {
    try {
      if ((await fetch(base)).ok) {
        ready = true;
        break;
      }
    } catch {
      /* startup */
    }
    await new Promise((resolve) => setTimeout(resolve, 250));
  }
  assert.ok(ready, `Next.js did not start: ${logs}`);
  const empty = await fetch(`${base}/api/images?query=gear&extension=PNG`);
  assert.deepEqual(await empty.json(), []);
  assert.equal(lastQuery.get("query"), "gear");
  assert.equal(lastQuery.get("extension"), "PNG");
  const invalid = new FormData();
  invalid.set("name", "");
  invalid.set("tags", "gear");
  assert.equal(
    (await fetch(`${base}/api/images`, { method: "POST", body: invalid }))
      .status,
    400,
  );
  const forged = new FormData();
  forged.set("name", "Test gear");
  forged.set("tags", "gear");
  forged.set(
    "file",
    new Blob(["not an image"], { type: "image/png" }),
    "forged.png",
  );
  assert.equal(
    (await fetch(`${base}/api/images`, { method: "POST", body: forged }))
      .status,
    400,
  );
  const oversize = new FormData();
  oversize.set("name", "Test gear");
  oversize.set("tags", "gear");
  oversize.set(
    "file",
    new Blob([new Uint8Array(4 * 1024 * 1024 + 1)], { type: "image/png" }),
    "big.png",
  );
  const rejected = await fetch(`${base}/api/images`, {
    method: "POST",
    body: oversize,
  });
  assert.ok([400, 413].includes(rejected.status));
  const valid = new FormData();
  valid.set("name", "Test gear");
  valid.set("tags", "gear, steel");
  valid.set("file", new Blob([fixture], { type: "image/png" }), "gear.png");
  const saved = await fetch(`${base}/api/images`, {
    method: "POST",
    body: valid,
  });
  assert.equal(saved.status, 201);
  assert.deepEqual(await saved.json(), { id: "test-image" });
  assert.ok(uploaded);
  const list = await (await fetch(`${base}/api/images`)).json();
  assert.equal(list[0].url, "/api/images/test-image");
  assert.equal(list[0].name, "Test gear");
  const image = await fetch(`${base}${list[0].url}`);
  assert.equal(image.headers.get("content-type"), "image/png");
  assert.deepEqual(Buffer.from(await image.arrayBuffer()), fixture);
  upstreamStatus = 500;
  assert.equal((await fetch(`${base}/api/images`)).status, 502);
  console.log(
    "PASS: search filters, empty state, invalid and oversized uploads, file signature, multipart contract, save, image proxy and backend errors.",
  );
} finally {
  app.kill();
  mock.closeAllConnections();
  await new Promise((resolve) => mock.close(resolve));
}
