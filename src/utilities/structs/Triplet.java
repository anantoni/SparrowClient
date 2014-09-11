/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.structs;

import java.util.Objects;

/**
 *
 * @author jim
 */
public class Triplet<T,U,V> {
    private T var1;
    private U var2;
    private V var3;

    /*constructor*/
    public Triplet(T a, U b, V c){
        this.var1 = a;
        this.var2 = b;
        this.var3 = c;
    }

    /*copy constructor*/
    public Triplet(Triplet<T,U,V> t){
        this.var1 = t.var1;
        this.var2 = t.var2;
        this.var3 = t.var3;
    }
    
    /*getter and setters*/
    public T getVar1() {
        return var1;
    }

    public void setVar1(T var1) {
        this.var1 = var1;
    }

    public U getVar2() {
        return var2;
    }

    public void setVar2(U var2) {
        this.var2 = var2;
    }

    public V getVar3() {
        return var3;
    }

    public void setVar3(V var3) {
        this.var3 = var3;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.var1);
        hash = 71 * hash + Objects.hashCode(this.var2);
        hash = 71 * hash + Objects.hashCode(this.var3);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
        if (!Objects.equals(this.var1, other.var1)) {
            return false;
        }
        if (!Objects.equals(this.var2, other.var2)) {
            return false;
        }
        if (!Objects.equals(this.var3, other.var3)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Triplet{" + "var1=" + var1 + ", var2=" + var2 + ", var3=" + var3 + '}';
    }
    
    
}
