
# FondoPensión – Backend (Spring Boot + MongoDB · Hexagonal)

Backend para gestionar suscripciones a fondos: **aperturas**, **cancelaciones**, **historial** y **notificaciones**.

Stack: **Java 17 · Spring Boot 3 · Spring Data MongoDB · Arquitectura Hexagonal (puertos/adaptadores)**.

---

## 📌 Requisitos de negocio cubiertos

| Requisito | Endpoint | Notas |
|---|---|---|
| (1) Suscribirse a un fondo | `POST /suscripciones/{fondoId}` | Genera transacción **APERTURA** con ID único y notificación (EMAIL/SMS). |
| (2) Salirse de un fondo | `DELETE /suscripciones/{fondoId}` | Genera transacción **CANCELACION** y retorna el monto de vinculación. |
| (3) Historial de transacciones | `GET /transacciones?limit=N` | Últimas N transacciones (aperturas/cancelaciones). |
| (4) Notificación | (en POST/DELETE) | Envío por canal seleccionado (EMAIL o SMS). |

**Reglas**: saldo inicial 500.000, monto mínimo por fondo, ID único de transacción, devolución al cancelar, mensaje de saldo insuficiente.

---

## 📁 Estructura del proyecto

```
fondopension/
├─ build.gradle
├─ gradle/ … (wrapper)
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/fondopension/fondopension/
│  │  │     ├─ application/port/         # Puertos in/out (casos de uso, repos, notificaciones)
│  │  │     ├─ domain/                    # Modelo de dominio (entidades, VOs, reglas, excepciones)
│  │  │     ├─ infrastructure/            # Adaptadores (REST, Mongo, notificaciones)
│  │  │     └─ FondopensionApplication.java
│  │  └─ resources/
│  │     ├─ application.properties        # ✅ Config principal usada
│  │     └─ application.yml               # ❌ Ignorar (puede contener datos sensibles)
│  └─ test/                               # Pruebas JUnit5 + Mockito
└─ README.md
```

> Si existe `infrastruture/`, renómbralo a **infrastructure/** para mantener el estándar.

---

## 🔒 Seguridad de configuración

Este proyecto **NO usa** `application.yml`. Añádelo a `.gitignore` y **no** lo empaquetes en el JAR:

```
# .gitignore
src/main/resources/application.yml
.env
*.env
```

Si se publicó por error, elimínalo del control de versiones y **rota** credenciales.

---

## ⚙️ Configuración en `application.properties`

```properties
spring.application.name=fondopension

# CORS (lee origens.urls desde ENV)
app.cors.allowed-origins=${origens.urls}

# MONGODB (arma la URI desde custom.db.*)
spring.data.mongodb.uri=mongodb+srv://${custom.db.username}:${custom.db.password}@${custom.db.host}/${custom.db.name}?retryWrites=true&w=majority&authSource=${custom.db.auth-db:admin}

# Índices automáticos (opcional)
spring.data.mongodb.auto-index-creation=${custom.db.auto-index-creation:true}

# Logs recomendados
logging.level.org.springframework.data.mongodb.core.MongoTemplate=INFO
logging.level.org.springframework.data.mongodb.core.MongoTemplate.query=OFF
logging.level.org.springframework.web=INFO
logging.level.com.fasterxml.jackson=INFO
logging.level.org.springframework.http.converter.json=INFO
logging.level.com.fasterxml.jackson.databind=INFO
```

> ⚠️ Si la contraseña contiene símbolos (`@`, `#`, `:`, `/`, etc.), debe estar **URL-encodeada** en `custom.db.password`.  
> Ej.: `#` → `%23`, `@` → `%40`, `:` → `%3A`, `/` → `%2F`.

---

## 🌱 Variables de entorno (lo que **debes** exportar)

Spring mapea automáticamente ENV → propiedades. Usa estos nombres:

| Propiedad | Variable de entorno |
|---|---|
| `origens.urls` | `ORIGENS_URLS` |
| `custom.db.host` | `CUSTOM_DB_HOST` |
| `custom.db.port` *(no usada en SRV)* | `CUSTOM_DB_PORT` |
| `custom.db.name` | `CUSTOM_DB_NAME` |
| `custom.db.username` | `CUSTOM_DB_USERNAME` |
| `custom.db.password` | `CUSTOM_DB_PASSWORD` |
| `custom.db.auth-db` | `CUSTOM_DB_AUTH_DB` |
| `custom.db.auto-index-creation` | `CUSTOM_DB_AUTO_INDEX_CREATION` |

### `.env.example`
```
# CORS
ORIGENS_URLS=*

# MongoDB (Atlas SRV)
CUSTOM_DB_HOST=cluster0.xn0yn5w.mongodb.net
CUSTOM_DB_NAME=fondopension
CUSTOM_DB_USERNAME=lukhack
CUSTOM_DB_PASSWORD=xxxxxxxx         # URL-encode si trae caracteres especiales
CUSTOM_DB_AUTH_DB=admin
CUSTOM_DB_AUTO_INDEX_CREATION=true
```

---

## ▶️ Ejecutar en local

### macOS/Linux (Bash)
```bash
export ORIGENS_URLS="*"
export CUSTOM_DB_HOST="cluster0.xn0yn5w.mongodb.net"
export CUSTOM_DB_NAME="fondopension"
export CUSTOM_DB_USERNAME="lukhack"
export CUSTOM_DB_PASSWORD="xxxxxxxx"   # URL-encode si aplica
export CUSTOM_DB_AUTH_DB="admin"
export CUSTOM_DB_AUTO_INDEX_CREATION="true"

./gradlew bootRun
```

### Windows (PowerShell)
```powershell
$Env:ORIGENS_URLS="*"
$Env:CUSTOM_DB_HOST="cluster0.xn0yn5w.mongodb.net"
$Env:CUSTOM_DB_NAME="fondopension"
$Env:CUSTOM_DB_USERNAME="lukhack"
$Env:CUSTOM_DB_PASSWORD="xxxxxxxx"
$Env:CUSTOM_DB_AUTH_DB="admin"
$Env:CUSTOM_DB_AUTO_INDEX_CREATION="true"

./gradlew bootRun
```

---

## 🧪 Compilar y probar

```bash
./gradlew clean build
./gradlew test
# Reporte de pruebas: build/reports/tests/test/index.html
```

---

## 🐳 Docker

**Dockerfile**
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR=build/libs/*.jar
COPY ${JAR} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
```

**Build & Run**
```bash
./gradlew clean bootJar
docker build -t fondopension-backend:1.0 .

docker run --rm -p 8080:8080   -e ORIGENS_URLS="*"   -e CUSTOM_DB_HOST="cluster0.xn0yn5w.mongodb.net"   -e CUSTOM_DB_NAME="fondopension"   -e CUSTOM_DB_USERNAME="lukhack"   -e CUSTOM_DB_PASSWORD="xxxxxxxx"   -e CUSTOM_DB_AUTH_DB="admin"   -e CUSTOM_DB_AUTO_INDEX_CREATION="true"   fondopension-backend:1.0
```

---

## ☁️ Despliegue en AWS — **CloudFormation / ECS Fargate**

Esta sección describe el **despliegue 100% IaC** con ALB + ECS Fargate usando **variables de entorno `custom.db.*`** (la app arma la URI) y **Secrets Manager** para la contraseña.

### 1) Prerrequisitos
- AWS CLI configurado.
- Permisos en ECR, ECS, EC2, IAM, CloudFormation, Secrets Manager, CloudWatch Logs.
- VPC y **dos subnets públicas** (simplificado para Atlas).

### 2) Construye y publica la imagen en **ECR**
```bash
AWS_REGION=us-east-1
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

./gradlew clean bootJar

# Dockerfile mínimo (si no lo tienes)
cat > Dockerfile << 'EOF'
FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR=build/libs/*.jar
COPY ${JAR} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
EOF

aws ecr create-repository --repository-name fondopension-backend || true

aws ecr get-login-password --region $AWS_REGION  | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker build -t fondopension-backend:1.0 .
docker tag fondopension-backend:1.0 ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/fondopension-backend:1.0
docker push ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/fondopension-backend:1.0
```

### 3) Crea un **Secret** para `CUSTOM_DB_PASSWORD`
```bash
aws secretsmanager create-secret   --name /fondopension/db/password   --secret-string 'TU_PASSWORD_URL_ENCODEADA'
```
Guarda el **ARN** (se usa como parámetro `DbPasswordSecretArn`).

### 4) Template CloudFormation (copia/pega como `infra/cloudformation/fondopension-ecs-fargate.yaml`)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Fondopension Backend - ECS Fargate + ALB'

Parameters:
  EcrImage:
    Type: String
    Description: URI de imagen ECR (ej. 123456789012.dkr.ecr.us-east-1.amazonaws.com/fondopension-backend:1.0)
  VpcId:
    Type: AWS::EC2::VPC::Id
    Description: VPC destino
  PublicSubnetIds:
    Type: List<AWS::EC2::Subnet::Id>
    Description: 2 subnets públicas para ALB/ECS (simplificado)
  DesiredCount:
    Type: Number
    Default: 1
  ContainerPort:
    Type: Number
    Default: 8080

  # CORS
  OrigensUrls:
    Type: String
    Default: "*"
    Description: Orígenes CORS (p.ej. *, http://localhost:4200,https://miapp.com)

  # Mongo granular (sin exponer la password)
  DbHost:
    Type: String
    Description: custom.db.host (Atlas SRV recomendado)
  DbName:
    Type: String
    Description: custom.db.name
  DbUsername:
    Type: String
    Description: custom.db.username
  DbAuthDb:
    Type: String
    Default: admin
    Description: custom.db.auth-db
  DbAutoIndexCreation:
    Type: String
    Default: 'true'
    AllowedValues: ['true','false']
    Description: custom.db.auto-index-creation
  DbPasswordSecretArn:
    Type: String
    Description: ARN del Secret con CUSTOM_DB_PASSWORD

Resources:
  LogGroup:
    Type: AWS::Logs::LogGroup
    Properties:
      LogGroupName: /ecs/fondopension-backend
      RetentionInDays: 14

  AlbSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Allow HTTP
      VpcId: !Ref VpcId
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0
      SecurityGroupEgress:
        - IpProtocol: -1
          CidrIp: 0.0.0.0/0

  ServiceSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: ALB -> ECS tasks
      VpcId: !Ref VpcId
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: !Ref ContainerPort
          ToPort: !Ref ContainerPort
          SourceSecurityGroupId: !Ref AlbSecurityGroup
      SecurityGroupEgress:
        - IpProtocol: -1
          CidrIp: 0.0.0.0/0

  LoadBalancer:
    Type: AWS::ElasticLoadBalancingV2::LoadBalancer
    Properties:
      Scheme: internet-facing
      Type: application
      Subnets: !Ref PublicSubnetIds
      SecurityGroups: [ !Ref AlbSecurityGroup ]

  TargetGroup:
    Type: AWS::ElasticLoadBalancingV2::TargetGroup
    Properties:
      TargetType: ip
      Protocol: HTTP
      Port: !Ref ContainerPort
      VpcId: !Ref VpcId
      HealthCheckEnabled: true
      HealthCheckPath: /cuenta
      HealthCheckProtocol: HTTP
      Matcher:
        HttpCode: '200-399'

  ListenerHttp:
    Type: AWS::ElasticLoadBalancingV2::Listener
    Properties:
      LoadBalancerArn: !Ref LoadBalancer
      Port: 80
      Protocol: HTTP
      DefaultActions:
        - Type: forward
          TargetGroupArn: !Ref TargetGroup

  Cluster:
    Type: AWS::ECS::Cluster
    Properties:
      ClusterName: fondopension-cluster

  TaskExecutionRole:
    Type: AWS::IAM::Role
    Properties:
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              Service: ecs-tasks.amazonaws.com
            Action: sts:AssumeRole
      ManagedPolicyArns:
        - arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
      Policies:
        - PolicyName: SecretsGet
          PolicyDocument:
            Version: '2012-10-17'
            Statement:
              - Effect: Allow
                Action: secretsmanager:GetSecretValue
                Resource: !Ref DbPasswordSecretArn

  TaskRole:
    Type: AWS::IAM::Role
    Properties:
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              Service: ecs-tasks.amazonaws.com
            Action: sts:AssumeRole

  TaskDefinition:
    Type: AWS::ECS::TaskDefinition
    Properties:
      Family: fondopension-backend
      Cpu: '512'
      Memory: '1024'
      NetworkMode: awsvpc
      RequiresCompatibilities: [ FARGATE ]
      ExecutionRoleArn: !Ref TaskExecutionRole
      TaskRoleArn: !Ref TaskRole
      ContainerDefinitions:
        - Name: fondopension
          Image: !Ref EcrImage
          PortMappings:
            - ContainerPort: !Ref ContainerPort
              Protocol: tcp
          Environment:
            - Name: ORIGENS_URLS
              Value: !Ref OrigensUrls
            - Name: CUSTOM_DB_HOST
              Value: !Ref DbHost
            - Name: CUSTOM_DB_NAME
              Value: !Ref DbName
            - Name: CUSTOM_DB_USERNAME
              Value: !Ref DbUsername
            - Name: CUSTOM_DB_AUTH_DB
              Value: !Ref DbAuthDb
            - Name: CUSTOM_DB_AUTO_INDEX_CREATION
              Value: !Ref DbAutoIndexCreation
          Secrets:
            - Name: CUSTOM_DB_PASSWORD
              ValueFrom: !Ref DbPasswordSecretArn
          LogConfiguration:
            LogDriver: awslogs
            Options:
              awslogs-group: !Ref LogGroup
              awslogs-region: !Ref AWS::Region
              awslogs-stream-prefix: ecs

  Service:
    Type: AWS::ECS::Service
    DependsOn: ListenerHttp
    Properties:
      Cluster: !Ref Cluster
      DesiredCount: !Ref DesiredCount
      LaunchType: FARGATE
      HealthCheckGracePeriodSeconds: 60
      NetworkConfiguration:
        AwsvpcConfiguration:
          AssignPublicIp: ENABLED       # Para Atlas SRV (salida a Internet)
          Subnets: !Ref PublicSubnetIds
          SecurityGroups: [ !Ref ServiceSecurityGroup ]
      LoadBalancers:
        - TargetGroupArn: !Ref TargetGroup
          ContainerName: fondopension
          ContainerPort: !Ref ContainerPort

Outputs:
  LoadBalancerDNSName:
    Description: DNS del ALB
    Value: !GetAtt LoadBalancer.DNSName
  ServiceURL:
    Description: URL HTTP
    Value: !Sub "http://${LoadBalancerDNSName}"
```

### 5) Desplegar el stack
```bash
STACK=fondopension-backend
TEMPLATE=infra/cloudformation/fondopension-ecs-fargate.yaml

# Descubre IDs si hace falta
aws ec2 describe-vpcs --query 'Vpcs[].{VpcId:VpcId,IsDefault:IsDefault}'
aws ec2 describe-subnets --filters "Name=default-for-az,Values=true"   --query 'Subnets[].{SubnetId:SubnetId,Az:AvailabilityZone}'

# Lanza el stack
aws cloudformation deploy   --template-file $TEMPLATE   --stack-name $STACK   --capabilities CAPABILITY_NAMED_IAM   --parameter-overrides     EcrImage=${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/fondopension-backend:1.0     VpcId=vpc-xxxxxxxx     PublicSubnetIds=subnet-aaa,subnet-bbb     OrigensUrls="*"     DbHost=cluster0.xn0yn5w.mongodb.net     DbName=fondopension     DbUsername=lukhack     DbAuthDb=admin     DbAutoIndexCreation=true     DbPasswordSecretArn=arn:aws:secretsmanager:...:secret:/fondopension/db/password-xxxx     DesiredCount=1
```

### 6) Outputs y verificación
```bash
aws cloudformation describe-stacks --stack-name $STACK   --query "Stacks[0].Outputs"

BASE=http://<ALB-DNS>
curl -s $BASE/cuenta | jq .
curl -s $BASE/fondos | jq .
```

---

## 🔌 Endpoints útiles (smoke)

```bash
# Apertura
curl -s -X POST $BASE/suscripciones/3   -H "Content-Type: application/json"   -d '{"channel":"EMAIL","destination":"usuario@example.com"}' | jq .

# Historial
curl -s $BASE/transacciones?limit=5 | jq .

# Cancelación
curl -s -X DELETE $BASE/suscripciones/3   -H "Content-Type: application/json"   -d '{"channel":"SMS","destination":"3001234567"}' | jq .
```

---

## 🧰 Troubleshooting

- **UNHEALTHY en TargetGroup** → verifica `/cuenta` 200 OK; aumenta el `HealthCheckGracePeriodSeconds`.
- **Mongo auth** → revisa `CUSTOM_DB_*`; en especial `CUSTOM_DB_PASSWORD` (URL-encode) y `CUSTOM_DB_AUTH_DB`.
- **CORS** → `ORIGENS_URLS="*"` para pruebas; en producción usa dominios explícitos si hay credenciales.
- **Atlas** → con `AssignPublicIp: ENABLED` las tareas salen a Internet; en VPC privada usa NAT.

---
