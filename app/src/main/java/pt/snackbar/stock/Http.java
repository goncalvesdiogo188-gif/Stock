package pt.snackbar.stock;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Pedidos HTTP simples para a API REST da Firebase Realtime Database. */
final class Http {
    private Http() {}

    /** Devolve {código, corpo}. Código 0 = sem ligação. */
    static Object[] request(String method, String url, String body) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(10000);
            c.setReadTimeout(15000);
            c.setUseCaches(false);
            // HttpURLConnection não suporta PATCH: a Firebase aceita POST com este cabeçalho.
            if ("PATCH".equals(method)) {
                c.setRequestMethod("POST");
                c.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                c.setRequestMethod(method);
            }
            if (body != null && !body.isEmpty() && !"GET".equals(method)) {
                byte[] b = body.getBytes(StandardCharsets.UTF_8);
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                c.setFixedLengthStreamingMode(b.length);
                try (OutputStream os = c.getOutputStream()) { os.write(b); }
            }
            int code = c.getResponseCode();
            InputStream in = code >= 400 ? c.getErrorStream() : c.getInputStream();
            String text = "";
            if (in != null) {
                try (InputStream is = in) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = is.read(buf)) > 0) out.write(buf, 0, n);
                    text = out.toString("UTF-8");
                }
            }
            return new Object[] { code, text };
        } catch (Exception e) {
            return new Object[] { 0, "" };
        } finally {
            if (c != null) c.disconnect();
        }
    }
}
