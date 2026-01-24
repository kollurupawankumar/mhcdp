#!/usr/bin/env bash
set -euo pipefail

if ! command -v mmdc >/dev/null 2>&1; then
  echo "Mermaid CLI (mmdc) not found. Install via npm: npm i -g @mermaid-js/mermaid-cli" >&2
  exit 1
fi

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
MR_FILES=(\
  doc/diagrams/architecture.md \
  doc/diagrams/sequence.md \
  doc/diagrams/class-diagram.md \
  doc/diagrams/use_case.md \
)

for f in "${MR_FILES[@]}"; do
  out="${f%.*}.svg"
  if [ -f "$f" ]; then
    echo "Rendering $f to $out"
    mmdc -i "$f" -o "$out" --width 800
  fi
done

echo "Diagram regeneration complete. SVGs updated in doc/diagrams/*.svg"
