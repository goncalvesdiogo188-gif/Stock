# Stock do snack bar

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
