#!/usr/bin/env python3
"""Notificador HTTP mínimo — usado só no contraste síncrono do Lab 04."""
from __future__ import annotations

from http.server import BaseHTTPRequestHandler, HTTPServer


class Handler(BaseHTTPRequestHandler):
    def do_POST(self) -> None:  # noqa: N802
        length = int(self.headers.get("Content-Length", "0"))
        _ = self.rfile.read(length)
        if self.path != "/notify":
            self.send_error(404)
            return
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(b'{"status":"notificado"}')

    def log_message(self, fmt: str, *args) -> None:
        print(f"[notificador] {args[0]}")


def main() -> None:
    port = 8099
    server = HTTPServer(("127.0.0.1", port), Handler)
    print(f"Notificador HTTP UP em http://127.0.0.1:{port}/notify")
    print("Ctrl+C para derrubar (simula notificador indisponível).")
    server.serve_forever()


if __name__ == "__main__":
    main()
