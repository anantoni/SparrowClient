/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.structs;

/**
 *
 * @author jim
 */
public class MathUtilities {
    
    public static boolean isPowerOfTwo(int n){
        if( (n & -n) == n) return true;
        else return false;
       }
    
    public static double log2(int n){
        return (Math.log(n) / Math.log(2));
    }
    
    public static double log2(double n){
        return (Math.log(n) / Math.log(2));
    }
    
}
