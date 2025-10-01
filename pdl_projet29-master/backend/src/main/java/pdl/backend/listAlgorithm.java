package pdl.backend;

import java.util.ArrayList;
import java.util.List;

public enum listAlgorithm { 
     // dans le fichier listAlgorihtm.java    
    modifierContrasteHistAvecCanalV,
    modifierContrasteExtAvecCanalV,
    modifierContrasteHistAvecGris,
    modifieContrastExtAvecGris,
    filtre,
    couleurImgConversionTogris,
    modifierLuminosite;

      
     private listAlgorithm() {   
    }  
      
     public static boolean isAlgorithm(String algo) {  
         return  algo.equals("modifierContrasteHistAvecCanalV") || algo.equals("modifierContrasteHistAvecCanalV") || algo.equals( "modifierContrasteHistAvecGris");
    }  
    public static boolean needTwoParametre(String algo){
        return algo.equals("modifierLuminosite");
    }
    
}