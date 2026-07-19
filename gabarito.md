

<div align="center">
  <h1>Gabarito do Tutorial Prático: Refatoração e Testes Unitários</h1>
  <p style="font-size: 12px"><strong>Resoluções das Atividades Teóricas e Práticas</strong></p>
  <br>
  <p>
    <b>Disciplina:</b> Engenharia de Software &nbsp;&bull;&nbsp; 
    <b>Autor:</b> Hálisson Piovezana
  </p>
</div>

<br><br>

## Visão Geral
Este documento contém as respostas esperadas para as perguntas teóricas e a implementação prática dos trechos de código refatorados, correspondentes às atividades propostas no `README.md` principal.

---

## 1. Atividade 1: Lidando com Parâmetros Excessivos (Projeto Eclipse Kura)

### 1.1 Respostas Teóricas Iniciais

**1. Qual é o nome formal desse Code Smell segundo o livro-texto de Engenharia de Software Moderna?**  
**Resposta:** Lista de Parâmetros Longa (*Long Parameter List*).

**2. O que aconteceria em tempo de compilação e de execução se um desenvolvedor passasse o valor de priority no lugar de qos ao chamar o método?**  
**Resposta:** 
- **Tempo de compilação:** O código compilaria perfeitamente, sem erros, pois tanto `priority` quanto `qos` são do tipo primitivo `int`.
- **Tempo de execução:** Ocorreria um comportamento anômalo silencioso (*bug*), fazendo com que o envio da telemetria assumisse prioridade ou Qualidade de Serviço (*QoS*) indevida, podendo impactar a infraestrutura de rede e perda de dados sem apontar erros explícitos.

**3. Qual técnica de refatoração você utilizaria para resolver isso sem quebrar a coesão?**  
**Resposta:** Introdução de Objeto de Parâmetro (*Introduce Parameter Object*), encapsulando os diversos parâmetros em uma estrutura/classe específica de configurações.

**4. Com qual objetivo os testes de unidade executam a pequena unidade de código criada e verificada por eles?**  
**Resposta:** Para assegurar que as funções de menor nível operam de forma correta e isolada, verificando o comportamento daquele pequeno bloco ou classe contra diferentes valores de entrada e cenários esperados.

**5. Por que adotamos a dinâmica fundamental de rodar os testes de unidade antes e depois da aplicação das alterações propostas de refatoração estrutural?**  
**Resposta:** Para garantir que o comportamento inicial e esperado do programa foi mantido. Rodar antes define a "linha de base" (que o sistema funcionava), e rodar depois certifica de que as mudanças em sua arquitetura ou design não introduziram quebras (preservação de comportamento).

---

### 1.2 Respostas da Validação Final

**1. Quais foram os principais benefícios alcançados com essa abordagem refatorada?**  
**Resposta:** Aumento da legibilidade da assinatura do método, prevenção de erros (impossibilidade de trocar acidentalmente valores como `qos` e `priority`), alta coesão e maior facilidade para manutenções futuras.

**2. Por que o payload, sendo um dado bruto, não faz parte dos metadados de configuração?**  
**Resposta:** Por conta da Separação de Responsabilidades. O *payload* é a carga útil, ou seja, a própria mensagem ou dado que precisa ser enviado. As outras variáveis tratam das configurações de rede aplicadas à mensagem, fazendo sentido o seu encapsulamento em um objeto `PublishConfig` em separado do dado transportado.

**3. Se o protocolo MQTT for atualizado no futuro e exigir um novo parâmetro (como timeToLive), qual arquivo precisará ser alterado e qual será o impacto nas classes que já utilizam o método publish?**  
**Resposta:** Apenas a nova classe `PublishConfig` precisará ser modificada (adicionando a propriedade, atualizando o construtor e getter). A assinatura do método `publish` e as implementações da interface que apenas passam ou invocam o método continuarão inalteradas, mitigando impactos em cascata no código.

---

### 1.3 Prática de Código: Atividade 1

**1. Criação do Objeto de Configuração (`PublishConfig.java`):**
```java
public class PublishConfig {
    private final String deviceId;
    private final String appTopic;
    private final int qos;
    private final boolean retain;
    private final int priority;

    public PublishConfig(String deviceId, String appTopic, int qos, boolean retain, int priority) {
        this.deviceId = deviceId;
        this.appTopic = appTopic;
        this.qos = qos;
        this.retain = retain;
        this.priority = priority;
    }

    public String getDeviceId() { return deviceId; }
    public String getAppTopic() { return appTopic; }
    public int getQos() { return qos; }
    public boolean isRetain() { return retain; }
    public int getPriority() { return priority; }
}
```

**2. Atualização da Interface (`CloudClient.java`):**
```java
public interface CloudClient {
    public int publish(byte[] payload, PublishConfig config) throws KuraException;
}
```

**3. Atualização da Classe de Teste (`CloudClientTest.java`):**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CloudClientTest {

    @Test
    public void testPublishSuccess() throws Exception {
        // Preparação
        CloudClient client = new CloudClientImpl(); 
        byte[] payload = new byte[]{1, 2, 3};
        PublishConfig config = new PublishConfig("device-001", "sensors/temp", 1, true, 2);

        // Execução
        int statusCode = client.publish(payload, config);

        // Verificação
        assertEquals(200, statusCode, "O status de publicação deve ser 200 em caso de sucesso");
    }
}
```

---

## 2. Atividade 2: Desestruturando a "Cadeia de Comandos" (Projeto ThingsBoard)

### 2.1 Respostas Teóricas Iniciais

**1. Analisando as estruturas de decisão que crescem descontroladamente, quais são os dois principais Code Smells que podemos classificar neste método, segundo a literatura de refatoração?**  
**Resposta:** Método Longo (*Long Method*) e Comandos/Condicionais Complexos (*Complex Conditionals/Switch Statements*). Adicionalmente, também ocorre uma Baixa Coesão e Quebra da Responsabilidade Única.

**2. O método acima fere qual princípio fundamental da Engenharia de Software (SOLID)? Justifique.**  
**Resposta:** Ele fere principalmente o **Princípio da Responsabilidade Única (SRP)**, pois concentra funções de roteamento dos tópicos, conversões dos payloads de tipos variados e despachos para o serviço; e também fere o **Princípio do Aberto/Fechado (OCP)**, visto que, a cada novo tópico necessário para o sistema, esse mesmo método precisaria ser modificado com novas ramificações.

**3. Identifique o padrão que se repete dentro de cada bloco condicional. Quais são as duas etapas que sempre acontecem independente do tópico?**  
**Resposta:** 
- 1ª Etapa: Conversão de formato do payload pelo *Adaptor* (ex: `payloadAdaptor.convertToPostAttributes(...)`).
- 2ª Etapa: A chamada de roteamento final e persistência enviando o conteúdo convertido (ex: `transportService.process(...)`).

**4. Códigos extensos que integram dependências para bancos de dados e sistemas externos (como os observados no contexto acima) tornam os testes locais difíceis. Conforme a literatura da Engenharia de Software Moderna (Capítulo 8), quais tipos de objetos nós precisaríamos para viabilizar e isolar testes dessas dependências complexas?**  
**Resposta:** Os objetos dublês de testes (*Test Doubles*), mais comumente **Mocks** ou **Stubs**, capazes de simular injeções de serviços de banco de dados e APIs externas sem executar o processo verdadeiro em ambiente de teste local.

---

### 2.2 Respostas da Validação Final

**1. Quais são os principais benefícios alcançados ao isolar cada operação em seu próprio método privado no que diz respeito a teste?**  
**Resposta:** Testabilidade ampliada através do isolamento. Permite validar e encontrar com precisão bugs específicos atrelados unicamente ao processamento de determinado tipo de operação, sem se envolver no emaranhado da grande cadeia decisória de forma intrínseca.

**2. O que acontece com a legibilidade da estrutura if / else if quando os detalhes de implementação são escondidos atrás de funções bem nomeadas?**  
**Resposta:** Ela se transforma em uma leitura clara e declarativa (auto-documentada), onde o código diz *o que* está fazendo de maneira fluída (ex: "se é tópico de telemetria, trate como telemetria") abstendo o desenvolvedor ou o leitor sobre *como* está fazendo (escondido na função específica).

---

### 2.3 Prática de Código: Atividade 2

**1. Criação e Execução do Teste Original (`MqttTransportHandlerTest.java`):**
Para viabilizar o teste localmente e isolar o ambiente, utilizamos a versão simplificada do código juntamente com um Mock:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MqttTransportHandlerTest {

    @Test
    public void testProcessDevicePublishAttributesFlow() {
        TelemetryServiceMock telemetryServiceMock = new TelemetryServiceMock();
        MqttTransportHandler handler = new MqttTransportHandler(telemetryServiceMock);
        String topicName = "v1/devices/me/attributes"; 
        int msgId = 1;
        
        handler.processDevicePublish(null, null, topicName, msgId);
        
        assertTrue(telemetryServiceMock.wasAttributesCalled(), "O fluxo de atributos deveria ter sido chamado");
        assertFalse(telemetryServiceMock.wasCalled(), "O fluxo de telemetria nao deveria ser chamado para tópico de atributos");
        assertFalse(telemetryServiceMock.wasRpcCalled(), "O fluxo RPC nao deveria ser chamado para tópico de atributos");
    }

    @Test
    public void testProcessDevicePublishTelemetryFlow() {
        TelemetryServiceMock telemetryServiceMock = new TelemetryServiceMock();
        MqttTransportHandler handler = new MqttTransportHandler(telemetryServiceMock);
        String topicName = "v1/devices/me/telemetry"; 
        int msgId = 2;
        
        handler.processDevicePublish(null, null, topicName, msgId);
        
        assertTrue(telemetryServiceMock.wasCalled(), "O fluxo de telemetria deveria ter sido chamado");
        assertFalse(telemetryServiceMock.wasAttributesCalled(), "O fluxo de atributos nao deveria ser chamado para tópico de telemetria");
        assertFalse(telemetryServiceMock.wasRpcCalled(), "O fluxo RPC nao deveria ser chamado para tópico de telemetria");
    }

    @Test
    public void testProcessDevicePublishRpcFlow() {
        TelemetryServiceMock telemetryServiceMock = new TelemetryServiceMock();
        MqttTransportHandler handler = new MqttTransportHandler(telemetryServiceMock);
        String topicName = "v1/devices/me/rpc/request/123"; 
        int msgId = 3;
        
        handler.processDevicePublish(null, null, topicName, msgId);
        
        assertTrue(telemetryServiceMock.wasRpcCalled(), "O fluxo de RPC deveria ter sido chamado");
        assertFalse(telemetryServiceMock.wasAttributesCalled(), "O fluxo de atributos nao deveria ser chamado para tópico RPC");
        assertFalse(telemetryServiceMock.wasCalled(), "O fluxo de telemetria nao deveria ser chamado para tópico RPC");
    }
}
```

**2. Refatoração do Arquivo `MqttTransportHandler.java`:**
> **OBS:** O `README.md` exibe conceitualmente o trecho denso e complexo do repositório do ThingsBoard original. Para compilar e testar localmente na sua máquina (onde as bibliotecas do ThingsBoard não estão presentes), utilizamos uma versão **simplificada** desse arquivo.

**A) Prática (Código Simplificado para Teste Local):**
Esta é a versão que deve constar no seu arquivo real `src/exercises/activity2/MqttTransportHandler.java` contemplando a refatoração dos blocos de Atributos, Telemetria e RPC para que o teste passe com sucesso:

```java
public class MqttTransportHandler {
    private final TelemetryService telemetryService;

    public MqttTransportHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    public void processDevicePublish(Object ctx, Object mqttMsg, String topicName, int msgId) {
        if ("v1/devices/me/attributes".equals(topicName)) {
            handleDeviceAttributes(topicName, msgId);
        } else if ("v1/devices/me/telemetry".equals(topicName)) {
            handleDeviceTelemetry(topicName, msgId);
        } else if (topicName.startsWith("v1/devices/me/rpc")) {
            handleDeviceRpc(topicName, msgId);
        }
    }

    private void handleDeviceAttributes(String topicName, int msgId) {
        telemetryService.processAttributes(topicName, msgId);
    }

    private void handleDeviceTelemetry(String topicName, int msgId) {
        telemetryService.recordTelemetry(topicName, msgId);
    }

    private void handleDeviceRpc(String topicName, int msgId) {
        telemetryService.processRpc(topicName, msgId);
    }
}
```
*Nota:* você poderia facilmente continuar repetindo essa mesma estrutura e padrão de separação para as demais funções (como Device Claim) caso fossem integradas ao serviço mockado.

**B) Resposta Teórica (Contexto Original/Completo):**
A título de teoria, no contexto do código-base real e completo referenciado no README, a aplicação da Extração de Método resultaria na seguinte estrutura limpa:

```java
void processDevicePublish(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    try {
        if (deviceSessionCtx.isDeviceAttributesTopic(topicName)) {
            handleDeviceAttributes(ctx, mqttMsg, topicName, msgId);
        } else if (deviceSessionCtx.isDeviceTelemetryTopic(topicName)) {
            handleDeviceTelemetry(ctx, mqttMsg, topicName, msgId);
        } else if (topicName.startsWith(MqttTopics.DEVICE_RPC_RESPONSE_TOPIC)) {
            handleDeviceRpcResponse(ctx, mqttMsg, topicName, msgId);
        } else if (topicName.equals(MqttTopics.DEVICE_CLAIM_TOPIC)) {
            handleDeviceClaim(ctx, mqttMsg, topicName, msgId);
        } 
        // ... os demais elses seguiriam a mesma lógica delegada
    } catch (Exception e) {
        log.warn("[{}] Failed to process publish msg [{}][{}]", sessionId, topicName, msgId, e);
        ctx.close();
    }
}

private void handleDeviceAttributes(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    MqttTransportAdaptor payloadAdaptor = deviceSessionCtx.getPayloadAdaptor();
    TransportProtos.PostAttributeMsg postAttributeMsg = payloadAdaptor.convertToPostAttributes(deviceSessionCtx, mqttMsg);
    transportService.process(deviceSessionCtx.getSessionInfo(), postAttributeMsg, getMetadata(deviceSessionCtx, topicName), getPubAckCallback(ctx, msgId, postAttributeMsg));
}

private void handleDeviceTelemetry(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    MqttTransportAdaptor payloadAdaptor = deviceSessionCtx.getPayloadAdaptor();
    TransportProtos.PostTelemetryMsg postTelemetryMsg = payloadAdaptor.convertToPostTelemetry(deviceSessionCtx, mqttMsg);
    transportService.process(deviceSessionCtx.getSessionInfo(), postTelemetryMsg, getMetadata(deviceSessionCtx, topicName), getPubAckCallback(ctx, msgId, postTelemetryMsg));
}

private void handleDeviceRpcResponse(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    MqttTransportAdaptor payloadAdaptor = deviceSessionCtx.getPayloadAdaptor();
    TransportProtos.ToDeviceRpcResponseMsg rpcResponseMsg = payloadAdaptor.convertToDeviceRpcResponse(deviceSessionCtx, mqttMsg, MqttTopics.DEVICE_RPC_RESPONSE_TOPIC);
    transportService.process(deviceSessionCtx.getSessionInfo(), rpcResponseMsg, getPubAckCallback(ctx, msgId, rpcResponseMsg));
}

private void handleDeviceClaim(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    MqttTransportAdaptor payloadAdaptor = deviceSessionCtx.getPayloadAdaptor();
    TransportProtos.ClaimDeviceMsg claimDeviceMsg = payloadAdaptor.convertToClaimDevice(deviceSessionCtx, mqttMsg);
    transportService.process(deviceSessionCtx.getSessionInfo(), claimDeviceMsg, getPubAckCallback(ctx, msgId, claimDeviceMsg));
}
```
