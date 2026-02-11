# CI/CD Quality Lab

Este repositório é um laboratório prático de **CI/CD com foco em qualidade de código**, utilizando **Jenkins** e **SonarQube Community Edition**.

O objetivo do projeto é simular um fluxo real de ambiente corporativo, validando:

- Análise estática de código
- Code Smells, Bugs e Vulnerabilidades
- Quality Gates
- Integração Jenkins + SonarQube
- Boas práticas de automação

O projeto foi desenhado para evoluir futuramente com **testes automatizados (ex: LambdaTest)**.

---

## 🧠 Visão Geral da Arquitetura

```
Developer → GitHub → Jenkins → Build/Test → SonarQube → Quality Gate
```

### Componentes

- Jenkins (CI)
- SonarQube Community (qualidade de código)
- PostgreSQL (banco do Sonar)
- Projeto Java simples (propositalmente vulnerável)
- Docker e Docker Compose

---

## 📁 Estrutura do Repositório

```
ci-cd-quality-lab/
├── infra/
    └── docker-compose.yaml # Infra local do Sonar (SonarQube + Postgres)
├── Jenkinsfile
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/example/demo/
                ├── DemoApplication.java
                ├── UserController.java
                └── UserService.java
```

---

## ☕ Sobre o Projeto Java

Este **não é um projeto de produção**.

Ele foi criado **intencionalmente com problemas de qualidade**, para que o SonarQube consiga identificar:

- Code Smells
- Bugs potenciais
- Vulnerabilidades
- Security Hotspots

Exemplos intencionais no código:

- Senha hardcoded
- Métodos grandes e confusos
- Uso de `System.out.println`
- Tratamento inadequado de exceções
- Possível `NullPointerException`
- Alto acoplamento

Isso permite estudar **Quality Gates na prática**, algo que projetos “Hello World perfeitos” não demonstram.

---

## 🧱 Pré-requisitos

- Ubuntu ou WSL2
- Docker
- Docker Compose
- Java 17
- Git

---

## 🐳 Instalação do SonarQube (Community Edition)

### 1️⃣ Subir SonarQube com Docker Compose

Crie o arquivo `docker-compose.yml`:

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_USER: sonar
      POSTGRES_PASSWORD: sonar
      POSTGRES_DB: sonar
    volumes:
      - sonar_db:/var/lib/postgresql/data
    networks:
      - sonar_net

  sonarqube:
    image: sonarqube:lts-community
    depends_on:
      - db
    ports:
      - "9000:9000"
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://db:5432/sonar
      SONAR_JDBC_USERNAME: sonar
      SONAR_JDBC_PASSWORD: sonar
    volumes:
      - sonar_data:/opt/sonarqube/data
      - sonar_extensions:/opt/sonarqube/extensions
      - sonar_logs:/opt/sonarqube/logs
    networks:
      - sonar_net

networks:
  sonar_net:
    driver: bridge

volumes:
  sonar_db:
  sonar_data:
  sonar_extensions:
  sonar_logs:
```

Subir os containers:

```bash
docker compose up -d
```

Acessar:

```
http://localhost:9000
```

Login padrão:

- usuário: `admin`
- senha: `admin`

---

## 🔓 Ajuste Obrigatório do Kernel (SonarQube)

O SonarQube **não funciona corretamente** sem esse ajuste no Linux/WSL.

```bash
sudo sysctl -w vm.max_map_count=262144
```

Para tornar permanente:

```bash
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

Sem isso, o SonarQube pode falhar ao iniciar ou reiniciar constantemente.

---

## ⚙️ Instalação do Jenkins no Ubuntu / WSL

### 1️⃣ Adicionar repositório oficial

```bash
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc   https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
```

```bash
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
```

```bash
sudo apt update
sudo apt install jenkins
```

### 2️⃣ Iniciar Jenkins

```bash
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

Acessar:

```
http://localhost:8080
```

---

## 🔐 Configuração Inicial do Jenkins

Obter senha inicial:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Depois:

- Instale os plugins sugeridos
- Crie o usuário administrador

---

## 🔌 Plugins Necessários no Jenkins

Instalar via **Manage Jenkins → Plugins**:

- SonarQube Scanner
- Pipeline
- Git
- Maven Integration
- JDK Tool

---

## 🔑 Integração Jenkins + SonarQube

### 1️⃣ Criar token no SonarQube

- User → My Account → Security → Generate Token

### 2️⃣ Configurar credencial no Jenkins

- Manage Jenkins → Credentials
- Tipo: Secret Text
- Cole o token do Sonar

### 3️⃣ Configurar Sonar Server

- Manage Jenkins → Configure System
- SonarQube Servers:
  - Name: `sonarqube`
  - URL: `http://localhost:9000`
  - Token: credencial criada

---

## 📜 Jenkinsfile (Pipeline)

```groovy
pipeline {
  agent any

  tools {
    maven 'maven'
    jdk 'jdk17'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn clean verify'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube') {
          sh 'mvn sonar:sonar'
        }
      }
    }

  }
}
```

---

## ✅ Verificação de Status dos Serviços

### 1️⃣ Jenkins (instalado via apt / systemd)

Verificar status do Jenkins:

```bash
sudo systemctl status jenkins
```

**Resultados esperados:**
- `active (running)` → Jenkins **ON** ✅
- `inactive` ou `failed` → Jenkins **OFF** ou com erro ❌

**Comandos úteis:**

```bash
sudo systemctl start jenkins
sudo systemctl stop jenkins
sudo systemctl restart jenkins
```

**Logs (quando der ruim):**

```bash
sudo journalctl -u jenkins -f
```

### 2️⃣ SonarQube (Docker Compose)

Como o Sonar está em container, **não usa systemctl**.

**Ver containers ativos:**

```bash
docker compose ps
```

Você deve ver algo como:

```
sonarqube   running
db          running
```

**Ver logs do Sonar:**

```bash
docker compose logs -f sonarqube
```

Procure por algo como:

```
SonarQube is up
```

⚠️ Se aparecer erro de memória ou bootstrap, ele não subiu corretamente.

### 3️⃣ Verificação por Porta (método DevOps clássico)

Isso ignora interface e olha direto a rede:

```bash
ss -lntp | grep -E '8080|9000'
```

Ou (alternativa):

```bash
netstat -lntp | grep -E '8080|9000'
```

**Você deve ver:**
- Java escutando na **8080** → Jenkins
- Java escutando na **9000** → SonarQube

Se a porta está aberta, o serviço está rodando. ✅

### 4️⃣ Verificação via Processo

Último nível de debugging:

```bash
ps aux | grep -E 'jenkins|sonar' | grep -v grep
```

---

## 📊 Resultados Esperados

Após rodar a pipeline:

- Projeto aparece no SonarQube
- Issues são criadas automaticamente
- Code Smells, Bugs e Vulnerabilidades visíveis
- Base sólida para Quality Gates

---

## 🚀 Próximos Passos (Roadmap)

- Configurar Quality Gate customizado
- Quebrar pipeline por falha de qualidade
- Corrigir código e validar melhoria
- Adicionar testes automatizados
- Integrar LambdaTest
- Criar badges de qualidade no README

---

## 🎯 Objetivo Educacional

Este laboratório foi criado para:

- Aprender CI/CD de forma prática
- Entender qualidade de código além do build
- Simular cenários reais de mercado
- Servir como referência futura

---

## 📌 Observação Final

Projetos pequenos e “perfeitos” não ensinam qualidade.  
Projetos imperfeitos, sim.

Este repositório existe exatamente para isso.
