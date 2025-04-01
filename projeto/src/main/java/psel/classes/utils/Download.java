package psel.classes.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Download {

    private String URLpdfs = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

    public Download() {
    }

    public void download(Path pastaArquivos, Path zipPath) throws Exception{
        HttpClient client = HttpClient.newHttpClient();

        Document doc = Jsoup.connect(URLpdfs)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*")
                        .get();
     
        Elements links = doc.select("a[href$=.pdf]");
        for (Element link : links) {

            String href = link.attr("href");  

            if (href.contains("Anexo_I") || href.contains("Anexo_II")) {
                System.out.println("Arquivo encontrado!"); 
                String representacaoURL = link.absUrl("href");
            
                URL url = null;
                
                if(representacaoURL.isEmpty()){
                    System.err.println("URL do PDF está vazia!");
                    continue;
                } 
                
                HttpRequest request = null;

                try{
                    url = new URL(representacaoURL);

                    request = HttpRequest.newBuilder()
                            .uri(url.toURI())
                            .GET()
                            .build();
                }
                catch(MalformedURLException | URISyntaxException e) {
                    System.err.println("URL malformada: " + representacaoURL);
                    continue;
                }

                try{
                    HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                
                    URI uri = request.uri();
                    String path = uri.getPath();
                    
                    String nomeArquivo = Paths.get(path).getFileName().toString();
                    Path diretorio = pastaArquivos.resolve(nomeArquivo);

                    if(response.statusCode() == 200){
                        Files.write(diretorio, response.body());
                        
                        System.out.println("Arquivo baixado!");
                        System.out.println("Salvo como: " + diretorio.toAbsolutePath());

                    }
                    else if(response.statusCode() == 500){
                        System.err.print("erro do servidor: " + response.statusCode());
                        continue;
                    }
                }
                catch(IOException | InterruptedException e){
                    System.err.println("Erro na comunicação com o servidor!");
                }
            }
        }
    }

    public void criarDiretorio(Path diretorioPai) throws IOException {
    
        if (diretorioPai != null) {
            try {
                Files.createDirectories(diretorioPai);
            } catch( IOException e) {
                System.out.println("Erro ao criar diretorio de anexos!");
            }
        }
    }
}

