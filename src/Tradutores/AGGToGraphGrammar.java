/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tradutores;

import GraphGrammar.AttributeType;
import GraphGrammar.EdgeType;
import GraphGrammar.Grammar;
import GraphGrammar.Graph;
import GraphGrammar.NodeType;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicol
 */
public class AGGToGraphGrammar {
    
    public static void aggReader(String arquivo, Grammar grammar){
        //Hash Maps utilizados para definir nodos do grafo tipo com arestas
        Map <String, String> attNames;  //Associa ID ao nome lendo rótulos
        Map <String, String> attTypes; //Associa ID ao tipo lendo rótulos
        Map <String, String> nodeAtt;   //Hash map com ID do nodo e ID do att
        attNames = new HashMap <>();
        attTypes = new HashMap<>();
        nodeAtt = new HashMap<>();
      
        //Auxiliares
        String tokenAtual;      //token sendo analisado
        Scanner entrada;        //scanner usado na leitura do arquivo
        
        String attName, attID, attType, rotuloID;
        //Vetores de String Auxiliares para quebrar comandos
        String[] auxiliar; //Declara vector que servirá como auxiliar ao quebrar o comando
        String[] auxiliar2; //Auxiliar 2 Para quebrar substrings;
        String[] auxiliar3; //Auxiliar 3 para quebrar substrings
       
       try {
             entrada = new Scanner(new FileReader(arquivo)).useDelimiter("<");  //le e usa > como delimitador
             
        //Definição de Nodes ou Edges - inútil, pois é definido novamente no grafo tipo
        tokenAtual = entrada.next();
        while(!tokenAtual.contains("kind=\"TG\"")){ //itera até achar o início do grafo tipo
            if (tokenAtual.contains("/NodeType"))
                tokenAtual = entrada.next();
            if (tokenAtual.contains("NodeType")){   //se for nodo
                
                //Pega ID do nodo
                auxiliar = tokenAtual.split(" ");
                auxiliar2 = auxiliar[1].split("\"");
                rotuloID = auxiliar2[1];
                
                tokenAtual = entrada.next();
                if (!tokenAtual.contains("kind=\"TG\"")){ //e se tem atributo, salva o nome do atributo e seu ID tipo
                    if (tokenAtual.contains("AttrType")){
                        auxiliar = tokenAtual.split(" ");
                        //salva ID
                        auxiliar2 = auxiliar[1].split("\"");
                        attID = auxiliar2[1];
                        //salva nome
                        auxiliar2 = auxiliar[2].split("\"");
                        attName = auxiliar2[1];
                        
                        //salva Tipo
                        auxiliar2 = auxiliar[3].split("\"");
                        attType = auxiliar2[1];
                        
                        //adiciona no hashmap
                        attNames.put(attID, attName);
                        attTypes.put(attID, attType);
                        nodeAtt.put(rotuloID, attID);
                    }               
                }
            }
            if (!tokenAtual.contains("kind=\"TG\""))
                tokenAtual = entrada.next();                    //itera para o próximo comando
        }
        //Definição de Grafo Tipo, se existir
        //Definição de nodos e
        //Define HashMap que converte para os "Ids Tipo" (confusão do AGG)
        Map <String, String> types = new HashMap<>();
        NodeType newNodeType;
        String nodeTypeID;
        AttributeType newAttType;
        if (tokenAtual.contains("kind=\"TG\"")){
            tokenAtual = entrada.next();
            while (tokenAtual.contains("Node ID") || tokenAtual.contains("Layout") ||tokenAtual.contains("/Node")){        //cria hashmap para os tipos dentro do grafo tipo
                if (!tokenAtual.contains("Layout") && !tokenAtual.contains("/Node")){
                    auxiliar = tokenAtual.split(" ");       //dá split
                    auxiliar2 = auxiliar[1].split("\"");     //coloca ID no auxiliar 2
                    auxiliar3 = auxiliar[2].split("\"");      //coloca tipo no auxiliar 3
                    nodeTypeID = auxiliar3[1];
                    //Cria nodo passando tipo
                    newNodeType = new NodeType(nodeTypeID);
                    //Se nodo possuia atributos no rótulo
                    if (nodeAtt.containsKey(auxiliar3[1])){
                        //Crianova instancia de atributo contendo o tipo
                        newAttType = new AttributeType(attTypes.get(nodeAtt.get(auxiliar3[1])));
                        //Insere atributo no nodo
                        newNodeType.getAttributes().add(newAttType);
                    }
                   //Insere nodo no grafo de tadução
                    grammar.getTypeGraph().addTranslNode(auxiliar2[1], auxiliar3[1]); //associa ID ao tipo para tradução (adiconará internamente no grafo tipo a coleção de nodos, pois esta esta associada aos valoresdo hashmap)
                    grammar.getTypeGraph().getAllowedNodes().add(newNodeType);
                }
                tokenAtual = entrada.next();
            }            
        }
        //Definição de Arestas do Grafo Tipo
        EdgeType newEdgeType;
        while(tokenAtual.contains("Edge")){
            //Extrai tipo da aresta e salva como ID
            auxiliar = tokenAtual.split(" ");
            auxiliar2 = auxiliar[4].split("\"");
            newEdgeType = new EdgeType(auxiliar2[1]);
            
            //Extrai Source
            auxiliar2 = auxiliar[2].split("\"");
            newEdgeType.addSource(grammar.getTypeGraph().getTranslationNodes().get((auxiliar2[1])));
            
            //Extrai Target
            auxiliar2 = auxiliar[3].split("\"");
            newEdgeType.addTarget(grammar.getTypeGraph().translate(auxiliar2[1]));
            
            //Adiciona Aresta no ArrayList do Grafo Tipo
            grammar.getTypeGraph().getAllowedEdges().add(newEdgeType);
            
            //Descarta opções de Layout
            while(!tokenAtual.contains("/Edge")){
                tokenAtual = entrada.next(); 
            }
            tokenAtual = entrada.next();
        }
        
        //FIM GRAFO TIPO
        if (tokenAtual.contains("/Graph")){
            tokenAtual = entrada.next();
        }
        if (tokenAtual.contains("/Types")){
            tokenAtual = entrada.next();
        }
        
        //INICIO GRAFO HOST
        Graph newHost = new Graph("HOST");
        tokenAtual = entrada.next();
        //Nodos do HOST
        newHost.defineGraphNodes(tokenAtual, entrada, attNames, attTypes);     //Funcionando até aqui
        
        //Está saindo em I68 mais ou menos e indo para definição de arestas.
        //Scanner não ta atualizando. Altera dentro de nodes e quando retorna volta ao original. Pq?
        
        //Arestas do HOST
        newHost.defineGraphEdges(tokenAtual, entrada);
        
       tokenAtual = entrada.next(); //Descarta /Graph do HOST
       //FIM DO HOST
       grammar.setHost(newHost);
       
       //REGRAS
       //Mais vantajoso ID LHS -> ID RHS ou ID RHS -> ID LHS ? Até o momento RHS ou NAC -> LHS
       grammar.defineRules(tokenAtual, entrada, attNames, attTypes);
       
       // /GraphTrasnformationSystem
       if (tokenAtual.contains("/GraphTransformationSystem"))
           tokenAtual = entrada.next();
       else
           System.out.println("Erro em graph transformationSystem.\n");
       
       //FIM DO ARQUIVO ABAIXO
       
       //Itera /Document
         // if (tokenAtual.contains("/Document"))
          // tokenAtual = entrada.next();
        //Fecha o Sacnner
        entrada.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Grammar.class.getName()).log(Level.SEVERE, null, ex);
        }    
       
    }
    
    /** Main para testes de conversão do AGG
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        // TODO code application logic here
       String arquivo =  "PacmanAtributo.ggx";
        
       Grammar test = new Grammar("PacmanAtributo");
       aggReader(arquivo, test);
       test.printGrammar();
       System.out.println("Finished!");
    }
    
}