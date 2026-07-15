
<table align="center" border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse; max-width: 100%;">
  <tr>
    <td align="right" style="padding-right: 30px; width: 50%;">
      <img src="./img/ufop-logo.jpg" height="250" style="object-fit: contain;" alt="Logo UFOP">
    </td>
      <td align="left" style="padding-left: 30px; width: 50%;">
        <img src="./img/logo-lecomp.png" width="300" style="object-fit: contain;" alt="Logo LECOMP">
    </td>
  </tr>

  <tr>
    <td colspan="2" align="center" style="padding-top: 25px;">
      <p style="font-size: 16px;"><sub><i>Programa de Monitoria 2026.1</i></sub></p>
    </td>
  </tr>
</table>

<br><br>

<div align="center">
  <h1>Tutorial Prático: Refatoração</h1>
  <p style="font-size: 12px"><strong>Linguagem Java</strong></p>
  <br>
  <p>
    <b>Disciplina:</b> Engenharia de Software &nbsp;&bull;&nbsp; 
    <b>Autor:</b> Hálisson Piovezana
  </p>
</div>

<br><br>

## 1. Objetivo
O objetivo deste guia prático é ensinar os alunos a corrigir code smells comuns encontrados em sistemas IoT reais escritos em Java, migrando da etapa de identificação passiva para a aplicação ativa de transformações de design de software. Os alunos irão transformar códigos acoplados, longos e de difícil manutenção em componentes modulares, legíveis e com papéis bem definidos (coesão).

---

## 2. Preparação do Ambiente
Descrição objetiva do que é necessário e o passo a passo para realizar o tutorial na sua máquina.

### 2.1 Ferramentas Necessárias/Recomendadas
* **IDE (Integrated Development Environment):** Eclipse, IntelliJ IDEA ou NetBeans. Ou editores de código: VSCode, Sublime Text;
* **Java Development Kit (JDK):** Versão 11 ou superior + Extension Pack for Java (VScode);
* **Git:** Ferramenta recomendada para realizar commits periódicos durante cada passo da refatoração.

### 2.2 Importar
Para começar a prática, você precisa trazer o código-fonte para a sua máquina local e abri-lo em uma IDE ou editor de código.

**Recomendado**: Editor **VS Code** + Extensão **Extension Pack for Java** + **JDK** (obrigatório)

1. Abra o **VS Code**.
2. Clique com o botão direito no workspace ou vá no menu direito superior em `File > Add Folder to Workspace...` e selecione uma pasta onde deseja salvar o repositório do tutorial prático e clique em `Add`
3. Inicie o terminal integrado do editor no caminho da pasta selecionada (clique com o botão direito em cima da pasta e vá em `Open in Integrated Terminal` ou vá no menu superior em `Terminal > New Terminal` e selecione a pasta)
3. Com o terminal aberto no diretório da pasta, faça o clone do repositório base utilizando o Git:
   ```bash
   git clone <https://github.com/HalissonPiov/monitoria-engenharia-de-software.git>
   ```

### 2.3 Compilar
Como estamos utilizando o **Extension Pack for Java**, o VS Code assume o trabalho pesado de compilação para você de forma quase transparente, ideal para focar apenas na refatoração.

1. Ao abrir qualquer arquivo `.java`, a extensão será ativada automaticamente (você verá o ícone do Java carregando na barra inferior).
2. O VS Code compila o código em segundo plano sempre que você salva o arquivo (`Ctrl + S`). Se a sua refatoração gerar algum erro de sintaxe, ele será imediatamente destacado com uma linha vermelha.
3. **(Opcional)** Caso deseje compilar manualmente via terminal (por exemplo, utilizando o terminal nativo do Linux/Ubuntu ou via WSL) para isolar a verificação de erros em uma classe específica:
   ```bash
   javac NomeDaClasse.java
   ```

### 2.4 Executar
Para garantir que o princípio fundamental da refatoração foi mantido (ou seja, o comportamento externo do software não foi alterado), você precisará rodar o código antes e depois das suas modificações.

1. Navegue pelo painel lateral (Explorer) e abra o arquivo Java principal que contém o método `public static void main(String[] args)`.
2. A extensão do Java adiciona automaticamente pequenos atalhos chamados **Run** e **Debug** logo acima da declaração do método `main`.
3. Clique em **Run**.
4. A execução será iniciada e você poderá acompanhar todos os logs, *prints* e resultados na aba `Terminal` na parte inferior da tela.
5. **(Opcional)** Para executar diretamente pelo terminal integrado após compilar:
   ```bash
   java NomeDaClasse
   ```

---

## 3. Introdução: O que é Refatoração?

Conforme estabelecido na literatura da Engenharia de Software Moderna (Capítulo 9), refatoração consiste em modificações realizadas na estrutura interna de um ecossistema de software com o objetivo de melhorar sua arquitetura, legibilidade e manutenibilidade, sem alterar o seu comportamento. Refatorar não significa corrigir bugs ou adicionar novas funcionalidades, mas sim otimizar um código que já funciona para torná-lo mais limpo e resiliente a futuras expansões, seguindo as boas práticas.

### Pontos Positivos e Importância nos Projetos:
* **Combate à Segunda Lei de Lehman (Complexidade Crescente):** Esta lei afirma que à medida que um programa evolui, sua complexidade interna aumenta, a menos que se dedique um trabalho ativo para reduzi-la. A refatoração é a ferramenta primária para estabilizar esse declínio, contendo o endividamento técnico.
* **Melhoria do Desenvolvimento Futuro:** Um código limpo e livre de deformidades arquiteturais (code smells) reduz drasticamente e comprovadamente o tempo necessário para depuração e implementação de novas features pelo time.
* **Foco em Coesão e Acoplamento:** O processo elimina redundâncias, aumenta a coesão das classes (Responsabilidade Única) e diminui o acoplamento prejudicial entre módulos.

---

## 4. Visão Geral dos Projetos e Arquivos Coletados
Para garantir a imersão em cenários de desenvolvimento de sistemas de produção reais, este tutorial adota trechos arquiteturais e conceituais extraídos de dois grandes ecossistemas de software livre voltados para a Internet das Coisas (IoT), em linguagem Java:

### Projeto 1: [Eclipse Kura](https://github.com/eclipse-kura/kura)
* **O que é o projeto:** É um framework baseado em Java e OSGi voltado para a construção de gateways de IoT no ecossistema M2M (Machine-to-Machine). Ele atua na camada de edge computing, servindo de ponte inteligente entre os sensores físicos locais e os servidores de computação em nuvem.
* **Arquivo Selecionado:** [`CloudClient.java`](https://github.com/eclipse-kura/kura/blob/develop/kura/org.eclipse.kura.api/src/main/java/org/eclipse/kura/cloud/CloudClient.java)
* **Contexto:** Este componente gerencia os canais de dados ativos entre o gateway físico e as aplicações cloud. Ele lida diretamente com conexões de telemetria baseadas em protocolos de rede restritos e foi selecionado devido à sua natureza crítica de passagem de parâmetros de conectividade.

### Projeto 2: [ThingsBoard](https://github.com/thingsboard/thingsboard)
* **O que é o projeto:** Uma plataforma IoT em nuvem altamente escalável de código aberto usada para coleta, processamento, visualização de telemetria de sensores e gerenciamento remoto de frotas de dispositivos em larga escala.
* **Arquivo Selecionado:** [`MqttTransportHandler.java`](https://github.com/thingsboard/thingsboard/blob/master/common/transport/mqtt/src/main/java/org/thingsboard/server/transport/mqtt/MqttTransportHandler.java)
* **Contexto:** Localizado no núcleo de rede da camada de transporte, este arquivo é responsável por interceptar e decodificar fluxos brutos de pacotes MQTT recebidos de milhares de sensores espalhados geograficamente. Ele foi selecionado por conter algoritmos densos de tratamento de estados e decisões estruturadas em cascata.

---

## 5. Exercício Prático Proposto
Com base nas características descritas do software e práticas de identificação de code smells, você deverá propor possíveis refatorações válidas para os componentes dos projetos abordados.

### ➡️ Atividade 1: Lidando com Parâmetros Excessivos (Projeto Eclipse Kura)
**Contexto:** Na interface responsável por realizar o envio de telemetria de um dispositivo para o broker na nuvem, o método de publicação acabou acumulando diversas variáveis primitivas de configuração ao longo do tempo.


**Código Base:**

```java
public interface CloudClient {
    public int publish(String deviceId, String appTopic, byte[] payload, int qos, boolean retain, int priority) throws KuraException;
}
```
<p style="font-size: 16px;"><sub><i>Linha 326 do arquivo CloudClient.java</i></sub></p>

**Problemas:**
* **Falta de Legibilidade:** Lendo a chamada do método, é impossível saber o que 1, true e 2 significam sem olhar a documentação.
* **Propensão a Erros (Bugs Silenciosos):** Como qos e priority são do tipo int, se o programador acidentalmente inverter a ordem na hora de chamar o método (passar a prioridade no lugar do QoS), o código vai compilar sem erros, mas o dispositivo IoT vai se comportar de forma errada na rede.
* **Rigidez:** Se amanhã o protocolo de rede for atualizado e precisarmos passar um novo parâmetro, como um timeToLive (tempo de expiração), teremos que alterar a assinatura da interface, quebrando todas as classes que já implementavam esse método.


**⚠️ Antes de alterar o código, responda brevemente no [formulário correspondente](https://docs.google.com/forms/d/1-wFwycXEUJrAVtQzR05qPgPee4qJppD0NrG-pXYv1yY/edit):**
1. Qual é o nome formal desse Code Smell?
2. O que aconteceria em tempo de compilação e de execução se um desenvolvedor passasse o valor de priority no lugar de qos ao chamar o método?
3. Qual técnica de refatoração você utilizaria para resolver isso sem quebrar a coesão, agrupando os parâmetros adequadamente?

---

### 5.1. Passo a Passo

**Etapa 1: Criando a estrutura de encapsulamento**

Em vez de passar parâmetros soltos, vamos criar uma estrutura de dados dedicada para as configurações de rede.
- Crie um novo arquivo/classe chamado PublishConfig.
- Identifique e mova os parâmetros que correspondem aos metadados da mensagem (ou seja, deviceId, appTopic, qos, retain, priority) para dentro desta classe.
- Crie formas básicas de acesso e inicialização à classe, seguindo boas práticas

**📍️ Checkpoint 1: Verifique a sua classe `PublishConfig` (Autovalidação)**

Verifique se o seu código seguiu as melhores práticas para sistemas distribuídos (IoT) e correção do Code Smell identificado.
* Para garantir a resiliência em sistemas distribuídos/IoT, os atributos foram declarados com modificadores de acesso fechados (private) e com a palavra-chave que garante imutabilidade (final)?
* Você criou um construtor que inicializa todos esses campos?
* Você gerou os métodos Getters adequados? **RECOMENDADO**: em objetos de configuração rigorosos, não geramos Setters (objetos de configuração não devem ser alterados após instanciados)
```java
public class PublishConfig {
    // Atributos privados e imutáveis (final)
    private final String deviceId;
    // ... declare os outros atributos restantes

    // Construtor inicializando todos os campos
    public PublishConfig(String deviceId, /* outros parâmetros... */) {
        this.deviceId = deviceId;
        // ...
    }

    // Apenas Getters gerados (sem Setters!)
    public String getDeviceId() { return deviceId; }
    // ...
}
```

**Etapa 2: Atualizando o Contrato**

Volte ao arquivo CloudClient.java.
- Substitua a longa lista de parâmetros primitivos pelo seu novo objeto agrupador.
- Mantenha o payload solto, pois ele é o dado bruto e não faz parte dos metadados de configuração.

**📍️ Checkpoint 2: Refatoração Concluída**

Salve os arquivos. A assinatura da sua interface agora deve estar limpa, contendo apenas o conteúdo e a classe de configuração, mantendo o restante do código semelhante a isso:
```java
public interface CloudClient {
    public int publish(byte[] payload, PublishConfig config) throws KuraException;
}
```

Validação Final: Acesse o formulário e responda:
1. Quais foram os principais benefícios alcançados com essa abordagem refatorada?
2. Por que o payload, sendo um dado bruto, não faz parte dos metadados de configuração?
3. Se o protocolo MQTT for atualizado no futuro e exigir um novo parâmetro (como timeToLive), qual arquivo precisará ser alterado e qual será o impacto nas classes que já utilizam o método publish?

**💡️ Compare o resultado final do seu código com o resultado esperado no arquivo `gabarito.md`**

---

### ➡️ Atividade 2: Desestruturando a "Cadeia de Comandos" (Projeto ThingsBoard)
**Contexto:** Ao processar uma mensagem publicada pelo dispositivo, o método `processDevicePublish` roteia a ação com base no tópico da mensagem. Com a evolução do projeto, o método se transformou em uma gigantesca cascata de `if/else if`, misturando conversão de dados, processamento no banco e respostas de protocolo no mesmo lugar.

**Código Base (Simplificado):**
```java
void processDevicePublish(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    try {
        MqttTransportAdaptor payloadAdaptor = deviceSessionCtx.getPayloadAdaptor();

        if (deviceSessionCtx.isDeviceAttributesTopic(topicName)) {
            TransportProtos.PostAttributeMsg postAttributeMsg = payloadAdaptor.convertToPostAttributes(deviceSessionCtx, mqttMsg);
            transportService.process(deviceSessionCtx.getSessionInfo(), postAttributeMsg, getMetadata(deviceSessionCtx, topicName), getPubAckCallback(ctx, msgId, postAttributeMsg));

        } else if (deviceSessionCtx.isDeviceTelemetryTopic(topicName)) {
            TransportProtos.PostTelemetryMsg postTelemetryMsg = payloadAdaptor.convertToPostTelemetry(deviceSessionCtx, mqttMsg);
            transportService.process(deviceSessionCtx.getSessionInfo(), postTelemetryMsg, getMetadata(deviceSessionCtx, topicName), getPubAckCallback(ctx, msgId, postTelemetryMsg));

        } else if (topicName.startsWith(MqttTopics.DEVICE_RPC_RESPONSE_TOPIC)) {
            TransportProtos.ToDeviceRpcResponseMsg rpcResponseMsg = payloadAdaptor.convertToDeviceRpcResponse(deviceSessionCtx, mqttMsg, MqttTopics.DEVICE_RPC_RESPONSE_TOPIC);
            transportService.process(deviceSessionCtx.getSessionInfo(), rpcResponseMsg, getPubAckCallback(ctx, msgId, rpcResponseMsg));

        } else if (topicName.equals(MqttTopics.DEVICE_CLAIM_TOPIC)) {
            TransportProtos.ClaimDeviceMsg claimDeviceMsg = payloadAdaptor.convertToClaimDevice(deviceSessionCtx, mqttMsg);
            transportService.process(deviceSessionCtx.getSessionInfo(), claimDeviceMsg, getPubAckCallback(ctx, msgId, claimDeviceMsg));

        } // ... O código original possui dezenas de outros "else if" sequenciais

    } catch (Exception e) {
        log.warn("[{}] Failed to process publish msg [{}][{}]", sessionId, topicName, msgId, e);
        ctx.close();
    }
}
```
<p style="font-size: 16px;"><sub><i>Linha 557 do arquivo CloudClient.java</i></sub></p>


**⚠️ Antes de alterar o código, responda brevemente no [formulário correspondente](https://docs.google.com/forms/d/1-wFwycXEUJrAVtQzR05qPgPee4qJppD0NrG-pXYv1yY/edit):**
* Analisando as estruturas de decisão que crescem descontroladamente, quais são os dois principais Code Smells que podemos classificar neste método, segundo a literatura de refatoração?
* O método acima fere qual princípio fundamental da Engenharia de Software (SOLID)? Justifique.
* Identifique o padrão que se repete dentro de cada bloco condicional. Quais são as duas etapas que sempre acontecem independente do tópico?

**Etapa 1: Limpando a poluição do roteador**
O problema principal não são os condicionais em si, mas o trabalho pesado (parsing e transporte) sendo feito dentro deles. A solução recomendada é a Extração de Método.
- Selecione todo o conteúdo interno do primeiro if (referente aos atributos do dispositivo: PostAttributeMsg).
- Mova esse bloco de código para um novo método privado, utilizando um nome que revele sua intenção (por exemplo, "handleDeviceAttributes").

📍 Checkpoint 1: Extração (Autovalidação):

O compilador exigirá que você passe variáveis locais para o seu novo método. Assegure-se de que a assinatura do seu método extraído ficou limpa e está recebendo os parâmetros necessários como ctx, mqttMsg, topicName e msgId.
Exemplo visual de uma parte do código sendo isolada:

```java
private void handleDeviceAttributes(/* seus parâmetros */) {
    // Lógica isolada de PostAttributeMsg aqui...
}
```

**Etapa 2: Repetição e Otimização do Fluxo Principal**
- Repita a Extração de Método para o bloco da Telemetria 
- Repita também para o bloco RPC

**Etapa 3: Substituição no Método "Pai"**

Agora que a complexidade foi abstraída para métodos auxiliares, volte ao método principal (processDevicePublish)
- Substitua o código poluído, que violava a responsabilidade única, pelas invocações dos seus novos métodos privados enxutos.

📍 **Checkpoint 2: Refatoração Concluída**
Verifique como o seu método processDevicePublish se tornou um "controlador". Ele agora apenas verifica o nome do tópico e delega a função, sem saber dos detalhes de implementação. 
```java
void processDevicePublish(ChannelHandlerContext ctx, MqttPublishMessage mqttMsg, String topicName, int msgId) {
    try {
        if (deviceSessionCtx.isDeviceAttributesTopic(topicName)) {
            handleDeviceAttributes(ctx, mqttMsg, topicName, msgId);
        } else if (deviceSessionCtx.isDeviceTelemetryTopic(topicName)) {
            handleDeviceTelemetry(ctx, mqttMsg, topicName, msgId);
        } 
        // ... a estrutura se mantém, mas os blocos estão limpos!
    } catch (Exception e) {
        log.warn("[{}] Failed to process...", sessionId, e);
        ctx.close();
    }
}
```
Validação Final: Salve seu progresso e responda às questões de fechamento no [formulário](https://docs.google.com/forms/d/1-wFwycXEUJrAVtQzR05qPgPee4qJppD0NrG-pXYv1yY/edit):
1. Quais são os principais benefícios alcançados ao isolar cada operação em seu próprio método privado no que diz respeito a teste?
2. O que acontece com a legibilidade da estrutura if / else if quando os detalhes de implementação são escondidos atrás de funções bem nomeadas?

**💡️ Compare o resultado final do seu código com o resultado esperado no arquivo `gabarito.md`**

---

## 6. Conclusão
Por fim, é importante salientar que algumas implementações podem abordar alguns code smells por opção pragmática ou decisão particular de projeto. Nesses casos, são exceções mediante a tomada de decisões claras e compreendidas.

Além disso, existem ferramentas para auxiliar essa prática, como assistentes de IA, recursos e extensões em IDEs (VSCode, IntelliJ) e plataformas de análise estática (Codacy, SonarQube, CodeClimate). ENTRETANTO, entender primeiro esse procedimento é essencial para posteriormente utilizar esses artifícios para agilizar o processo, que assim será assimilado e transparente para uma validação pessoal.

Refatoração é uma das práticas mais fundamentais do desenvolvimento de software para evitar códigos catastróficos e que estão cada vez mais frequentes com uso deliberado de IA generativa para geração de código, sem revisão de propriedades e princípios de software que seguem as boas práticas da engenharia de software.

Foi utilizado como referência o capítulo 9 do livro Engenharia de Software, de autoria do professor Marco Túlio Valente.
