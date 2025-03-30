package psel.classes.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompactarPDF {

    public CompactarPDF() {
    }

    public void compactarArquivos(Path pastaArquivos, Path zipPath) {

        try{
            DirectoryStream<Path> pdfs = Files.newDirectoryStream(pastaArquivos);
            OutputStream saida = Files.newOutputStream(zipPath);
            ZipOutputStream zip = new ZipOutputStream(saida);
            
            for(Path pdf : pdfs){

                byte[] conteudo = Files.readAllBytes(pdf);
                
                ZipEntry entrada = new ZipEntry(pdf.getFileName().toString());

                zip.putNextEntry(entrada);
                zip.write(conteudo);
                
                System.out.println("Adicionado ao ZIP: " + pdf.getFileName());
            }
            zip.closeEntry();
            zip.close(); 

            System.out.println("Compactação finalizada. Arquivo criado: " + zipPath.toAbsolutePath());
        }
        catch(IOException e ){
            System.err.println("Erro ao compactar arquivos: " + e.getMessage());
        }
    }
}
