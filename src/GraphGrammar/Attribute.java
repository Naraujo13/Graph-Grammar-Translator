package GraphGrammar;

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nícolas Oreques de Araujo
 */
public class Attribute extends AttributeType {

    private String ID;
    private String value;

    public Attribute(String type, String ID, String name, String value){
        super(name, type);
        this.ID = ID;
        this.value = value;
        
        
    }
    
    /**
     * Método de acesso ao valor do atributo.
     * @return - valor do atributo em forma de string.
     */
    @Override
    public String getValue() {
        return value;
    }
    /**
     * Método de acesso ao ID do atributo.
     * @return - ID do atributo em forma de string.
     */
    @Override
    public String getID() {
        return ID;
    }

}
