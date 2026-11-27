# Stock caffé c@ffé

App Android (WebView) para controlar o stock de um snack bar. Funciona offline; os dados ficam guardados no telemóvel.

- **APK pronto a instalar:** [`dist/StockSnackBar.apk`](dist/StockSnackBar.apk) (Android 8 ou superior; permitir "instalar apps desconhecidas").
- **A app inteira:** um único ficheiro HTML em `app/src/main/assets/index.html` (também abre num browser).
- Sem contas. Funciona offline; o WhatsApp é aberto pela ligação `wa.me`.

## Stock igual em todos os telemóveis

Opcional, em **"Partilhar entre telemóveis"** (fundo da lista). Usa uma base de dados Firebase Realtime Database gratuita, criada uma vez:

1. Em [console.firebase.google.com](https://console.firebase.google.com), **Criar projeto**.
2. **Realtime Database → Criar base de dados**, localização Bélgica (europe-west1), modo bloqueado.
3. Separador **Regras**, colar e **Publicar**:
   ```json
   {
     "rules": {
       ".read": false,
       ".write": false,
       "lojas": { "$codigo": { ".read": true, ".write": true } }
     }
   }
   ```
4. Copiar o endereço da base de dados (separador **Dados**) e colá-lo na app no primeiro telemóvel → **Ligar**.
5. **Enviar ligação pelo WhatsApp** aos outros telemóveis; em cada um, colar a mensagem em "Partilhar entre telemóveis" → **Ligar** (dois toques).

Cada alteração é enviada como uma operação (os + e − são somas no servidor, por isso toques ao mesmo tempo em telemóveis diferentes não se perdem). Sem rede, as alterações ficam em fila e são enviadas depois; a app verifica alterações dos outros a cada 4 segundos. A ligação contém um código aleatório: quem a tiver pode ver e alterar o stock.

## Compilar

Requer JDK 17+ e Android SDK (platform 35):

```
echo "sdk.dir=/caminho/para/android-sdk" > local.properties
./gradlew assembleRelease
```

O APK sai em `app/build/outputs/apk/release/`. Se existir `keystore/stock.jks` (alias `stock`), é usado para assinar; caso contrário usa a chave de debug. Para atualizar a app sem a desinstalar é preciso assinar sempre com a mesma chave.

A palavra-passe da chave **nunca** vai para o git: defina a variável de ambiente `STOCK_KS_PASS` ou crie `keystore/keystore.properties` com a linha `storePassword=...` (a pasta `keystore/` está no `.gitignore`).

## Utilizadores e histórico

Até 3 utilizadores (só nome, sem palavra-passe), escolhidos num ecrã inicial; o telemóvel lembra o último. Cada alteração de stock (+/−, editar, novo, apagar, repor exemplo, texto colado) fica no **Histórico** com utilizador, material, quantidade antes/depois, data e hora (últimas 200). Com a partilha ligada, utilizadores e histórico ficam em `lojas/<código>/users` e `lojas/<código>/history`, gravados no mesmo pedido que a alteração do stock.

## Páginas

- **Início** (abre aqui): logotipo do café e dois cartões, um abaixo do outro, **Avisos** e **Faltas**, com o número laranja de itens por resolver. Tocar abre a página respetiva.
  - **Avisos**: quadro da equipa (até 300 caracteres), fixar, "Resolvido", apagar em dois toques, máximo 50. Partilhados em `lojas/<código>/notices`.
  - **Faltas**: o que falta (até 80 caracteres), "Já temos", apagar, "Limpar resolvidas", máximo 100. Partilhadas em `lojas/<código>/shopping`.
- **Stock**: grelha de cartões por secção (cor, nome e número de produtos), pesquisa geral, "Gerir secções" (criar, renomear, reordenar, mudar a cor, apagar/mover) e "+ Novo produto". Tocar num cartão abre a secção: pesquisa na secção, lista (nome e quantidade; tocar abre a folha do produto), "Ordenar" (setas ▲ ▼; ordem no campo opcional `order` de cada produto; sem ordem, alfabética) e "Contar esta secção". Secções partilhadas em `lojas/<código>/sections`.
- **Lista de compras** (em Mais): lista manual, separada das Faltas (até 60 caracteres, comprado, apagar, "Apagar comprados", copiar, WhatsApp; máximo 100). Partilhada em `lojas/<código>/purchases`. Cada falta tem "Passar para compras".
- **Contagem rápida**: pergunta o âmbito (Tudo ou uma secção). O progresso fica guardado no telemóvel e, se a app fechar a meio, oferece "Continuar contagem".
- **Histórico**: atalhos Hoje / Ontem / 7 dias / Tudo, filtros por tipo e utilizador, pesquisa por produto e um resumo do filtro atual.
- **Mais**: lista de compras, receber mercadoria, contagem rápida, enviar/receber stock em texto, partilha, utilizadores e dados de exemplo.

Visual: fundo branco; preto e laranja (cores do logotipo) no cabeçalho e nos detalhes. Fontes locais (OFL) em `app/src/main/assets/fonts`: Poppins nos títulos e Inter no texto. Cada secção tem uma cor (paleta de 9, escolhida em "Gerir secções", campo opcional `color` em `lojas/<código>/sections`). Secções Bar e Mercearia acrescentadas uma vez (marca em `lojas/<código>/flags/secoesBarMercearia`). As secções antigas Pão, Detergentes, Lácteos e Comida foram retiradas na v1.7 (os produtos passaram para as secções novas).

A app não usa mínimos. Produtos antigos com `min` carregam normalmente (o valor é mantido, sem uso). Texto de enviar/receber: `Secção | Nome | quantidade unidade` (textos antigos com `| min N` continuam a ser aceites).
