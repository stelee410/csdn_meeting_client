#!/bin/sh
# Install git hooks. Run from project root.
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
HOOKS_SRC="$SCRIPT_DIR/hooks"
HOOKS_DST="$PROJECT_ROOT/.git/hooks"

mkdir -p "$HOOKS_DST"
cp "$HOOKS_SRC/pre-commit" "$HOOKS_DST/pre-commit"
chmod +x "$HOOKS_DST/pre-commit"
echo "Git hooks installed. Pre-commit will run mvn verify before each commit."
