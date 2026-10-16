import java.util.*;

public class Main {
    //Holas
    public static void imprimirMatriz(int [][] m){
        for (int f=0;f<m.length;f++){
          for (int c=0;c<m[0].length ;c++){
            if(m[f][c]!=0){
            System.out.print(m[f][c]+"\t");
            }else{
              System.out.print(" \t");
            }
          } 
          System.out.println("");
        } 
    }
    public static int[][] matrizForma21(int tm, int numI){
      int[][] m=new int[tm][tm];
        for(int c=0;c<m[0].length;c++){//2
          if(c%2==0){//Amarillo
            for (int f=0;f<m.length;f++){
                m[f][c]=numI;  numI++;
            } 
          }else{//Verde
            for (int f=m.length-1;f>=0;f--){
              m[f][c]=numI;  numI++;
            }
          }
        }
      return m;
    }

    public static int[][] matrizForma27(int tm, int numI){
      int [][] m=new int[tm][tm];
        for(int f=m.length-1;f>=0;f--){
          if(f%2==0){
              for (int c=m[0].length-1;c>=0 ;c--){
                m[f][c]=numI; numI++;
              } 
          }else{
              for (int c=0;c<m[0].length;c++){
                m[f][c]=numI; numI++;
              } 
          }
        }
      return m;
    }
    public static int[][] ejercicio_25(int tm, int numI ){
      int[][] m=new int [tm][tm];
      for(int f=0;f<m.length;f++){
        if(f%2==0){
            for (int c=0;c<m[0].length ;c++ ){
                m[f][c]=numI; numI++;
            } 
        }else{
            for (int c=m[0].length-1;c>=0 ;c-- ){
              m[f][c]=numI; numI++;
            }
        }
      }
      return m;
    }

    public static void main(String[] args) {
      imprimirMatriz(matrizForma21(5,1));
      System.out.println("");
      imprimirMatriz(matrizForma27(5,1));
      System.out.println("");
      imprimirMatriz(ejercicio_25(5,1));
    }
}