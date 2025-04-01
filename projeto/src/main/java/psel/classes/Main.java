package psel.classes;

import java.nio.file.Path;
import java.nio.file.Paths;

import psel.classes.utils.CompactarArquivo;
import psel.classes.utils.Download;

public class Main {

    private static Path pastaArquivos = Paths.get("../anexos_ans");
    private static Path zipPath = Paths.get("../anexos_ans/compactado.zip");
    
    public static void main(String[] args) throws Exception {

        Download download = new Download();
        download.criarDiretorio(pastaArquivos);
        download.download(pastaArquivos, zipPath);
        CompactarArquivo compactarArquivos = new CompactarArquivo();
        compactarArquivos.compactarArquivosZip(pastaArquivos, zipPath);
        TransformarDadosPDF transformarDados = new TransformarDadosPDF();
        transformarDados.transformarDados();


        
    }
}