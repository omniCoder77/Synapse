#!/bin/bash

CONNECT_URL="http://localhost:18083/connectors"

declare -A connectors=(
  ["product-mongodb-connector"]="./connectors/product-connector.json"
  ["auth-postgres-connector"]="./connectors/auth-connector.json"
  ["order-postgres-connector"]="./connectors/order-connector.json"
)

for name in "${!connectors[@]}"; do
  config_file="${connectors[$name]}"
  echo "🔍 Checking connector: $name"

  if curl -s "$CONNECT_URL/$name" | grep -q '"name"'; then
    echo "✅ Connector '$name' already exists."
  else
    echo "🚀 Registering connector '$name'..."
    curl -s -X POST -H "Content-Type: application/json" \
         --data @"$config_file" \
         "$CONNECT_URL"
  fi

  echo ""
done
