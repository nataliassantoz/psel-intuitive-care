package psel.classes;

import java.nio.file.Path;
import java.nio.file.Paths;

import psel.classes.utils.CompactarPDF;
import psel.classes.utils.DownloadPDF;

public class Main {

    private static Path pastaArquivos = Paths.get("anexos_ans");
    private static Path zipPath = Paths.get("compactado.zip");
    
    public static void main(String[] args) throws Exception {

        DownloadPDF downloadPDF = new DownloadPDF();
        downloadPDF.criarDiretorio(pastaArquivos);
        downloadPDF.download(pastaArquivos, zipPath);
        CompactarPDF compactarArquivos = new CompactarPDF();
        compactarArquivos.compactarArquivos(pastaArquivos, zipPath);
        
    }
}