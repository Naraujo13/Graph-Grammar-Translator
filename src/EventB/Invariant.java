/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventB;

/**
 *
 * @author Nícolas Oreques de Araujo
 */
public class Invariant {
    private String name;
    private String predicate;
    
    public Invariant(String name, String predicate){
        this.name = name;
        this.predicate = predicate;
    }
    
    public String getName(){
        return name;
    }
    
}
