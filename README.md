# Spring AI Agent

A customer support chatbot application built with Spring AI that provides intelligent responses to customer inquiries about Amplify Federal. The application uses Retrieval Augmented Generation (RAG) to enhance responses with information from the Amplify Federal website.

## Features

- AI-powered customer support chatbot
- Conversation memory to maintain context across interactions
- Automatic web scraping to build knowledge base
- Vector search for relevant information retrieval
- REST API for easy integration

## Technologies

- Java 21
- Spring Boot 3.4.5
- Spring AI 1.0.0
- OpenAI API for embeddings and chat completions
- Milvus Vector Database for similarity search
- Apache Tika for document processing
- Docker for containerization

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- OpenAI API key

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd spring-ai-agent
```

### 2. Configure OpenAI API Key

Edit `src/main/resources/application.yml` to add your OpenAI API key:

```yaml
spring:
  ai:
    openai:
      api-key: "your-openai-api-key"
```

### 3. Start Milvus Vector Database

```bash
docker-compose up -d
```

This will start the Milvus vector database and its dependencies (etcd and MinIO).

### 4. Build and Run the Application

```bash
./mvnw spring-boot:run
```

The application will start, automatically scrape the Amplify Federal website, and populate the vector store with the extracted information.

## Usage

### REST API Endpoints

The application exposes the following endpoints:

#### GET /ai

Simple endpoint for asking questions:

```
GET /ai?question=What are Amplify Federal's core values?
```

Returns a plain text response.

#### POST /ai

Endpoint for more structured interactions:

```
POST /ai
Content-Type: application/json

{
  "question": "What services does Amplify Federal offer?"
}
```

Returns a JSON response:

```json
{
  "answer": "Amplify Federal offers..."
}
```

### Maintaining Conversation Context

The application uses HTTP sessions to maintain conversation context. Make sure to include session cookies in subsequent requests to maintain the conversation flow.

## Configuration

The application can be configured through `application.yml`:

```yaml
spring:
  application:
    name: spring-ai-agent
  ai:
    openai:
      api-key: "your-openai-api-key"
      embedding:
        options:
          model: "text-embedding-3-small"
    vectorstore:
      milvus:
        client:
          host: "localhost"
          port: 19530
          username: "root"
          password: "milvus"
        databaseName: "default"
        collectionName: "vector_store"
        embeddingDimension: 1536
        indexType: IVF_FLAT
        metricType: COSINE
        initialize-schema: true
app:
  site:
    baseUrl: "https://amplifyfederal.com"
```

## Project Structure

- `src/main/java/net/starkenberg/ai/springaiagent/`
  - `SpringAiAgentApplication.java` - Main application entry point
  - `bootstrap/` - Application initialization
    - `VectorstoreLoader.java` - Loads website data into vector store
  - `chat/` - Chat functionality
    - `CustomerSupportAssistant.java` - Core chat logic
    - `Question.java` - Request model
    - `Answer.java` - Response model
  - `controllers/` - REST API endpoints
    - `ChatController.java` - Exposes chat functionality
  - `services/` - Business logic
    - `WebScraperService.java` - Scrapes website content
    - `OpenAIService.java` - Interacts with OpenAI API

## Building a Docker Image

To build a Docker image of the application:

```bash
./mvnw spring-boot:build-image
```

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Developer

- Brad Starkenberg ([@bstarke](https://github.com/bstarke))
