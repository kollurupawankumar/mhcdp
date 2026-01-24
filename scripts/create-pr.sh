#!/usr/bin/env bash
set -euo pipefail

# Minimal PR creator script
BRANCH=${1:-phase6plus-final}

git checkout -b "$BRANCH"
git push -u origin "$BRANCH"

if command -v gh >/dev/null 2>&1; then
  gh pr create --title "MDHP: Phase 6+ Spark Runner, UI Metadata Editing, EMR-Ready Runtime" \
    --body "$(cat README-Runbook.md)" \
    --head "$BRANCH" --base main
else
  echo "GH CLI not installed. Please install gh and run: gh pr create --title ... --body ... --head $BRANCH --base main"
fi

echo "PR creation attempted for branch: $BRANCH"
