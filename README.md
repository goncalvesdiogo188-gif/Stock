# Stock do snack bar

App Android (WebView) para controlar o stock de um snack bar. Funciona offline; os dados ficam guardados no telemóvel.

- **APK pronto a instalar:** [`dist/StockSnackBar.apk`](dist/StockSnackBar.apk) (Android 8 ou superior; permitir "instalar apps desconhecidas").
- **A app inteira:** um único ficheiro HTML em `app/src/main/assets/index.html` (também abre num browser).
- Sem contas, sem servidor, sem permissão de Internet. O WhatsApp é aberto pela ligação `wa.me`.

## Compilar

Requer JDK 17+ e Android SDK (platform 35):

```
echo "sdk.dir=/caminho/para/android-sdk" > local.properties
./gradlew assembleRelease
```

O APK sai em `app/build/outputs/apk/release/`. Se existir `keystore/stock.jks` (alias `stock`), é usado para assinar; caso contrário usa a chave de debug. Para atualizar a app sem a desinstalar é preciso assinar sempre com a mesma chave.
