package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.scif.FormatException;
import net.imagej.types.DataType8BitUnsignedInteger;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET)
  @CrossOrigin("*")
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {   // reccupere l'image qui comme identifiant depuis le serveur
  if(imageDao.isValidId(id) == false){ // si l'id est invalid , on retourne le message d'erreur correspond dans la reponse 
  return ((BodyBuilder) ResponseEntity.notFound())
  .body("Aucune image existante avec l’indice "+(id)+"\n");
    //return new ResponseEntity<>( "Aucune image existante avec l’indice "+(id) , HttpStatus.NOT_FOUND); 
  }
  Optional <Image> img = imageDao.retrieve(id); //on reccupere l'image

  return ResponseEntity
              .ok()
              .contentType(MediaType.IMAGE_JPEG)
              .body(img.get().getData());
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)   //delete image with id "id" from the server
  @CrossOrigin("*")
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    if(imageDao.isValidId(id) == false){ // si l'id est invalid , on retourne le message d'erreur correspond dans la reponse 
    return ((BodyBuilder) ResponseEntity.notFound())
    .body("Aucune image existante avec l’indice "+(id)+"\n");
      //return new ResponseEntity<>( "Aucune image existante avec l’indice "+(id) , HttpStatus.NOT_FOUND); 
    }
    Optional <Image> img = imageDao.retrieve(id);
      imageDao.delete(img.get());
      return new ResponseEntity<>("L’image a bien été effacée.\n", HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)  // add image to the server
  @PostMapping(path = "/upload")
  @CrossOrigin("*")
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) throws IOException {
        if(!file.isEmpty()){
          String fileName = file.getOriginalFilename(); //on reccupere le nom de l'image
          String extension = ImageDao.getExtension(fileName); //on reccupere l'extension
          if(ImageDao.isValidExtension(extension) == false){
            return new ResponseEntity<>("La requête a été refusée car le serveur ne supporte pas le format reçu\n", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
          }
          Image img = new Image(file.getOriginalFilename(), file.getBytes());
          imageDao.create(img);
          return new ResponseEntity<>("La requête s’est bien exécutée et l’image est à présent sur le serveur\n", HttpStatus.CREATED);

        }
    
    return new ResponseEntity<>("le contenue est  vide" ,HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")  // return array node from image map
  @CrossOrigin("*")
  @ResponseBody
  public ArrayNode getImageList() throws FormatException, IOException {
    ArrayNode nodes = mapper.createArrayNode();
    List <Image> array = new ArrayList<Image>();
    array = imageDao.retrieveAll();
    for(Image value : array){
      ObjectNode var2 = mapper.createObjectNode();
			var2.put("id" , value.getId());
      var2.put("Name" , value.getName());
      String extension = ImageDao.getExtension(value.getName()); // on recuppere l'extension de l'image
      org.springframework.http.MediaType type = org.springframework.http.MediaType.IMAGE_JPEG; //par defaut on suppose que l'image est de type jpeg 
      if(ImageDao.extensionIsTif(extension)){ //on verifi si l'extension est gif
        type =org.springframework.http.MediaType.IMAGE_GIF;  
      }
      String size = imageDao.getSize(value.getId());
      var2.put("Type",type.getSubtype());
      var2.put("Size", size );
      nodes.add(var2);
    }
   
		
    return nodes;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET , params = "algorithm")
  @CrossOrigin("*")
  public ResponseEntity<?> getImageApplyAlgorithm(@PathVariable("id") long id  , @RequestParam(name = "algorithm" , required = true)  String X , @RequestParam(name = "p1", required = false) String Y , @RequestParam(name = "p2", required = false)  String Z ) throws IOException, FormatException {   // get  image with id "id" from the server 
  // on utilise le type string  pour pouvoir tester si la variable a ete defini ou non dans l'url
    if(imageDao.isValidId(id) == false){ // si l'id est invalid , on retourne le message d'erreur correspond dans la reponse 
    return ((BodyBuilder) ResponseEntity.notFound())
    .body("Aucune image existante avec l’indice "+(id)+"\n");
      //return new ResponseEntity<>( "Aucune image existante avec l’indice "+(id) , HttpStatus.NOT_FOUND); 
    }
    int x = Integer.parseInt(X);
    int y;
    Image img = imageDao.modifierLuminosite(id ,x);
    switch(x){   // on attribut un identifiant a chaque algorithme
      case 0:     // l'identifiant  0 est attribut a modifierLuminosite
      if(Y.isEmpty()){ // on test si la paramettre Y a été defini dans l'url , sinon on montionne le fait que le parametre est manquant
        return ResponseEntity.badRequest()
        .body("L’un des paramètres  mentionné n’existe pas pour l’algorithme choisi : Y\n");
      }
      y = Integer.parseInt(Y);
      if(y <= -256 || y >= 256){ // la valeur du parametre Y sort des cadres de l'algorithme
        return ResponseEntity.badRequest()
        .body("La valeur du jeu de paramètres est invalide : Y qui vaut "+y+" doit avoir une valeur comprise entre -256 et 256 pour etre compatible avec l'algoritm"+x+"\n");
      }
      img = imageDao.modifierLuminosite(id ,y);
      return ResponseEntity //image a bien été traiter
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getData());
      case 1: // l'identifiant  1 est attribué a couleurImgConversionTogris
      img = imageDao.couleurImgConversionTogris(id);
      return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getData());
      case 2://  l'identifiant 2 est attribué  a filtre
      y = Integer.parseInt(Y);
      if(y < 0 || y > 360 ){ // la valeur du parametre y de l'algorithm doit etre comprise entre 0 et 360
        return ResponseEntity.badRequest()
        .body("La valeur du jeu de paramètres est invalide : Y qui vaut "+y+" doit avoir pour valeur compris entre 0 et 360 pour etre compatible avec l'algorithm "+x+"\n");
      }
      img = imageDao.filtre(id , y);
      return ResponseEntity 
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getData());
      case 3://  l'identifiant  3 est attribué a modifieContrastExtAvecGris
      img = imageDao.modifieContrastExtAvecGris(id);
      return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getData());
      case 4: //  l'identifiant  4 est attribué a modifierContrasteHistAvecGris
     img =  imageDao.modifierContrasteHistAvecGris(id);
      return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getData());
      case 5: //  l'identifiant  5 est attribué a modifierContrasteHistAvecCanalV
     img =  imageDao.modifierContrasteHistAvecCanalV(id);
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(img.getData());
      case 6: //  l'identifiant  6 est attribué a modifierContrasteHistAvecCanalS
      img = imageDao.modifierContrasteHistAvecCanalS(id);
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(img.getData());
      case 7: //AJOUTER AU SERVERUR
       
      y = Integer.parseInt(Y);
      Image img1 = imageDao.modifierLuminosite(id ,y);
      Image  imageModifier = new Image(img1.getName()+"(modifie_luminosite)",img1.getData());
      imageDao.create(imageModifier);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(img1.getData());
      case 8: //  Ajouter au serveur 
        
      y = Integer.parseInt(Y);
      Image img2 = imageDao.filtre(id ,y);
      Image  imageModifierfitre = new Image(img2.getName()+"(modifie_filtre_coloré)",img2.getData());
      imageDao.create(imageModifierfitre);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(img2.getData());
      case 9: //  l'identifiant  9 est attribué a filre
      if(Y.isEmpty()){ // on test si la paramettre Y a été defini dans l'url , sinon on montionne le fait que le parametre est manquant
        return ResponseEntity.badRequest()
        .body("L’un des paramètres  mentionné n’existe pas pour l’algorithme choisi : Y\n");
      }
      y = Integer.parseInt(Y);
      if(y <=0){ // la valeur du parametre Y sort des cadres de l'algorithme
        return ResponseEntity.badRequest()
        .body("La valeur du jeu de paramètres est invalide : Y qui vaut "+y+" doit avoir une valeur strictement superieur a 0 pour etre compatible avec l'algoritm"+x+"\n");
      }
      Image img4 = imageDao.gaussFilterImgLib(id , y);
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(img4.getData());
      case 10: //  Ajouter au serveur 
        
      y = Integer.parseInt(Y);
      Image gaussien= imageDao.gaussFilterImgLib(id , y);
      Image  imagegaussine = new Image(gaussien.getName()+"(modifie_gaussien)",gaussien.getData());
      imageDao.create(imagegaussine);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(gaussien.getData());
      case 11: //  Ajouter au serveur 
        
      
      Image colortogris= imageDao.couleurImgConversionTogris(id);
      Image  image_colortogris = new Image(colortogris.getName()+"(modifie_imgcolor_to_gris)",colortogris.getData());
      imageDao.create(image_colortogris);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(colortogris.getData());

      case 12: //  Ajouter au serveur 
        
      
      Image constra_Ext= imageDao.modifieContrastExtAvecGris(id);
      Image  image_constra_Ext = new Image(constra_Ext.getName()+"(modifie_modifieContrastExtAvecGri)",constra_Ext.getData());
      imageDao.create(image_constra_Ext);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(constra_Ext.getData());

      case 13: //  Ajouter au serveur 
        
      
      Image constra_hist= imageDao.modifierContrasteHistAvecGris(id);
      Image  image_constra_hist = new Image(constra_hist.getName()+"(modifie_modifierContrasteHistAvecGris)",constra_hist.getData());
      imageDao.create(image_constra_hist);
      
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(constra_hist.getData());
      default: // aucun identifiant de l'algorithm passé dans l'url ne correspond a l'identifiant d'aucun des algorithmes
      return ResponseEntity.badRequest()
      .body("L’algorithme n’existe pas : "+(x)+"\n");


    }
  }

 




  

}