# Instruções para assistentes

## Relatório de progresso (obrigatório)
O ficheiro `RELATORIOS.md` na raiz tem de estar sempre em dia.

1. No final de CADA resposta, e sempre que houver qualquer alteração ao projeto, adicionar uma nova entrada no topo do `RELATORIOS.md` (a mais recente primeiro).
2. Logo a seguir, fazer commit e push para a branch em uso. Mensagem do commit: `relatório: <resumo curto>`. Se o push falhar, avisar o utilizador no fim da resposta.
3. O commit do próprio relatório não precisa de nova entrada.
4. Português europeu, curto e direto.
5. Nunca incluir passwords, tokens, chaves de API nem dados pessoais.
6. Atualizar também o bloco "Estado atual do projeto" (3 a 5 linhas) no topo, em cada entrada.

Formato de cada entrada:

```
## AAAA-MM-DD HH:MM — Título curto
- **Pedido:** ...
- **Feito:** ...
- **Ficheiros:** ...
- **Estado:** compila? testes passam? funciona? ("não testado" se não verificado)
- **Problemas:** ... (ou "nenhum")
- **Próximos passos:** 2 a 3 sugestões concretas
```

## Projeto
- App inteira: `app/src/main/assets/index.html` (HTML único, pt-PT).
- Compilar: `./gradlew assembleRelease` e copiar o APK para `dist/StockSnackBar.apk`. Aumentar `versionCode` em `app/build.gradle` a cada versão.
