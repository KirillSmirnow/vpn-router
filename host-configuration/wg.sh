#!/bin/bash

wg-quick up do
iptables -t nat -A POSTROUTING -j MASQUERADE
/root/reload.sh
