#!/bin/bash
# setup-aws-tools.sh — Source this in your shell to load AWS tools
# Sourcea en tu shell para cargar las herramientas AWS

export PATH=/Users/user/bin:$PATH

# Load AWS creds from .env if present
if [ -f "$(dirname "${BASH_SOURCE[0]}")/../.env" ]; then
    set -a
    source "$(dirname "${BASH_SOURCE[0]}")/../.env"
    set +a
fi

echo "✓ aws: $(aws --version 2>&1 | head -1)"
echo "✓ terraform: $(terraform version | head -1)"
echo "✓ helm: $(helm version --short 2>&1 | head -1)"
echo "✓ kubectl: $(kubectl version --client --short 2>&1 | head -1)"
