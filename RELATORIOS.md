# Relatórios — Stock do snack bar

## Estado atual do projeto
- **Funciona (testado em Chromium):** APK v1.5 em `dist/`. Páginas Avisos (quadro da equipa, abre aqui), Stock, Histórico e Mais. Sem mínimos nem reposição. Mais tem lista de compras manual, receção, contagem rápida e notas por produto. Mantém stock offline, partilha opcional via Firebase, até 3 utilizadores e histórico de 200 alterações.
- **Falta:** testar num telemóvel real (visual, ícone, margens, teclado) e com a base de dados Firebase verdadeira.
- **Maior risco:** a partilha e os utilizadores só foram testados contra uma imitação local da Firebase. O comportamento real (regras, PATCH via Java, ponte Android) está por confirmar.
- **Segurança:** a palavra-passe da chave esteve escrita no `app/build.gradle` (público) até ao commit `60d93c4`. Já foi retirada, mas continua no histórico do git. Recomenda-se mudar a palavra-passe da chave; o certificado mantém-se igual.

---

## 2026-11-26 19:20 — Avisos da equipa; fim dos mínimos e da reposição
- **Pedido:** a app serve para saber o que há em stock, não para avisar o que comprar. Remover tudo o que é mínimo/reposição e transformar o Início num quadro de avisos da equipa.
- **Feito:**
  - **Mínimo removido:** campo na edição, "A acabar / Esgotado", indicadores vermelhos, sugestões, página Compras automática e cartão do Início. Quantidade 0 mostra só "0", sem destaque.
  - **Dados antigos:** o campo `min` (local ou Firebase) é lido sem erro e preservado tal como está no produto. Não é mostrado nem usado, nem apagado à força. As notas antigas do histórico que falavam do mínimo deixam de aparecer.
  - **Texto de enviar/receber:** agora `Família | Nome | quantidade unidade`; a coluna `min` saiu. O importador aceita também os textos antigos de 4 colunas (ignora o `min`). Os produtos importados mantêm a nota de um produto com o mesmo nome.
  - **Navegação:** Avisos · Stock · Histórico · Mais. A app abre em Avisos e o botão voltar volta a Avisos.
  - **Avisos:**
    - Caixa "Escreve um aviso para a equipa" com contador 0/300 e botão laranja "Publicar" (desativado com o texto vazio).
    - Lista do mais recente para o mais antigo, com nome e "hoje 14:32" / "ontem 09:10" / "dd/mm hh:mm". Os fixados aparecem primeiro, com ícone e barra laranja.
    - "Resolvido" esbate o aviso e desce-o para "Resolvidos (N)", recolhido por defeito; pode reabrir-se. Apagar em dois toques. Qualquer utilizador pode fazer tudo.
    - Máximo de 50: saem primeiro os resolvidos mais antigos, depois o mais antigo por resolver que não esteja fixado.
    - Ponto laranja em "Avisos" quando há avisos por resolver de outros utilizadores ainda não vistos. O último momento visto fica guardado neste telemóvel, por utilizador.
    - Mensagem a convidar a escrever o primeiro quando não há avisos.
    - Firebase `lojas/<código>/notices/<id>` = {text, user, uid, ts, pinned, resolved, resolvedTs}. Criar e apagar escrevem o nó inteiro; fixar e resolver escrevem só os campos desse nó, num PATCH atómico. Usam a mesma fila offline e não entram no histórico do stock.
    - Se um telemóvel fixar um aviso que outro acabou de apagar, o nó incompleto é limpo na sincronização seguinte.
  - **Mais:** Lista de compras, Receber mercadoria, Contagem rápida, Enviar ou receber stock, Partilhar entre telemóveis, Gerir utilizadores, Trocar utilizador, Repor dados de exemplo.
  - **Lista de compras manual:** itens livres (60 caracteres) com quem os escreveu, visto de comprado (descem e ficam riscados), apagar em dois toques, "Apagar comprados", copiar e WhatsApp. Partilhada em `lojas/<código>/shopping`, com um nó por item.
  - **Receber mercadoria:** mostra todos os materiais; soma e regista "Receção" como antes.
  - Ao ligar um telemóvel a uma partilha que ainda não tem avisos ou lista, os locais são enviados (como já acontecia com os utilizadores).
  - Correção: ao ligar um telemóvel novo a uma partilha com utilizadores, o formulário "Novo utilizador" fecha e aparece a lista para escolher.
  - Versão 1.5 (versionCode 6). Assinada com `keystore/stock.jks`; a palavra-passe só via `STOCK_KS_PASS`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk` (alterados).
- **Estado:**
  - Compila. Assinatura verificada: o certificado é o mesmo.
  - Novo teste em Chromium (101 verificações, todas OK, sem erros JS):
    - **Dados antigos:** carregam com `min`, o `min` é preservado ao editar, quantidade 0 sem destaque e texto antigo de 4 colunas importado.
    - **Ecrã (390×844 e 360×640):** nenhum texto, campo ou atributo com mínimo, "a acabar", "esgotado" ou "a repor" (com janelas abertas e fechadas); todas as páginas e janelas sem texto cortado; a barra não tapa conteúdo.
    - **Avisos:** publicar, limite 300, fixar, resolver, apagar em dois toques e voltar.
    - **Lista manual, receção e contagem:** funcionam; os avisos não entram no histórico.
    - **Dois telemóveis simulados:** veem os mesmos avisos; ponto de novo aparece e desaparece; escritas simultâneas (fixar num, resolver noutro) não se atropelam; apagar vs fixar não deixa nó fantasma; offline com envio depois; lista partilhada; limite de 50 local e na Firebase pela ordem certa; stock continua sincronizado.
  - As 39 verificações anteriores (utilizadores, histórico, partilha, offline, limite 200) continuam a passar.
  - **Não testado:** num telemóvel real e com a Firebase verdadeira.
- **Problemas:**
  - Telemóveis ainda com a v1.4 ligados à mesma partilha continuam a mostrar avisos de mínimo e não veem os avisos da equipa. Todos devem atualizar.
  - O ponto de avisos novos depende do relógio de cada telemóvel (hora de publicação). Um utilizador escolhido pela primeira vez num telemóvel começa sem avisos "novos".
  - O "Resolvido" retira o fixado ao aviso, para não ficar fixado na secção de resolvidos.
- **Próximos passos:**
  1. Atualizar todos os telemóveis para a v1.5 e publicar um aviso de teste com a Firebase real.
  2. Confirmar no telemóvel que o teclado não tapa a caixa de avisos ao escrever.
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-26 18:09 — Páginas por secção, contagem rápida, receção e notas
- **Pedido:** dividir a app em páginas (Início, Stock, Compras, Histórico, Mais), deixar os avisos de mínimo só em Início e Compras, e acrescentar contagem rápida, receção de mercadoria e nota por produto.
- **Feito:**
  - Navegação com 5 separadores. A app abre em Início. O botão voltar leva ao Início.
  - **Início:**
    - Bloco "A acabar / Esgotado": esgotados primeiro, depois pela razão quantidade/mínimo. Tem − e + diretos e vermelho discreto.
    - Cartão laranja "Lista de compras" com o número em falta; abre Compras.
    - "Últimas alterações" (5) com "Ver tudo". "Está tudo em stock." quando nada falta.
  - **Stock:** pesquisa, famílias e lista com − e +, sem avisos nem pontos vermelhos. Dentro de cada família, os mais mexidos aparecem primeiro (contagem de entradas no histórico guardado; desempate alfabético).
  - **Compras:**
    - Lista automática do que está abaixo do mínimo, agrupada por família, com a sugestão (2×mín − qtd) e um visto ao comprar.
    - "Enviar lista" com texto, copiar e WhatsApp; os comprados aparecem marcados com "✓ comprado".
    - "Receber mercadoria": um campo por item, com os comprados pré-preenchidos com a sugestão e a opção "Mostrar todos os materiais". Soma ao stock (incrementos atómicos) e regista "Receção" no histórico.
  - **Mais:** Contagem rápida, Enviar ou receber stock, Partilhar entre telemóveis, Gerir utilizadores, Trocar utilizador, Repor dados de exemplo.
  - **Contagem rápida:**
    - Produto a produto, com barra de progresso e a quantidade atual. Vazio = "Certo"; um número = nova quantidade. Tem "Anterior" e "Terminar".
    - No fim mostra as diferenças e aplica-as. Cada diferença é gravada como valor absoluto e registada como "Contagem" no histórico.
  - **Nota por produto:** campo opcional na edição (até 60 caracteres), mostrada em pequeno na linha, guardada no próprio produto (também na Firebase). "Aplicar texto colado" mantém as notas dos produtos com o mesmo nome.
  - Cabeçalho só com "N materiais" (sem "a repor").
  - Versão 1.4 (versionCode 5), assinada com `keystore/stock.jks`; a palavra-passe só veio da variável de ambiente.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `dist/StockSnackBar.apk` (alterados).
- **Estado:**
  - Compila. Assinatura verificada: o certificado é o mesmo.
  - Novo teste em Chromium a 390×844 e 360×640 (94 verificações): todas as páginas e janelas sem texto cortado, barra em baixo sem tapar conteúdo, sem erros JS. Confirma:
    - avisos só em Início/Compras;
    - ajuste no Início reflete-se em Stock e Compras;
    - visto, envio da lista e receção (stock + histórico);
    - contagem (Certo, número novo, número inválido, resumo, aplicar, histórico);
    - nota; "tudo em stock"; voltar.
  - As 39 verificações anteriores (utilizadores, histórico, partilha simulada com 2 telemóveis, offline, limite 200) foram adaptadas à nova navegação e passam todas.
  - **Não testado:** num telemóvel real e com a Firebase verdadeira.
- **Problemas:**
  - O visto de "comprado" fica só no telemóvel onde foi marcado, sem ir para a Firebase, para não mudar a estrutura dos dados partilhados. É apagado sozinho quando o produto deixa de estar abaixo do mínimo.
  - A nota é um campo novo opcional no produto (`note`). Não entra no texto de "Enviar ou receber stock", para manter o formato combinado.
  - A contagem não fica guardada se a app fechar a meio.
  - "Mais mexidos" usa só as últimas 200 alterações do histórico.
- **Próximos passos:**
  1. Atualizar os telemóveis e fazer uma contagem real e uma receção real.
  2. Decidir se o visto "comprado" deve ser partilhado entre telemóveis.
  3. Mudar a palavra-passe da chave (pendente, ver entradas anteriores).

---

## 2026-11-26 18:01 — Redesenho visual: escuro, laranja e preto
- **Pedido:** redesenho visual mais limpo, com as cores laranja e preto, sem mudar funcionalidades nem dados.
- **Feito:**
  - **Tema único escuro:** o modo claro foi removido. Fundo `#0e0e0e`, cartões `#1a1a1a`, texto branco.
    - Laranja `#ff7a00`, com texto preto por cima (contraste ~8:1), só nos botões principais, no item ativo da navegação, no separador ativo e no avatar.
    - Vermelho (`#ff5a52`) para "a repor" e avisos.
  - **Ecrã principal:**
    - Só pesquisa (com botão laranja "+" para novo material), separadores por família e lista.
    - Cada linha mostra nome, "mín. X" e quantidade grande, com botões − e + de 46 px. Tocar no nome abre a edição (o botão "Editar" foi removido).
    - "A repor": barra lateral vermelha fina, texto "Baixo"/"Esgotado" a vermelho e um ponto vermelho no separador. Sem contadores amarelos.
  - **Cabeçalho:** nome do café, resumo "N materiais · X a repor" e o utilizador (avatar e primeiro nome; tocar abre a troca).
  - **Barra fixa em baixo:** Stock · A repor · Histórico · Mais, com o ativo a laranja. O Histórico passou de janela a vista própria. O botão voltar regressa ao Stock.
  - **Menu "Mais":** Lista de compras, Enviar ou receber stock, Partilhar entre telemóveis (com o estado), Gerir utilizadores, Trocar utilizador e Repor dados de exemplo (dois toques).
  - Ecrã de utilizadores e todas as janelas no mesmo estilo. "Gerir utilizadores" abre diretamente em modo de gestão.
  - Android: o tema é sempre escuro (removido `values-night`) e o fundo da janela é `#0E0E0E`. Ícone: chávena laranja sobre fundo preto.
  - Versão 1.3 (versionCode 4). Assinado com `keystore/stock.jks`, com a palavra-passe só via variável de ambiente `STOCK_KS_PASS`. O certificado SHA-256 é igual ao anterior.
- **Ficheiros:** alterados `app/src/main/assets/index.html`, `app/build.gradle`, `app/src/main/java/pt/snackbar/stock/MainActivity.java`, `app/src/main/res/values/themes.xml`, `app/src/main/res/drawable/ic_coffee.xml` e `dist/StockSnackBar.apk`. Apagado `app/src/main/res/values-night/themes.xml`.
- **Estado:**
  - Compila. Assinatura verificada.
  - Verificações em Chromium a 390×844 e 360×640 (32 verificações por medida): todos os ecrãs (inicial, principal, a repor, histórico, Mais, editar, enviar, partilha, gerir utilizadores, repor). Passam todas e sem erros JS. Cobrem:
    - texto cortado e scroll horizontal;
    - a barra em baixo não tapa o último produto;
    - botões − e + com pelo menos 44 px;
    - funções: pesquisa, separadores, filtros, ajustar, editar, histórico com filtro, lista de compras, aplicar texto, trocar utilizador, repor.
  - As 39 verificações funcionais anteriores (utilizadores, histórico, partilha simulada com 2 telemóveis, offline, limite 200) foram repetidas com a nova navegação: passam todas.
  - **Não testado:** num telemóvel real (aspeto do ícone no launcher, barras do sistema, teclado) e com a Firebase verdadeira.
- **Problemas:**
  - A 360 px, nomes longos ocupam 2 linhas. Decisão: aceitar, em vez de reduzir os botões abaixo de 46 px.
  - No cabeçalho só aparece o primeiro nome do utilizador, para o título caber; o nome completo continua no ecrã de utilizadores e no histórico.
  - A mensagem do commit `94d95b2` diz "v1.4" por engano: a versão é 1.3 (versionCode 4). Não reescrevi o histórico.
- **Próximos passos:**
  1. Atualizar os telemóveis e confirmar o aspeto real (ícone, barras do sistema, teclado a abrir na edição).
  2. Confirmar com a Firebase real que dois telemóveis veem as mesmas alterações.
  3. Mudar a palavra-passe da chave (continua no histórico do git, ver entrada anterior).

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
