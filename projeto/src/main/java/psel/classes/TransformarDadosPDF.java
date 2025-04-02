package psel.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import psel.classes.utils.CompactarArquivo;
import psel.classes.utils.Download;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;



public class TransformarDadosPDF {

    private String nomeArquivo = "../anexos_csv/dados_extraidos.csv";

    private FileWriter  writer; 

    private static Path pastaArquivos = Paths.get("../anexos_csv");
    private static Path zipPath = Paths.get("../anexos_csv/Teste_Natalia_santos.zip");
    
    public TransformarDadosPDF() {
    }

    public void transformarDados() throws  IOException{
        Download download = new Download();
        download.criarDiretorio(pastaArquivos);

        writer = new FileWriter(nomeArquivo);
       extrairDados();
        writer.close();
        CompactarArquivo compactar = new CompactarArquivo();
        compactar.compactarArquivosZip(pastaArquivos, zipPath);

        

        
        
        
    }

    private void extrairDados() throws  IOException {
        escreverCabecalho();

        File pdf = new File("../anexos_ans/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf");
        PDDocument documentoPDF = PDDocument.load(pdf);

        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

        PageIterator pi = new ObjectExtractor(documentoPDF).extract();

        while(pi.hasNext()){
            Page pagina = pi.next();

            List<Table> tabela = sea.extract(pagina);

            String texto = null;
            for (Table tabelas : tabela) {
                
                List<List<RectangularTextContainer>> linhas = tabelas.getRows();
            
                for (int linha=1; linha<linhas.size(); linha++) {
                    
                    StringBuilder sb = new StringBuilder();
                    
                    List<RectangularTextContainer> celulas = linhas.get(linha);
                    for (RectangularTextContainer conteudo : celulas) {
                        
                        
                        texto = conteudo.getText().replace("\r", " ");
                        if (texto.isEmpty()) {
                            sb.append("\"\", ");
                        }else {
                            sb.append("\"").append(texto).append("\", ");
                        }
                    }
                    salvarTexto(sb.toString() + '\n');
                }
            }
        }
        System.out.println("Extração e arquivo gravado com sucesso!");
        documentoPDF.close();
    }
    

    private void salvarTexto(String texto) throws IOException{
        writer.write(texto);
    }

    private void escreverCabecalho() throws  IOException{
        String texto = "PROCEDIMENTO, RN(alteração), VIGÊNCIA, Seg. Odontológica, Seg. Ambulatorial, HCO, HSO, REF, PAC, DUT, SUBGRUPO, GRUPO, CAPÍTULO,\n";
        writer.write(texto);
    }

    private void compactarCSV(){
        CompactarArquivo compactar = new CompactarArquivo();
        compactar.compactarArquivosZip(pastaArquivos, zipPath);

    }


}
