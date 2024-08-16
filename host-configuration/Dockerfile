FROM ubuntu
RUN apt update && \
    apt upgrade -y && \
    apt install -y iptables
RUN iptables -t nat -A POSTROUTING -j MASQUERADE
CMD sleep infinity
