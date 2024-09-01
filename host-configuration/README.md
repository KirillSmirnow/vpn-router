# VPN Router Host Configuration

## Network Interface

* Switch DHCP off
* Set static IP address
* Set default gateway (to real router IP address)
* Set DNS servers

```yaml
# Netplan configuration
network:
  ethernets:
    enp0s3:
      dhcp4: false
      addresses: [ 192.168.0.33/24 ]
      gateway4: 192.168.0.30
      nameservers:
        addresses: [ 8.8.8.8 ]
  version: 2
```

## Enable Forwarding

* `echo 1 >/proc/sys/net/ipv4/ip_forward`
* `iptables -t filter --policy FORWARD ACCEPT`
* `iptables -t nat -A POSTROUTING -j MASQUERADE`

## Install WireGuard

* Install tunnel configuration
* In the configuration, set `Table = off`
