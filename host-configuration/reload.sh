#!/bin/bash

# Disable existing
for ((table = 1000; table < 1100; table++)); do
  ip rule del lookup "$table" &>/dev/null
done

# Enable required

table=1000
while read entry; do
  name=$(echo "$entry" | awk '{print $1}')
  ip=$(echo "$entry" | awk '{print $2}')
  enabled=$(echo "$entry" | awk '{print $3}')

  echo ">> $name"
  if [[ "$enabled" = "1" ]]; then
    ip rule add from "$ip" lookup "$table"
    ip route add default dev do table "$table"
    let table+=1
  fi
done </root/vpn-ips.txt
