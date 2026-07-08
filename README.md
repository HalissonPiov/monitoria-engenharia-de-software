[Inserir Imagem: Logos institucionais - LE COMP, Programa de Monitoria 2026.1, UFOP]

# Tutorial Prático: Refatoração (Linguagem Java)
**Disciplina:** Engenharia de Software  
**Autor:** Hálisson Piovezana  

---

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
[Inserir Imagem: Ilustração/Ícone sobre Engenharia de Software Moderna ou Refatoração]

Conforme estabelecido na literatura da Engenharia de Software Moderna (Capítulo 9), refatoração consiste em modificações realizadas na estrutura interna de um ecossistema de software com o objetivo de melhorar sua arquitetura, legibilidade e manutenibilidade, sem alterar o seu comportamento. Refatorar não significa corrigir bugs ou adicionar novas funcionalidades, mas sim otimizar um código que já funciona para torná-lo mais limpo e resiliente a futuras expansões, seguindo as boas práticas.

### Pontos Positivos e Importância nos Projetos:
* **Combate à Segunda Lei de Lehman (Complexidade Crescente):** Esta lei afirma que à medida que um programa evolui, sua complexidade interna aumenta, a menos que se dedique um trabalho ativo para reduzi-la. A refatoração é a ferramenta primária para estabilizar esse declínio, contendo o endividamento técnico.
* **Melhoria do Desenvolvimento Futuro:** Um código limpo e livre de deformidades arquiteturais (code smells) reduz drasticamente e comprovadamente o tempo necessário para depuração e implementação de novas features pelo time.
* **Foco em Coesão e Acoplamento:** O processo elimina redundâncias, aumenta a coesão das classes (Responsabilidade Única) e diminui o acoplamento prejudicial entre módulos.

---

## 3. Visão Geral dos Projetos e Arquivos Coletados
Para garantir a imersão em cenários de desenvolvimento de sistemas de produção reais, este tutorial adota trechos arquiteturais e conceituais extraídos de dois grandes ecossistemas de software livre voltados para a Internet das Coisas (IoT), em linguagem Java:

### Projeto 1: Eclipse Kura
* **O que é o projeto:** É um framework baseado em Java e OSGi voltado para a construção de gateways de IoT no ecossistema M2M (Machine-to-Machine). Ele atua na camada de edge computing, servindo de ponte inteligente entre os sensores físicos locais e os servidores de computação em nuvem.
* **Arquivo Selecionado:** `CloudClient.java`
  * [Inserir Link: Caminho para o diretório/arquivo CloudClient.java no repositório do Eclipse Kura]
* **Contexto:** Este componente gerencia os canais de dados ativos entre o gateway físico e as aplicações cloud. Ele lida diretamente com conexões de telemetria baseadas em protocolos de rede restritos e foi selecionado devido à sua natureza crítica de passagem de parâmetros de conectividade.

### Projeto 2: ThingsBoard
* **O que é o projeto:** Uma plataforma IoT em nuvem altamente escalável de código aberto usada para coleta, processamento, visualização de telemetria de sensores e gerenciamento remoto de frotas de dispositivos em larga escala.
* **Arquivo Selecionado:** `MqttTransportHandler.java`
  * [Inserir Link: Caminho para o diretório/arquivo MqttTransportHandler.java no repositório do ThingsBoard]
* **Contexto:** Localizado no núcleo de rede da camada de transporte, este arquivo é responsável por interceptar e decodificar fluxos brutos de pacotes MQTT recebidos de milhares de sensores espalhados geograficamente. Ele foi selecionado por conter algoritmos densos de tratamento de estados e decisões estruturadas em cascata.

---

## 4. Exercício Prático Proposto
Com base nas características descritas do software e práticas de identificação de code smells, os alunos deverão propor possíveis refatorações válidas para os componentes dos projetos abordados.

### Atividade 1: Lidando com Parâmetros Excessivos
**Contexto:** Na interface responsável por realizar o envio de telemetria de um dispositivo para o broker na nuvem, o método de publicação acabou acumulando diversas variáveis primitivas de configuração ao longo do tempo.

**Problemas:**
* **Falta de Legibilidade:** Lendo a chamada do método, é impossível saber o que 1, true e 2 significam sem olhar a documentação.
* **Propensão a Erros (Bugs Silenciosos):** Como qos e priority são do tipo int, se o programador acidentalmente inverter a ordem na hora de chamar o método (passar a prioridade no lugar do QoS), o código vai compilar sem erros, mas o dispositivo IoT vai se comportar de forma errada na rede.
* **Rigidez:** Se amanhã o protocolo de rede for atualizado e precisarmos passar um novo parâmetro, como um timeToLive (tempo de expiração), teremos que alterar a assinatura da interface, quebrando todas as classes que já implementavam esse método.

**Código Base:**

[Inserir Imagem: Captura de tela contendo o código original da interface CloudClient]

**Antes de alterar o código, responda brevemente:**
1. Qual é o nome formal desse Code Smell segundo o livro-texto de Engenharia de Software Moderna?
2. O que aconteceria em tempo de compilação e de execução se um desenvolvedor passasse o valor de priority no lugar de qos ao chamar o método?
3. Qual técnica de refatoração você utilizaria para resolver isso sem quebrar a coesão?

---

### 4.1. Passo a Passo

**Etapa 1: Agrupando os metadados**
Em vez de passar parâmetros soltos, vamos criar uma estrutura de dados dedicada para as configurações de rede.
* Crie uma nova classe chamada `PublishConfig`.
* Mova os atributos relacionados ao contexto da mensagem (deviceId, appTopic, qos, retain, priority) para dentro desta classe.

**Checkpoint 1: Verifique a sua classe `PublishConfig`.**
* Os atributos foram declarados como `private final` para garantir a imutabilidade durante o tráfego na rede (recomendado)?
* Você criou um construtor que inicializa todos esses campos?
* Os métodos Getters foram gerados? (Recomendado não criar Setters: objetos de configuração não devem ser alterados após instanciados).

**Etapa 2: Atualizando o Contrato**
Agora, substitua a longa lista de parâmetros na interface original para utilizar a sua nova classe, mantendo apenas o conteúdo da mensagem (payload) separado de suas configurações (config).

**Checkpoint 2 (Resultado Esperado):** A sua interface `CloudClient` deve ter ficado semelhante a esta assinatura enxuta, mantendo as exceções contratuais originais:

[Inserir Imagem: Captura de tela contendo o código refatorado da interface CloudClient]

---

### Atividade 2: Desestruturando a "Cadeia de Comandos"
**Contexto:** Ao processar uma mensagem publicada pelo dispositivo, o método `processDevicePublish` roteia a ação com base no tópico da mensagem. Com a evolução do projeto, o método se transformou em uma gigantesca cascata de if/else if, misturando conversão de dados, processamento no banco e respostas de protocolo no mesmo lugar.

**Código Base (Simplificado):**

[Inserir Imagem: Captura de tela contendo o código base simplificado com a cascata de if/else]

**Instruções:**
* Identifique o tipo de Code Smell presente.
* Aplique a refatoração de Extração de Método para remover o trabalho braçal de dentro dos condicionais tornando o método principal limpo e focado apenas no fluxo de controle.

---

## 5. Conclusão
Por fim, é importante salientar que algumas implementações podem abordar alguns code smells por opção pragmática ou decisão particular de projeto. Nesses casos, são exceções mediante a tomada de decisões claras e compreendidas.

Além disso, existem ferramentas para auxiliar essa prática, como assistentes de IA, recursos e extensões em IDEs (VSCode, IntelliJ) e plataformas de análise estática (Codacy, SonarQube, CodeClimate). ENTRETANTO, entender primeiro esse procedimento é essencial para posteriormente utilizar esses artifícios para agilizar o processo, que assim será assimilado e transparente para uma validação pessoal.

Refatoração é uma das práticas mais fundamentais do desenvolvimento de software para evitar códigos catastróficos e que estão cada vez mais frequentes com uso deliberado de IA generativa para geração de código, sem revisão de propriedades e princípios de software que seguem as boas práticas da engenharia de software.
