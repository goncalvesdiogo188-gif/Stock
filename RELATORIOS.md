# Relatórios — Stock do snack bar

## Estado atual do projeto
- **Funciona (testado em Chromium):** APK v1.2 em `dist/`, com stock offline, partilha opcional via Firebase, até 3 utilizadores e histórico das últimas 200 alterações.
- **Falta:** testar num telemóvel real e com a base de dados Firebase verdadeira.
- **Maior risco:** a partilha e os utilizadores só foram testados contra uma imitação local da Firebase. O comportamento real (regras, PATCH via Java, ponte Android) está por confirmar.
- **Segurança:** a palavra-passe da chave esteve escrita no `app/build.gradle` (público) até ao commit `60d93c4`. Já foi retirada, mas continua no histórico do git. Recomenda-se mudar a palavra-passe da chave; o certificado mantém-se igual.

---

## 2026-11-26 17:44 — Utilizadores (até 3) e histórico de alterações
- **Pedido:** ecrã inicial com até 3 utilizadores (só nome), registo de quem faz cada alteração, ecrã "Histórico" com filtro, e assinar com a chave anexada.
- **Feito:**
  - Ecrã inicial: botões grandes com os nomes e "+ Novo utilizador" (escondido com 3). Nome com 2 a 20 caracteres, sem repetidos (ignora maiúsculas e acentos).
  - O telemóvel lembra o último utilizador. No ecrã principal aparece "Utilizador: X" e os botões "Trocar utilizador" e "Histórico".
  - "Gerir utilizadores": mudar o nome e apagar com dois toques. Se o utilizador atual for apagado, a app volta ao ecrã inicial.
  - Histórico: utilizador, material, quantidade antes/depois, data e hora e tipo (ajuste, editado, novo, apagado, repor exemplo, texto colado).
    - Mais recente primeiro, agrupado por dia, com filtro por utilizador. Os utilizadores apagados aparecem como "Nome (apagado)".
    - Guarda no máximo 200 entradas.
    - Toques seguidos em +/− no mesmo material, pelo mesmo utilizador, em menos de 1 minuto, juntam-se numa só entrada.
  - Firebase: utilizadores em `lojas/<código>/users` e histórico em `lojas/<código>/history`. Cada entrada do histórico vai no mesmo PATCH que a alteração do stock (atómico). Os incrementos atómicos e a fila offline mantêm-se. Sem alterações às regras.
  - Se 2 telemóveis criarem o 4.º utilizador ao mesmo tempo, ficam os 3 mais antigos.
  - `app/build.gradle`: a palavra-passe da chave sai do código. Passa a vir de `STOCK_KS_PASS` ou de `keystore/keystore.properties` (ignorado pelo git). A compilação falha se a chave existir e faltar a palavra-passe.
  - Chave anexada copiada para `keystore/stock.jks`. É idêntica byte a byte à anterior. Alias `stock` confirmado e SHA-256 do certificado igual ao APK anterior (`EF:DF:0A:A4:…:2C:78:57`).
  - Versão 1.2 (versionCode 3). APK assinado com essa chave (v2) e copiado para `dist/StockSnackBar.apk`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk` (alterados).
- **Estado:**
  - Compila. Assinatura verificada com `apksigner`: o certificado é o mesmo, por isso a atualização instala por cima.
  - 39 verificações automáticas em Chromium contra uma imitação local da Firebase, com dois "telemóveis": todas passam e sem erros JS. Cobrem:
    - criar 3 utilizadores, recusar o 4.º, recusar nomes repetidos e curtos, trocar de utilizador, lembrar após recarregar;
    - renomear (incluindo para um nome repetido) e apagar com confirmação; o histórico mantém o nome apagado;
    - o outro telemóvel vê os mesmos utilizadores e o histórico com o utilizador certo;
    - toques simultâneos somam-se; modo offline com envio quando a rede volta;
    - limite de 200 (local e Firebase).
  - **Não testado:** num telemóvel Android real (ponte Java, botão voltar, teclado) e com a Firebase verdadeira.
- **Problemas:**
  - Encontrado e corrigido: os botões "Novo utilizador" e "Voltar" não ficavam escondidos (o CSS de `.btn` anulava `hidden`).
  - Encontrado e corrigido: a atualização da partilha a cada 4 s anulava o "Confirmar apagar" no ecrã de utilizadores.
  - A palavra-passe antiga continua no histórico do git (commit `5e7c117`). Não reescrevi o histórico, porque isso exige force-push. O risco é limitado, porque o ficheiro da chave nunca esteve no git.
  - Não há segurança nos utilizadores, por desenho: qualquer pessoa pode escolher qualquer nome.
- **Próximos passos:**
  1. Instalar a v1.2 por cima nos telemóveis e repetir os testes principais com a Firebase real.
  2. Mudar a palavra-passe da chave (`keytool -storepasswd` e `-keypasswd`): mantém o mesmo certificado e as atualizações continuam a instalar por cima.
  3. Opcional: registar também a criação, mudança de nome e apagamento de utilizadores no histórico.

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
