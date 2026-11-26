# Relatórios — Stock do snack bar

## Estado atual do projeto
- **Funciona:** app Android (APK v1.1 em `dist/`), com stock offline, famílias, filtros, lista de compras, envio/receção por texto e WhatsApp, e partilha opcional do stock entre telemóveis via Firebase.
- **Falta:** testar num telemóvel real e com uma base de dados Firebase verdadeira (o utilizador está a criá-la agora).
- **Maior risco:** a partilha só foi testada contra uma imitação local da Firebase; comportamento real (regras, endereço, PATCH via Java) por confirmar.
- **Atenção:** a chave de assinatura (`keystore/stock.jks`) não está no git. Sem ela, as atualizações obrigam a desinstalar a app.

---

## 2026-11-26 17:17 — Criação do relatório e estado inicial
- **Pedido:** criar o `RELATORIOS.md` com o estado atual do projeto e mantê-lo atualizado a partir de agora.
- **Feito:**
  - Criado `RELATORIOS.md` (este ficheiro).
  - Criado `CLAUDE.md` com as regras do relatório, para qualquer assistente que abra o repositório as seguir.
  - Resumo do que já existe (commits anteriores `5e7c117` e `4a9a270`):
    - **Estrutura:** projeto Gradle Android (`settings.gradle`, `build.gradle`, `gradle.properties`, wrapper Gradle 8.14.3), um módulo `app`.
    - **`app/src/main/assets/index.html`:** a app inteira num único ficheiro HTML/CSS/JS (pt-PT). Inclui o ecrã principal, a pesquisa, os filtros "Todo o stock"/"A repor", os separadores por família com contador amarelo, os botões − e +, as janelas de edição (apagar em dois toques), a lista de compras, a janela "Enviar ou receber stock", o "Repor dados de exemplo" e a janela "Partilhar entre telemóveis". Tem modo claro/escuro automático.
    - **`app/src/main/java/pt/snackbar/stock/MainActivity.java`:** WebView em ecrã inteiro. Respeita as margens do ecrã (barras, recorte, teclado) e trata o botão voltar. Ponte JS `AndroidBridge` com guardar/carregar (SharedPreferences), copiar, abrir ligações externas (WhatsApp) e pedidos HTTP.
    - **`app/src/main/java/pt/snackbar/stock/Http.java`:** pedidos HTTP à API REST da Firebase (PATCH enviado como POST com `X-HTTP-Method-Override`).
    - **`app/src/main/res/`:** tema claro/escuro, nome da app e ícone adaptativo de chávena de café.
    - **`dist/StockSnackBar.apk`:** APK v1.1 (versionCode 2), assinado, minSdk 26 (Android 8+), targetSdk 35. Só pede a permissão de Internet.
    - **`README.md`:** instruções de compilação e de configuração da Firebase.
  - **Como compilar:** JDK 17+ e Android SDK (platform 35). Criar `local.properties` com `sdk.dir=...` e correr `./gradlew assembleRelease`. O resultado fica em `app/build/outputs/apk/release/app-release.apk`, que se copia para `dist/StockSnackBar.apk`. Se existir `keystore/stock.jks`, é usada para assinar; senão usa a chave de debug.
- **Ficheiros:** criados `RELATORIOS.md` e `CLAUDE.md`.
- **Estado:**
  - Compila (testado nesta sessão com AGP 8.7.3 e Gradle 8.14.3).
  - A interface foi testada em Chromium (Playwright): ajustes de quantidade, filtros, pesquisa, edição/apagar, importação/exportação e persistência. Sem erros JS.
  - A partilha foi testada com dois "telemóveis" simulados contra uma imitação local da Firebase: alterações propagam-se em menos de 5 s, os toques simultâneos somam-se e a fila offline funciona.
  - **Não testado** num telemóvel real nem com a Firebase verdadeira.
- **Problemas:**
  - As famílias do enunciado não cobrem tudo. Decisão: bebidas, café, açúcar, fiambre e batatas vão para "Comida"; copos, tampas e guardanapos vão para "Detergentes".
  - A partilha exige servidor, o que contraria o pedido original de "sem servidor". Escolhi a Firebase Realtime Database porque é gratuita, sem servidor próprio e tem incrementos atómicos. Fica opcional: sem ligação, a app funciona só no telemóvel.
  - Segurança da partilha: o acesso depende apenas de um código aleatório de 20 caracteres na ligação, e quem tiver a ligação pode alterar o stock.
- **Próximos passos:**
  1. Acabar de criar a Realtime Database na consola da Firebase (regras + endereço) e ligar o primeiro telemóvel.
  2. Instalar o APK em 2 telemóveis e confirmar que a sincronização, o WhatsApp, o copiar e o botão voltar funcionam.
  3. Guardar a chave de assinatura num local seguro fora do contentor (necessária para futuras atualizações).
