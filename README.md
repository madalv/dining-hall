## Steps to run Dining Hall

1. Make sure Kitchen and Ordering Service are already running.
2. Run the command below:

```
docker compose up --build
```

## Other notes

If you want to connect the cluster of restaurants running in Docker containers to the OrderingService
which is running on your local machine, change following fields in ALL `config.json` files as such, **where IP is the IP address of your host inside the docker network**:

```json
"ordering_service": "IP:9000",
"address": "localhost:8083"
```

The IP address can be found running:

```
ip addr show docker0 | grep -Po 'inet \K[\d.]+'
```

And for the love of God, do not change the ports!