# Vid2Knowledge

Vid2Knowledge is a web application that turns public YouTube videos into active-learning materials. A user submits a YouTube URL, and the backend will use Gemini to generate structured summaries, key takeaways, flashcards, and quizzes.

The project is currently in the early development stage. Its first product goal is to validate the quality and reliability of Gemini's direct YouTube URL understanding before expanding into a full MVP.

## Technology Stack

- React
- Vite
- Java
- Spring Boot
- Maven
- PostgreSQL
- Flyway
- Gemini API
- Docker
- GitHub Actions
- Terraform
- AWS

## Product Features

This list is updated as the product evolves.

- YouTube URL submission and validation
- AI-powered video analysis with Gemini
- Structured learning summaries
- Key takeaways
- Flashcards
- Multiple-choice quizzes with explanations
- Analysis history
- User authentication
- Usage quota and rate limiting
- Markdown, PDF, and Word export

## Project Structure

This section must be updated whenever a source directory is added, removed, or repurposed.

```text
Vid2Knowledge/
├── .github/
│   └── workflows/
│       └── ci.yml                         # GitHub Actions continuous-integration workflow
├── backend/
│   ├── .mvn/
│   │   └── wrapper/                       # Maven Wrapper configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vid2knowledge/
│   │   │   │   ├── Vid2KnowledgeApplication.java # Spring Boot application entry point
│   │   │   │   ├── analysis/              # Video-analysis bounded context
│   │   │   │   │   ├── api/               # REST endpoints and request/response DTOs
│   │   │   │   │   ├── application/       # Use cases and application services
│   │   │   │   │   ├── domain/            # Analysis domain model and business rules
│   │   │   │   │   └── infrastructure/    # Gemini client, persistence, and external adapters
│   │   │   │   ├── auth/                  # Authentication and authorization module
│   │   │   │   ├── common/                # Cross-cutting backend code
│   │   │   │   │   ├── api/               # Shared API contracts
│   │   │   │   │   ├── exception/         # API error model and global exception handling
│   │   │   │   │   └── validation/        # Reusable validation rules
│   │   │   │   ├── config/                # Security, CORS, and configuration properties
│   │   │   │   ├── usage/                 # Quota and usage-tracking module
│   │   │   │   └── user/                  # User-profile module
│   │   │   └── resources/
│   │   │       ├── application.yaml       # Shared Spring Boot configuration
│   │   │       ├── application-local.yaml # Local environment configuration
│   │   │       ├── application-prod.yaml  # Production environment configuration
│   │   │       ├── db/migration/          # Flyway SQL migrations
│   │   │       ├── static/                # Static backend-served assets, if needed
│   │   │       └── templates/             # Server-side templates, if needed
│   │   └── test/
│   │       └── java/com/vid2knowledge/    # Backend integration and unit tests
│   ├── pom.xml                            # Maven dependencies and build configuration
│   ├── mvnw                               # Maven Wrapper for Unix-like systems
│   └── mvnw.cmd                           # Maven Wrapper for Windows
├── frontend/
│   ├── src/
│   │   ├── app/                           # Application shell and root React component
│   │   ├── assets/
│   │   │   └── logo/                      # Brand assets
│   │   ├── features/                      # Feature-based UI modules
│   │   │   ├── analysis/
│   │   │   │   ├── api/                   # Analysis API calls
│   │   │   │   ├── components/            # Analysis-specific UI components
│   │   │   │   └── pages/                 # Analysis pages
│   │   │   ├── auth/                      # Authentication UI
│   │   │   ├── history/                   # Analysis-history UI
│   │   │   └── usage/                     # Quota and usage UI
│   │   ├── shared/                        # Reusable frontend code
│   │   │   ├── api/                       # Shared HTTP client and API utilities
│   │   │   ├── components/                # Shared UI components
│   │   │   ├── hooks/                     # Reusable React hooks
│   │   │   ├── lib/                       # Framework-agnostic utilities
│   │   │   └── styles/                    # Shared style definitions
│   │   ├── main.jsx                       # React bootstrap entry point
│   │   └── style.css                      # Global styles
│   ├── index.html                         # Vite HTML entry point
│   ├── package.json                       # Frontend scripts and dependencies
│   └── package-lock.json                  # Locked npm dependency versions
├── docs/
│   ├── features.md                        # Product scope and feature definition
│   └── plan.md                            # Delivery plan and implementation phases
├── infra/
│   ├── cloud-run/                         # Cloud Run deployment resources
│   ├── docker/                            # Docker-related resources
│   └── terraform/                         # Infrastructure as Code resources
├── docker-compose.yaml                    # Local PostgreSQL container
└── README.md                              # Project documentation
```

## Access

There is no public deployment URL yet. This section will be updated when the application is deployed.

| Environment | Frontend | Backend API |
|---|---|---|
| Local development | `http://localhost:5173` | `http://localhost:8080` |
| Public deployment | Coming soon | Coming soon |

## Documentation

- [Product Scope and Features](docs/features.md)
- [Implementation Plan](docs/plan.md)

## Author

**Pham Gia Khanh**

Full-Stack Developer

**Core Technologies**

- React
- HTML
- CSS
- JavaScript
- Java
- Spring
- Docker
- CI/CD
- GitHub Actions

**AWS Cloud and Infrastructure as Code**

- IAM
- Route 53
- Security Groups
- EC2
- S3
- Terraform
