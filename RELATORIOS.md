# Relatórios — Stock do snack bar

## Estado atual do projeto
- **Funciona (testado em Chromium):** APK v2.3 em `dist/` ("caffé c@ffé").
  - "Desfazer" durante 5 s em quantidades, produtos apagados, avisos e faltas.
  - Ecrã de arranque com a gota (nativo no Android 12+). Animações curtas, que respeitam "reduzir animações". Vibração ligeira com opção em Mais.
  - Estilo com medidas numa escala única (variáveis CSS) e ícones iguais. Bolinha de sincronização no cabeçalho.
  - Mantém stock em secções, ordenar, compras, contagem, histórico, partilha Firebase e modo offline.
- **Falta:** testar num telemóvel real: vibração, ecrã de arranque nativo, animações a 60 fps, botão voltar. Testar também com a Firebase verdadeira.
- **Maior risco:** a partilha só foi testada contra uma imitação local da Firebase. A vibração e o arranque nativo só se veem num Android real.
- **Segurança:** a palavra-passe da chave continua no histórico do git (até ao commit `60d93c4`). Recomenda-se mudá-la.

---

## 2026-11-28 09:20 — Desfazer, arranque, animações, vibração, estilo uniforme e bolinha de sincronização (v2.3)
- **Pedido:** polimento visual e de experiência, sem mudar dados, estrutura da Firebase nem regras: Desfazer, ecrã de abertura, animações, vibração, estilo uniforme e indicador discreto de sincronização.
- **Feito:**
  - **Desfazer:**
    - Barra escura durante 5 s com texto curto (ex.: "Água 0,5 L: 36 → 34") e "Desfazer" a laranja.
    - Aparece depois de +, −, escrever e guardar uma quantidade, apagar um produto, resolver/reabrir/apagar um aviso e marcar/apagar uma falta.
    - Toques seguidos no mesmo produto juntam-se numa só barra, com o valor inicial.
    - Desfazer uma quantidade escreve o valor anterior (não uma soma) e regista no histórico "Ajuste · desfeito". Um produto reposto fica como "Ajuste · desfeito: apagar", com a mesma secção, nota e ordem. Avisos e faltas voltam ao estado anterior.
    - Usa a fila normal, por isso funciona offline e com a partilha.
    - A barra fica por cima da barra de baixo e do "+ Novo produto". Na folha do produto aparece logo acima do "Guardar". Substitui o aviso curto dessas ações.
  - **Ecrã de arranque:**
    - Android 12+: splash nativo (`values-v31`) com a gota sobre branco.
    - Android 8–11: a janela abre com a gota ao centro (`splash_window`).
    - O Android segura o arranque até a página avisar que está pronta (ponte `ready`; no máximo 1,2 s). Na página, a mesma gota, ao mesmo tamanho, desvanece em 0,25 s. Se algo falhar, sai sempre ao fim de 1,5 s. Não há atraso de propósito.
  - **Animações:**
    - Folhas a abrir (0,18 s) e a fechar (0,16 s, com descida), entrada das páginas e das secções (0,2 s), e números que mudam (pequena subida a laranja, 0,24 s).
    - Só usam transformações e transparência, que o telemóvel faz sem esforço.
    - Com "reduzir animações" no sistema, quase nada se move e as folhas fecham logo.
  - **Vibração:**
    - Feedback háptico do Android pela ponte (`haptic`, com `performHapticFeedback`), sem permissões novas. Toque curto em + e −; "confirmar" ao guardar e ao desfazer; "rejeitar" ao apagar.
    - Respeita a definição de vibração do telemóvel e falha em silêncio.
    - Opção "Vibração ao tocar" em Mais, ligada por defeito e guardada só neste telemóvel.
  - **Estilo uniforme:**
    - Todas as medidas passaram para variáveis em `:root`: espaços `--sp-*` (2 a 64 px), cantos (12 / 14 / 20 / 24 px), alturas (44 / 52 / 58 px), letra (legenda 13, pequeno 14, texto 16, subtítulo 19, título 22, números 24 / 28 / 40) e ícones (22 px, traço 2).
    - Ícones refeitos no mesmo estilo de traço: "Mais", alfinete, seta dos cartões, "×" da pesquisa e setas de ordenar.
    - Valores soltos foram aproximados à escala (ex.: 14 → 16 px de margem, 17 → 16 px de texto) e a margem interna por defeito dos botões foi retirada.
    - Revistas todas as páginas e janelas com um verificador automático, sem nada fora da escala.
  - **Bolinha de sincronização:**
    - Bolinha de 10 px antes do utilizador, sem texto: cinzenta sem partilha, verde suave quando sincronizado, âmbar com alterações por enviar.
    - Tocar mostra "Atualizado há X s" ou "Sem ligação: as alterações seguem quando voltares a ligar". Substitui o texto "Partilhado entre telemóveis" e a faixa vermelha de "sem Internet". A faixa fica só para erros, como falta de permissão.
  - Versão 2.3 (versionCode 14); a palavra-passe passou só por `STOCK_KS_PASS`. README atualizado.
- **Ficheiros:**
  - Alterados: `app/src/main/assets/index.html`, `app/src/main/java/pt/snackbar/stock/MainActivity.java`, `app/src/main/res/values/themes.xml`, `app/src/main/res/values-v27/themes.xml`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk`.
  - Criados: `app/src/main/res/values-v31/themes.xml`, `app/src/main/res/drawable/splash_icon.xml`, `app/src/main/res/drawable/splash_window.xml`.
- **Estado:**
  - Compila. O certificado é igual, o tema do Android 12+ e a gota estão no APK, e o `index.html` dentro do APK é o testado.
  - Teste em Chromium a 390×844 e 360×640, com dois telemóveis numa imitação local da Firebase: 102 verificações novas, mais as 160 + 137 + 80 anteriores, todas OK, sem erros JS.
    - Desfazer em todos os tipos de ação: juntar toques, desaparecer aos 5 s, offline, e efeito no outro telemóvel e na Firebase. A barra não tapa a navegação, o "+", o "Guardar" nem os +/−.
    - Abertura sai em menos de 1,5 s e a ponte recebe "pronta"; com movimento reduzido, a abertura sai logo, as janelas fecham sem esperar e as animações duram 1 ms.
    - Vibração: a ponte simulada recebe "tap"/"confirm"/"reject"; com a opção desligada não vibra (opção guardada depois de recarregar); uma ponte com erro não parte nada.
    - Bolinha nos 3 estados e as mensagens ao tocar.
    - Uniformidade em 19 páginas e janelas: espaços, cantos, letra, ícones e alturas dentro da escala, sem texto cortado, sobreposições nem deslocamento horizontal.
    - Continua a funcionar: utilizadores, histórico, avisos, faltas, compras, contagem, receção, partilha e secções.
  - **Não testado:** num Android real (vibração, splash nativo e a passagem para a página, animações a 60 fps num telemóvel modesto, botão voltar) e com a Firebase verdadeira.
- **Problemas:**
  - Corrigidos durante o trabalho:
    - A escala alargou a seta dos filtros do histórico e "Novo/Apagado" deixou de caber a 360 px.
    - A bolinha cortava o título "Stock caffé c@ffé" a 360 px. Apertei os espaços do cabeçalho; com nomes curtos o título cabe.
    - O interruptor da vibração ficava por baixo do texto.
  - Com nomes de utilizador compridos (ex.: "Mariana") a 360 px, o título continua a ser encurtado com "…", como já acontecia antes.
  - O texto base passou de 17 para 16 px, para haver um só tamanho de texto.
  - A faixa vermelha "Sem Internet" deixou de aparecer: agora é a bolinha âmbar que o indica.
- **Próximos passos:**
  1. Instalar a v2.3 num telemóvel e ver o arranque, a vibração e o "Desfazer".
  2. Se a vibração for fraca ou forte demais, ajustar o tipo de feedback.
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 12:30 — Início mais arejado e um só "Guardar" na folha do produto (v2.2)
- **Pedido:** só ajustes visuais. Mais espaço entre o logotipo e os cartões, com espaços proporcionais à altura do ecrã. Na folha do produto, tirar o "Guardar" de cima e ficar só o de baixo.
- **Feito:**
  - **Início:**
    - A área entre o cabeçalho e a barra de baixo é usada toda. O logotipo desce (margem 6% da altura, entre 20 e 64 px) e os cartões ficam centrados no espaço livre por baixo dele.
    - Espaço entre cartões: 2,6% da altura, entre 12 e 26 px. O logotipo encolhe um pouco em ecrãs baixos.
    - A altura do cabeçalho (maior com a linha "Partilhado entre telemóveis") é medida, para nunca haver scroll.
  - **Folha do produto:**
    - Saiu o "Guardar" de cima. O de baixo (antes "Guardar alterações", dentro de "Mais opções") passou para fora, chama-se "Guardar" e fica fixo no fundo da folha, à vista mesmo com o teclado aberto.
    - Grava o que mudou, como antes, e agora também a quantidade escrita no campo (antes era só o de cima que a gravava). Para isso, as duas gravações juntaram-se numa só função; os dados escritos são os mesmos de antes.
    - Enter no campo da quantidade também grava. "−" e "+" continuam a gravar logo. "Apagar produto" continua em "Mais opções".
  - Versão 2.2 (versionCode 13); a palavra-passe passou só por `STOCK_KS_PASS`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `dist/StockSnackBar.apk`.
- **Estado:**
  - Compila. O certificado é igual e o `index.html` dentro do APK é o testado.
  - Teste em Chromium a 390×844 e 360×640, com partilha numa imitação local da Firebase: 80 verificações novas, mais as 137 e 160 anteriores, todas OK, sem erros JS.
    - Início, sem e com a linha da partilha, sem scroll nem sobreposições. Espaços medidos:
      - 390×844: logotipo a 51 px do cabeçalho, 141–161 px até aos cartões, 22 px entre cartões.
      - 360×640: 38 px, 46–66 px e 17 px.
    - Folha: um só botão "Guardar", também com "Mais opções" aberto.
    - Grava quantidade, Enter, nome + unidade + nota + quantidade juntos, e o histórico regista Ajuste e Editado. "Sem alterações" quando nada mudou; número inválido ou nome vazio não gravam.
    - Fechar sem guardar com "Fechar", tocando fora e com o voltar do Android (simulado) não altera nada, nem na Firebase.
    - Com o ecrã encolhido a 55% e 45% da altura (como com teclado), o "Guardar" fica à vista, pode ser tocado e grava.
  - **Não testado:** num telemóvel real (teclado verdadeiro, botão voltar verdadeiro, aspeto do Início no ecrã real) e com a Firebase verdadeira.
- **Problemas:**
  - "Arrastar para baixo" para fechar a folha não existe na app (nunca existiu): a barra no topo da folha é só decorativa. Não foi acrescentado, porque o pedido era só visual. Fechar funciona com "Fechar", tocando fora ou com o voltar do Android.
  - Não confirmei num telemóvel porque é que o "Guardar" de cima falhava. Uma causa provável: depois de tocar em − ou +, a quantidade já está gravada e o botão respondia "Sem alterações".
- **Próximos passos:**
  1. Atualizar os telemóveis para a v2.2 e ver o Início no ecrã real.
  2. Se quiser, acrescentar "arrastar para baixo" para fechar as folhas.
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 12:05 — Compras, contagem por secção, histórico com filtros, ordenar produtos (v2.1)
- **Pedido:** só interface e dados novos opcionais, sem mudar as regras da Firebase. Lista de compras em "Mais"; contagem por âmbito com continuar; histórico com atalhos, tipo e resumo; ordenar produtos à mão em cada secção.
- **Feito:**
  - **Lista de compras** ("Mais" → "Lista de compras"):
    - Caixa "O que vais comprar?" (máx. 60) e "Adicionar", também com Enter. Não repete um texto que já esteja por comprar.
    - Cada item tem um círculo de visto (comprado desce para o fim e fica riscado) e "Apagar" em dois toques.
    - Botões "Copiar", "Enviar por WhatsApp" (só o que falta comprar) e "Apagar comprados" (dois toques, só aparece se houver comprados). Máximo 100 itens.
    - Partilhada em `lojas/<código>/purchases`, uma escrita por item na fila offline normal. Nada é escrito em `shopping`.
    - Nas Faltas por resolver, um texto laranja discreto "Passar para compras" copia a falta para a lista; a falta mantém-se.
  - **Contagem rápida:**
    - Ao começar pelo menu, pergunta o âmbito: "Tudo" ou uma secção (com a cor e o número de produtos; secções vazias desativadas).
    - Na página de uma secção, "Contar esta secção" começa logo. O passo mostra "3 de 5 · Bar".
    - O resto mantém-se: produto a produto, barra, vazio = "Certo", "Anterior", "Terminar", resumo, aplicar, histórico "Contagem".
    - O progresso fica no telemóvel, no mesmo registo dos outros dados. Ao abrir a app com uma contagem a meio, aparece "Continuar contagem" ou "Começar de novo". O mesmo acontece ao tocar em "Contagem rápida" ou "Contar esta secção".
  - **Histórico:**
    - Atalhos Hoje / Ontem / 7 dias (hoje e os 6 dias anteriores) / Tudo, com Tudo por defeito.
    - Filtro "Tipo" (Todos, Ajuste, Receção, Contagem, Novo/Apagado) ao lado de "Utilizador". Combinam com a pesquisa.
    - Resumo por baixo do título, ex.: "12 alterações hoje", "1 receção ontem", "1 novo ou apagado nos últimos 7 dias · Rui".
  - **Ordenar:**
    - Na secção com 2 ou mais produtos, "Ordenar" passa ao modo de ordenação: só nome e setas ▲ ▼; a pesquisa e "+ Novo produto" escondem-se. "Concluído" (ou o voltar do Android) sai.
    - Cada movimento grava a ordem da secção inteira num só pedido (campo opcional `order` nos produtos).
    - Produto novo numa secção ordenada entra no fim. Ao mudar de secção, o produto vai para o fim da nova (ou perde a ordem, se a nova não tiver).
  - Ajuste visual: a caixa de texto de Faltas e Compras passou a 16 px, para "O que vais comprar?" caber inteiro a 360 px.
  - README atualizado. Versão 2.1 (versionCode 12); a palavra-passe passou só por `STOCK_KS_PASS`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk`.
- **Estado:**
  - Compila. O certificado é igual ao da v2.0 e o `index.html` dentro do APK é o testado.
  - Teste em Chromium a 390×844 e 360×640, com dois telemóveis e uma imitação local da Firebase: 137 verificações novas e as 160 da v2.0, todas OK, sem erros JS.
    - Compras: adicionar, comprar, apagar, apagar comprados, copiar e WhatsApp (endereço verificado, não enviado). Passar de Faltas. O outro telemóvel vê tudo; nó `purchases` separado.
    - Compras offline: o outro telemóvel adiciona e marca sem rede, fecha e reabre a app, e tudo é enviado quando a rede volta.
    - Contagem: Tudo e por secção, pela ordem da secção, a partir da página da secção. Continuar depois de recarregar a página (como fechar a app). Diferença aplicada e registada.
    - Histórico: 160 combinações de período × tipo × utilizador × pesquisa, com entradas de ontem, de há 3 e de há 10 dias. O número de linhas e o resumo batem certo.
    - Ordenação: mover, concluir, voltar, persistência depois de fechar, ordem no outro telemóvel e na Firebase. Produto novo no fim; secção sem ordem em ordem alfabética.
    - Dados antigos (sem compras, ordem nem contagem) abrem bem.
    - Sem texto cortado (incluindo o texto das listas de filtro), sobreposições nem deslocamento horizontal.
    - Continuam a funcionar: utilizadores, avisos, faltas, receção, partilha e secções.
  - **Não testado:** num telemóvel real (fechar a app de verdade, botão voltar, copiar, abrir o WhatsApp) e com a Firebase verdadeira.
- **Problemas:**
  - Dentro da secção, a ordem sem ordem guardada passou a alfabética, como pedido. Antes era pelos "mais mexidos", não alfabética.
  - Telemóveis na v2.0 ignoram a lista de compras e a ordem. Se um deles usar "Repor dados de exemplo" ou criar a partilha, a ordem perde-se.
  - O histórico guarda só as últimas 200 alterações, por isso "7 dias" pode não mostrar tudo numa semana muito movimentada.
- **Próximos passos:**
  1. Atualizar todos os telemóveis para a v2.1 no mesmo dia.
  2. Ordenar à mão as secções mais usadas e experimentar "Contar esta secção".
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 11:30 — Stock em dois níveis (v2.0)
- **Pedido:** só interface. Página Stock com grelha de cartões por secção; pesquisa geral; página de cada secção com voltar, título, pesquisa e lista; novo produto com a secção já escolhida; mensagem na secção vazia; tirar secções recolhíveis e "Favoritos"; lembrar a secção aberta.
- **Feito:**
  - Grelha 2 por linha (também a 360 px). Cada cartão tem fundo suave da cor da secção, nome em Poppins 19 px na cor forte e círculo com o número de produtos no canto (mostra também 0).
  - "Gerir secções" e "+ Novo produto" mantêm-se na grelha. Dentro da secção, "Gerir secções" esconde-se.
  - Pesquisa no topo: com texto mostra os produtos de todas as secções, cada um com a secção pequena e na sua cor; sem texto volta à grelha.
  - Página da secção: "‹ Stock", título na cor da secção, pesquisa "Pesquisar em <secção>" e a mesma linha de produto (toque abre a folha).
  - Botão voltar do Android: primeiro limpa a pesquisa, depois volta à grelha, depois ao Início. Tocar outra vez em "Stock" dentro de uma secção também volta à grelha.
  - Novo produto dentro de uma secção abre com essa secção escolhida; na grelha, como antes (a primeira).
  - Secção vazia: "Ainda não há produtos aqui. Toque em “+ Novo produto” para adicionar o primeiro."
  - Removidas as secções recolhíveis (e a memória delas no telemóvel). Não existia uma lista "Favoritos" no código; a ordem dentro da secção continua a dos mais mexidos.
  - A secção aberta fica lembrada ao mudar de separador, só enquanto a app está aberta. Se a secção aberta for apagada (aqui ou noutro telemóvel), volta à grelha.
  - Retirado o segundo "×" que o navegador punha na pesquisa.
  - Versão 2.0 (versionCode 11), assinada com `keystore/stock.jks`; a palavra-passe passou só por `STOCK_KS_PASS`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `dist/StockSnackBar.apk`.
- **Estado:**
  - Compila. O certificado é igual ao da v1.9 e o `index.html` dentro do APK é o testado.
  - Teste em Chromium a 390×844 e 360×640, com dois telemóveis e uma imitação local da Firebase: 160 verificações OK, sem erros JS.
    - Grelha com 9 secções, 9 cores, 2 por linha, fonte de títulos.
    - Contagens iguais aos produtos de cada secção (abrindo cada uma), a atualizar ao criar e mover produtos, e iguais no outro telemóvel.
    - Abrir e voltar com "‹ Stock" e com o voltar do Android (simulado com `handleBack`).
    - Pesquisa geral (várias secções, com nome da secção) e dentro da secção (só dessa secção; sem resultados).
    - Novo produto com a secção escolhida; secção vazia com mensagem.
    - Gerir secções: criar, renomear, mudar a cor e apagar refletem-se na grelha e no outro telemóvel.
    - Sem texto cortado, sobreposições nem deslocamento horizontal. O último cartão fica acima da barra de baixo.
    - Partilha, histórico, avisos, faltas, contagem e receção continuam a funcionar.
  - Contraste: nome sobre o fundo suave ≥ 4,3:1 (texto grande, mínimo 3:1); número branco sobre o círculo ≥ 4,9:1.
  - **Não testado:** num telemóvel real (botão voltar verdadeiro, toque, teclado) e com a Firebase verdadeira.
- **Problemas:**
  - Primeira versão: o nome dos cartões a 18 px ficava abaixo do contraste pedido para texto normal (4,3:1). Passou a 19 px negrito, que conta como texto grande.
  - Usei por engano o id `secTitle`, que já existia em "Gerir secções". Corrigido para `secPageTitle` antes dos testes.
- **Próximos passos:**
  1. Atualizar todos os telemóveis para a v2.0 e experimentar o botão voltar do Android.
  2. Ver se a ordem das secções na grelha é a desejada (setas ↑↓ em "Gerir secções").
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 11:20 — APK v1.9 enviado na conversa
- **Pedido:** mostrar o APK na conversa para o descarregar.
- **Feito:** enviado `dist/StockSnackBar.apk` (v1.9, versionCode 10) como anexo. O projeto não mudou.
- **Ficheiros:** nenhum alterado (só este relatório).
- **Estado:** é o mesmo APK já verificado: mesmo certificado da v1.8, compila. Não testado num telemóvel real.
- **Problemas:** nenhum.
- **Próximos passos:**
  1. Instalar por cima da v1.8 em todos os telemóveis.
  2. Confirmar num telemóvel real os cartões e a escolha da unidade.
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 11:15 — Cartões sem legenda e unidade por botões (v1.9)
- **Pedido:** só interface. Tirar a legenda dos cartões Avisos/Faltas. Trocar o campo de unidade por botões (un, caixa, saco, pacote, garrafa, lata, kg, L, Outra), com "Outra" a abrir texto até 12 caracteres.
- **Feito:**
  - Cartões: só título, número laranja no canto (escondido quando é 0) e seta. O título fica centrado na vertical.
  - Unidade em "Novo produto" e em "Mais opções" do produto: botões numa linha que quebra quando é preciso. A unidade marcada fica a laranja.
  - "Outra" mostra uma caixa de texto (máx. 12). Uma unidade guardada fora da lista abre em "Outra", com o texto preenchido. Se o texto ficar vazio, grava "un". Texto igual a uma da lista (ex.: "Caixas") fica com o nome da lista ("caixa").
  - Por defeito: "un". A unidade continua pequena ao lado da quantidade.
  - Para isto funcionar, a leitura dos dados passou a aceitar unidades fora de un/kg/L (antes eram trocadas por "un"). O texto colado em "Partilhar" também as aceita. A estrutura dos dados e a partilha não mudaram.
  - Versão 1.9 (versionCode 10), assinada com `keystore/stock.jks`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `dist/StockSnackBar.apk`.
- **Estado:**
  - Compila. O certificado é igual ao da v1.8, por isso dá para atualizar por cima.
  - Teste em Chromium a 390×844 e 360×640, com dois telemóveis e uma imitação local da Firebase: 178 verificações OK, sem erros JS.
    - Cartões: sem legenda; número escondido com 0, aparece com 1 aviso e 2 faltas, e chega ao outro telemóvel.
    - Criar com cada uma das 8 unidades, com "Outra" com texto ("dúzia"), vazia (→ un), "Caixas" (→ caixa) e com 12 caracteres.
    - Editar para cada unidade e voltar, de personalizada para kg, para "Outra" com texto e para "Outra" vazia (→ un).
    - Os botões cabem no ecrã, sem deslocamento horizontal.
    - Unidades antigas (un, kg, L, sem unidade → un) e uma unidade personalizada vinda da partilha aparecem bem.
    - Partilha (as unidades chegam à base de dados e ao outro telemóvel), botão + e histórico ("unidade dúzia → kg") continuam a funcionar.
  - **Não testado:** num telemóvel real e com a Firebase verdadeira.
- **Problemas:**
  - A assinatura usou `keystore/keystore.properties` (fora do git), como escolheu, em vez de `STOCK_KS_PASS`.
  - Telemóveis ainda na v1.8 trocam as unidades novas por "un" no ecrã até serem atualizados. No servidor, a unidade só é reescrita se esse telemóvel editar o produto.
- **Próximos passos:**
  1. Atualizar todos os telemóveis para a v1.9 ao mesmo tempo.
  2. Rever a unidade dos produtos existentes (ex.: garrafas, latas).
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 10:53 — Identidade caffé c@ffé, fontes, cores por secção, Bar e Mercearia
- **Pedido:**
  - Nome "Stock caffé c@ffé" e "caffé c@ffé" como nome da app, com ícone da gota.
  - Cartões com o número por resolver, em vez das novidades.
  - Fontes de destaque e de texto, incluídas na app.
  - Cores por secção, escolhidas em "Gerir secções".
  - Secções Bar e Mercearia.
  - O "+" não pode tapar conteúdo.
- **Feito:**
  - **Identidade:**
    - Cabeçalho e `<title>` "Stock caffé c@ffé". `app_name` = "caffé c@ffé" (confirmado no APK: `application-label:'caffé c@ffé'`).
    - Ícone adaptativo: fundo `#1B1B1B`, gota laranja com anel branco e círculo preto, dentro da zona segura (54 de 108 dp). Tem versão monocromática para os ícones temáticos do Android 13+.
    - A marca do cabeçalho já era a gota. O logotipo do Início e do ecrã de utilizadores mantém-se ("caffé" pequeno sobre "c@ffé" grande).
  - **Cartões do Início:**
    - Sem o ícone escuro.
    - Círculo laranja no canto superior direito com o número de Avisos/Faltas por resolver, escondido quando é 0.
    - Título em Poppins, uma linha discreta ("Sem avisos", "Não falta nada" ou o texto do mais recente) e a seta.
    - Removidos o número e o ponto de "novidades", incluindo o ponto na barra de baixo.
  - **Tipografia:**
    - Poppins 600/700 nos títulos (cabeçalho, secções, páginas, cartões, janelas) e Inter 400–700 no texto.
    - Ficheiros woff2 (subconjunto latino, cobre os acentos do português) em `app/src/main/assets/fonts`, com as licenças OFL, carregados por `@font-face` local. Foram obtidos do registo npm (@fontsource, v5.1.0).
    - Nome da secção em maiúsculas com espaçamento, produto em peso 500, quantidade em 700 e maior.
  - **Cores por secção:**
    - Paleta de 9: laranja, âmbar, verde, verde-escuro, azul, roxo, rosa, castanho, cinzento-azulado. Cada cor tem um tom forte com contraste ≥ 4,5:1 sobre branco (o laranja escurecido para `#BA520A` por isso) e uma variante suave para fundos.
    - Cada secção tem uma barra e um título na sua cor, o número num fundo suave, um friso de 4 px à esquerda dos produtos e a quantidade na mesma cor. A folha do produto mostra a secção na sua cor.
    - "Gerir secções": círculo da cor ao lado do nome; tocar abre as 9 cores. A escolha grava `sections/<id>/color`, um campo opcional.
    - Secções sem cor guardada usam a de origem pelo nome; outras usam uma cor calculada a partir do id, sempre a mesma.
    - Cores de origem: Padaria âmbar, Pastelaria rosa, Congelador azul, Utensílios cinzento-azulado, Limpeza verde, Frigorífico roxo, Armazém castanho, Bar laranja, Mercearia verde-escuro.
  - **Bar e Mercearia:**
    - Fazem parte das secções de origem.
    - Telemóveis e partilhas já existentes acrescentam-nas uma só vez, só se ainda não existirem. A marca `lojas/<código>/flags/secoesBarMercearia` vai no mesmo PATCH, para nenhum telemóvel as recriar depois de alguém as apagar. Sem partilha, a marca fica no telemóvel.
    - O exemplo põe bebidas no Bar e café, açúcar e chocolate na Mercearia.
  - **"+":**
    - Passa a "+ Novo produto", encostado à direita numa faixa da barra de baixo, só na página Stock. Nunca fica por cima da lista, e a lista tem espaço em baixo para acabar acima da barra.
    - Com o teclado aberto (o ecrã encolhe mais de 25% com um campo ativo), a barra de baixo esconde-se e volta quando o teclado fecha.
    - No Stock, as mensagens rápidas aparecem acima do botão.
  - Versão 1.8 (versionCode 9), assinada com `keystore/stock.jks`; a palavra-passe só via `STOCK_KS_PASS`.
- **Ficheiros:**
  - Alterados: `app/src/main/assets/index.html`, `app/src/main/res/values/strings.xml`, `app/src/main/res/drawable/ic_coffee.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk`.
  - Criados: `app/src/main/assets/fonts/` (6 woff2 e 2 licenças), `app/src/main/res/drawable/ic_coffee_mono.xml`.
- **Estado:**
  - Compila. Assinatura verificada: o certificado é o mesmo. As fontes estão dentro do APK.
  - Teste em Chromium a 390×844 e 360×640 (210 verificações, todas OK três vezes, sem erros JS):
    - Título do cabeçalho.
    - Cartões: círculo laranja no canto, só quando é maior que 0, a contar os itens por resolver; atualizam com dois telemóveis (resolvido noutro telemóvel → sem número).
    - 9 secções sem duplicados e com 9 cores diferentes; Padaria em âmbar no título, barra, friso e quantidade.
    - Mudar a cor (guardada e partilhada com o outro telemóvel); secções antigas sem cor recebem a de origem.
    - Bar e Mercearia acrescentadas uma vez numa partilha antiga; um Bar apagado não volta quando outro telemóvel se liga.
    - Poppins e Inter carregadas, sem nenhum pedido a sites externos.
    - "+ Novo produto" dentro da barra de baixo; o fim da lista não fica tapado.
    - Teclado simulado: barra escondida e pesquisa visível; barra volta com o campo ainda ativo.
    - Sem texto cortado.
    - Utilizadores, histórico, avisos, faltas, contagem, receção, partilha, offline e dados antigos continuam a passar.
  - Ícone: vista prévia do desenho com as máscaras redonda e quadrada.
  - **Não testado:** num telemóvel real (ícone no launcher, teclado verdadeiro, barras do sistema) e com a Firebase verdadeira.
- **Problemas:**
  - O pedido dizia "só interface; não mexer em dados", mas também pedia a cor guardada em `sections` e Bar/Mercearia na lista partilhada. Por isso foram acrescentados o campo opcional `color` e o nó `flags/secoesBarMercearia`; nada existente foi alterado. As regras da Firebase não mudaram.
  - A paleta tem 9 cores em vez de 8: Mercearia pedia "verde-escuro" e Limpeza "verde", e ficariam iguais.
  - O "+" deixou de ser um círculo flutuante: passou para a barra de baixo, para nunca tapar a quantidade de nenhum produto.
  - Corrigido durante o trabalho: a primeira versão escondia a barra de baixo sempre que um campo tinha o foco, e a barra não voltava depois de fechar o teclado com o botão voltar.
- **Próximos passos:**
  1. Atualizar todos os telemóveis e ver o ícone novo no ecrã inicial do Android.
  2. Escolher em "Gerir secções" as cores finais de cada secção.
  3. Mudar a palavra-passe da chave (pendente).

---

## 2026-11-27 10:29 — Logotipo no Início, app branca, sem secções antigas
- **Pedido:**
  - Página inicial com o logotipo do café (fotografia da parede) e as caixas Avisos e Faltas, uma abaixo da outra, com notificação; tocar nelas abre a lista.
  - Apagar as secções Pão, Detergentes, Lácteos e Comida.
  - App mais limpa: branca, com preto e laranja só no cabeçalho e nos detalhes.
- **Feito:**
  - **Logotipo:** "caffé c@ffé" redesenhado em SVG a partir da fotografia (duas gotas preta/laranja, "@" laranja). O tipo de letra é aproximado, com Century Gothic, Futura ou o do sistema. Aparece no Início e no ecrã de escolha de utilizador. A gota laranja pequena é a marca do cabeçalho.
  - **Início:**
    - Logotipo e dois cartões, um abaixo do outro: Avisos e Faltas.
    - Cada cartão mostra quantos estão por resolver ("Sem avisos" / "Não falta nada" quando não há), o texto do mais recente e um número laranja com as novidades de outros utilizadores ainda não vistas.
    - Tocar abre a página Avisos ou Faltas, com "‹ Início" para voltar; o botão voltar do Android também volta.
    - O controlo segmentado da v1.6 saiu. A barra em baixo continua: Início · Stock · Histórico · Mais.
  - **Cores:** fundo, cartões, janelas e barra de baixo a branco; cabeçalho preto `#1b1b1b`; laranja `#f07b1d`, tirado do logotipo, nos botões principais, item ativo, "+" e notificações; vermelho só para apagar.
  - **Android:** barra de estado preta (junto ao cabeçalho) com ícones claros. Barra de navegação do sistema branca com ícones escuros (Android 8.1+; no 8.0 fica preta). Tema claro e fundo da janela branco.
  - **Secções antigas:** Pão, Detergentes, Lácteos e Comida são apagadas uma vez por partilha (e uma vez sem partilha), num só PATCH.
    - Os produtos passam para a secção nova do exemplo, se o nome for conhecido (ex.: Tampas → Utensílios, Fiambre → Frigorífico); senão Pão→Padaria, Detergentes→Limpeza, Lácteos→Frigorífico, Comida→Armazém.
    - Nenhum produto é apagado. Com a partilha ligada, a mudança espera pelos dados da Firebase.
    - Depois disso, a equipa pode voltar a criar uma secção com um desses nomes, se quiser.
  - Faltas antigas sem `uid` reconhecem o autor pelo nome, para não aparecerem como novidade a quem as escreveu.
  - Versão 1.7 (versionCode 8), assinada com `keystore/stock.jks`; a palavra-passe só via `STOCK_KS_PASS`.
- **Ficheiros:** alterados `app/src/main/assets/index.html`, `app/src/main/java/pt/snackbar/stock/MainActivity.java`, `app/src/main/res/values/themes.xml`, `app/build.gradle`, `README.md` e `dist/StockSnackBar.apk`; criado `app/src/main/res/values-v27/themes.xml`.
- **Estado:**
  - Compila. Assinatura verificada: o certificado é o mesmo.
  - Teste em Chromium (172 verificações, todas OK três vezes seguidas, sem erros JS):
    - Tudo o que já era testado na v1.6.
    - Início com logotipo e cartões um abaixo do outro; contagens, texto e números de novidade.
    - Cartões abrem as páginas e "‹ Início" volta.
    - Cores medidas: corpo, cartões e barra brancos; cabeçalho preto.
    - Dados locais antigos: Lácteos/Comida desaparecem, produtos movidos e não voltam após recarregar.
    - Partilha antiga com Pão/Comida/Lácteos/Detergentes: a Firebase fica com 7 secções e os 6 produtos movidos para o sítio certo, sem perder nada (o mínimo antigo mantém-se).
    - Duas medidas (390×844 e 360×640): sem texto cortado nem elementos tapados.
  - **Não testado:** num telemóvel real (cores das barras do sistema, aspeto do logotipo com o tipo de letra do telemóvel) e com a Firebase verdadeira.
- **Problemas:**
  - Encontrado e corrigido durante o trabalho: uma substituição apagou o código de publicar e gerir avisos; foi recuperado do git e coberto pelos testes.
  - O logotipo é uma reprodução aproximada; com o ficheiro original (SVG ou PNG), fica exato.
  - O título do cabeçalho continua "Stock do snack bar".
- **Próximos passos:**
  1. Atualizar todos os telemóveis e confirmar onde ficaram os produtos das secções antigas.
  2. Enviar o ficheiro original do logotipo, se existir, para substituir a reprodução.
  3. Decidir se o título do cabeçalho passa a ser o nome do café e se o ícone da app passa a ser a gota do logotipo.

---

## 2026-11-27 10:03 — Avisos e Faltas, Stock por secções, folha do produto
- **Pedido:**
  - Início com separadores Avisos e Faltas.
  - Stock por secções geridas pela equipa.
  - Linhas só com nome e quantidade, com uma folha do produto para as alterações.
  - Pesquisa por produto no histórico.
  - O pedido dizia substituir "o anterior ainda não executado". A v1.5 já tinha sido entregue, por isso este trabalho foi feito por cima dela.
- **Feito:**
  - **Início (separador "Início" na barra):**
    - Controlo segmentado Avisos | Faltas, com o ativo a laranja e um ponto laranja de novidades em cada um. O ponto da barra aparece se houver novidades em qualquer dos dois.
    - Avisos sem alterações.
  - **Faltas:**
    - Campo "O que falta?" (80 caracteres, com contador) e "Adicionar". Lista do mais recente para o mais antigo, com quem e hora.
    - "Já temos" passa o item para "Resolvidas", recolhida por defeito; "Volta a faltar" desfaz. Apagar e "Limpar resolvidas" pedem dois toques.
    - Máximo de 100: saem primeiro as resolvidas mais antigas.
    - Reutiliza `lojas/<código>/shopping` com o mesmo formato (text, done, ts, user; acrescenta `uid` opcional para o ponto de novidades). Escritas de um só item, fila offline.
    - "Lista de compras" saiu do menu Mais; os itens antigos aparecem nas Faltas sem migração de dados.
  - **Stock:**
    - Pesquisa no topo.
    - Secções por defeito: Padaria, Pastelaria, Congelador, Utensílios, Limpeza, Frigorífico, Armazém. Cada uma é recolhível, com nome e número de produtos; as fechadas ficam guardadas no telemóvel. A pesquisa abre as secções e mostra só as que têm resultados.
    - Famílias antigas dos produtos (Pão, Comida, Lácteos…) tornam-se secções extra, criadas automaticamente. Nada é apagado.
    - Secções partilhadas em `lojas/<código>/sections/<id>` = {name, order}. O id vem do nome, por isso dois telemóveis a criar a mesma secção escrevem no mesmo nó; duplicados com o mesmo nome são limpos.
  - **Gerir secções:**
    - Criar (sem nomes repetidos).
    - Renomear: muda a secção e os produtos dela num só PATCH.
    - Subir/descer: reescreve a ordem de todas num só PATCH.
    - Apagar uma secção vazia: dois toques.
    - Apagar uma secção com produtos: pergunta para onde os mover, depois "Mover e apagar" com dois toques, num só PATCH.
  - **Botão flutuante "+":** novo produto com nome, secção, quantidade inicial e unidade. Fica no histórico como "Novo".
  - **Linhas:** só nome e quantidade (número grande, unidade pequena ao lado). Sem botões, notas nem ícones.
  - **Folha do produto (ao tocar numa linha):**
    - Nome, secção e quantidade grande. − e + de 64 px; campo numérico com "Guardar" (grava o valor exato).
    - "Mais opções": nome, unidade, secção, nota e apagar com dois toques. A edição grava só esses campos, sem tocar na quantidade, para não apagar um + feito noutro telemóvel ao mesmo tempo.
    - Se outro telemóvel mudar o produto, a folha aberta atualiza; se o apagar, a folha fecha com um aviso.
  - **Histórico:** pesquisa por produto e filtro por utilizador. Apagar um produto mantém as entradas antigas.
  - "Repor dados de exemplo" usa as novas secções. O texto de enviar/receber passa a usar a secção na 1.ª coluna; receção e contagem seguem a ordem das secções.
  - Cabeçalho: "N produtos".
  - Versão 1.6 (versionCode 7). Assinada com `keystore/stock.jks`; a palavra-passe só via `STOCK_KS_PASS`.
- **Ficheiros:** `app/src/main/assets/index.html`, `app/build.gradle`, `README.md`, `dist/StockSnackBar.apk` (alterados).
- **Estado:**
  - Compila. Assinatura verificada: o certificado é o mesmo.
  - Novo teste em Chromium (151 verificações, todas OK, sem erros JS):
    - **Dados antigos v1.5:** famílias antigas viram secções extra com os produtos visíveis; `min` e nota preservados; lista de compras antiga aparece nas Faltas.
    - **Ecrãs a 390×844 e 360×640:**
      - utilizadores (limite 3);
      - Avisos;
      - Faltas: adicionar, limite 80, "Já temos", apagar e limpar com dois toques;
      - secções: ordem, contagens, recolher lembrado após recarregar, pesquisa;
      - linhas só com nome e quantidade;
      - novo produto (incluindo nome repetido);
      - folha: − e +, valor escrito, número inválido, editar nome, secção e unidade, apagar;
      - histórico com pesquisa e filtro;
      - gerir secções: criar, repetida, reordenar, renomear com produtos, apagar vazia, apagar com mover;
      - receção, contagem, texto, repor exemplo, voltar;
      - sem texto cortado em nenhuma página ou janela, e o fim da lista não fica tapado pela barra nem pelo "+".
    - **Dois telemóveis simulados:**
      - secções criadas na Firebase;
      - utilizadores, Faltas e pontos de novidade partilhados;
      - escritas simultâneas em faltas diferentes;
      - offline;
      - secção nova, reordenada e renomeada (com os produtos) vista no outro telemóvel;
      - + simultâneo nos dois soma;
      - folha aberta atualiza;
      - histórico partilhado;
      - limite de 100 local e na Firebase.
  - Os testes anteriores (v1.5) usavam os botões − e + nas linhas, que já não existem. Os fluxos principais foram cobertos no teste novo. O limite de 50 avisos e o conflito apagar/fixar não foram repetidos, porque esse código não mudou.
  - **Não testado:** num telemóvel real e com a Firebase verdadeira.
- **Problemas:**
  - Encontrado e corrigido: o botão "Renomear" das secções dava erro JS (procurava o campo numa linha já redesenhada).
  - O botão "+" flutuante tapa a linha que lhe fica por baixo enquanto se desliza, como qualquer botão flutuante. No fim da lista nada fica tapado.
  - Se alguém apagar todas as secções, as 7 de origem voltam a ser criadas.
  - Telemóveis com v1.5 ou anterior ligados à partilha veem as secções novas como "Comida" e podem mudá-las ao editar. Todos devem atualizar.
- **Próximos passos:**
  1. Atualizar todos os telemóveis para a v1.6 e verificar as secções de produtos antigos.
  2. Confirmar no telemóvel que o teclado numérico abre na folha do produto e não tapa o "Guardar".
  3. Mudar a palavra-passe da chave (pendente).

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
