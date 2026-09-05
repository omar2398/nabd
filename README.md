# Nabd

So during my time studying Computer Engineering and Control, I spent a good chunk of it working with embedded systems, IoT devices, and sensors. You know how it is, microcontrollers, reading sensor data, figuring out why the readings are wrong, that kind of stuff so at some point I started getting into backend development and wanted to build something that actually combines both worlds instead of keeping them completely separate, or completely forgot what I had learned from the college.

I also wanted to experiment with LLMs and chatbots, since I didn't integrate with any LLM in any of the projects that I have worked on them before, so this led me to integrate DeepSeek using Spring AI with opencode into the project. The goal was to build something a little different not just another CRUD app with basic microservices architecture that has no real meaning to the user use.

So Nabd is basically an IoT energy monitoring system built as a microservices application and it simulates sensor data coming from devices, ingests it, processes it, triggers alerts when usage exceeds a threshold and uses an LLM to give you insights and energy saving tips based on your consumption data I think that Not bad for a side project :D

The main reason I built it is learning. It's a personal project where I'm practicing microservices design, APIs, security, Kafka, and LLM integration all in one project, rather to refresh what I learned before in Spring and learning new stuff like using Influx DB, Spring AI since I studied those 2 topics while I'm working in this porject :)

## Services

| Service | Port | What it does |
|---|---|---|
| api-gateway | 9000 | Single entry point for the app |
| user-service | 8080 | User management and authentication |
| device-service | 8081 | Manages IoT devices |
| ingestion-service | 8082 | Takes in energy usage data from devices |
| usage-service | 8083 | Stores and queries energy usage from InfluxDB |
| alert-servicce | 8084 | Sends email alerts when usage crosses a threshold |
| insight-service | 8085 | Calls an LLM to generate energy insights and tips |

## Stack

- **Spring Boot** — each service is its single Spring Boot app
- **Spring Cloud Gateway** — routes everything through one gateway
- **Kafka** — async messaging between services
- **PostgreSQL** — stores user and device data
- **InfluxDB** — stores time-series energy data
- **Spring AI + Local DeepSeek Model** — uses LLM for energy insights and tips
- **Mailpit** — catches emails locally during development using Docker too :)
- **Docker** — runs all the infrastructure of the project

## How to run

You need Docker. From the root directory:

```
docker compose up -d
```

Then start whichever services you need from your IDE or with Maven.

To stop the containers:

```
docker compose down
```

If something is going wrong with the database, you might need to wipe the volumes:

```
docker compose down -v
```

## Kafka UI

If you want to peek at what's going through Kafka, there's a UI running at [http://localhost:8070](http://localhost:8070).
