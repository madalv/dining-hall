## Steps to run Dining Hall

1. Make sure Kitchen and Ordering Service are already running.
2. Run the command below:

```
docker compose up --build
```

## Other notes

If you want to connect the cluster of restaurants running in Docker containers to the OrderingService
which is running on your local machine, change following fields in ALL `config.json` files as such:

```json
"ordering_service": "localhost:9000",
"address": "localhost:8083"
```

And for the love of God, do not change the ports!