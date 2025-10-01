<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    

    <div class="container">
    <div class="large-12 medium-12 small-12 cell">
      <label>File
        <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
      </label>
        <button v-on:click="submitFile()">Submit</button>
         
       
         <br>
    </div>
    
  </div>

         
    <button class="Search__button" @click="callRestService()">AFFICHER IMAGES</button>
        <select  v-model="imageid" v-on:click="lead_image()"  id="value_images">
           <option   v-for="values in response"  :value="values.id"  :key="values.id"  > {{values}} </option>
        </select>
        <span><button @click="loadImage">
             Download image 
         </button></span>
        <br/>
          <span><button @click="deleteImage">
              DELETE
         </button></span>
        <br/>
           
            <div v-if="traite_image==true">
           <p>TRAITREMENT D'IMAGE</p>
         <select>
             <option @click="Valeur_LIM()">Réglage de la luminosité</option>
              <option @click="Valeur_VS()">Égalisation d’histogramme</option>
              <option @click="Valeur_TEINTE()">Filtre coloré</option>
              <option  @click="VALEUR_FITRE()">Filtres de flou</option>
              <option  @click="Image_color_to_gris()">COLOR IMAGE TO GRIS</option>
              <option  @click="modifier_constract()">MODIFIER CONSTRACTE</option>
         </select>
           <div  hidden="hidden" id="hid">
              Réglage de la luminosité :<input type="number"  id="val_lum" min="-256" max="256">
              <button @click="RE_LIM()" >APPIQUE</button>
              <button hidden="hidden" id="hid3" @click="RE_LIMAJOU_SERVEUR()" >AJOUTER AU SERVEUR</button>
           </div>
           <div  hidden="hidden" id="hid1">
               Filtre coloré:<input type="number"  id="val_teinte" min="-256" max="256">
              <button @click="FILTRE_COLORE()" >APPIQUE</button>
              <button hidden="hidden" id="hid4" @click="FITRECOLOR_JOU_SERVEUR()" >AJOUTER AU SERVEUR</button>
            </div>
             <div hidden="hidden" id="hid2">
                  <select>
                    <option @click="CANAL_V()">V</option>
                    <option  @click="CANAL_S()">S</option>
                  </select>
                  
             </div>
             <div hidden="hidden" id="hid5">
                  <select>
                    <option @click="FILTRE_G_VALUE()">FILTRE GAUSSIEN</option>
                    <option @click="FILTRE_G_VALUE()">FILTRE MOYENNEUR </option>
                  </select>
                  <div hidden="hidden" id="hid6">
                   La valeur du filtre :<input type="number"  id="val_filtre" min="-256" max="256">
                  <button @click="FILTRE_G()" >APPIQUE</button>
                  <button hidden="hidden" id="hid7" @click="FITREGAUSSIEN_JOU_SERVEUR()" >AJOUTER AU SERVEUR</button>
                  </div>
             </div>
             <div hidden="hidden" id="colorto_gris">
               <button  @click="Image_color_to_gris_ajou_serve()" >AJOUTER AU SERVEUR</button>
             </div>
             <div>
               <select hidden="hidden"  id="modifierconstract">
                 <option @click="modifierconstract_exetention()">AVEC EXTENTION</option>
                 <option @click="modifierconstract_histogramme()" >AVEC HISTOGRAMME </option>
               </select>
                <button hidden="hidden" id="modifierconstractextention" @click="modifierconstract_exetention_ajou_serve()" >AJOUTER AU SERVEUR</button>
                <button hidden="hidden" id="modifierconstracthistogramme" @click="modifierconstract_histogramme_ajou_serve()" >AJOUTER AU SERVEUR</button>
             </div>

         </div>
        
         <img alt=""  width="50%"  height="50%" id ="image1" /> 
        






        
         
      
        

	
    <p>
      For a guide and recipes on how to configure / customize this project,<br>
      check out the
      <a href="https://cli.vuejs.org" target="_blank" rel="noopener">vue-cli documentation</a>.
    </p>
    <h3>Installed CLI Plugins</h3>
    <ul>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-babel" target="_blank" rel="noopener">babel</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-router" target="_blank" rel="noopener">router</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-eslint" target="_blank" rel="noopener">eslint</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-unit-jest" target="_blank" rel="noopener">unit-jest</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-e2e-nightwatch" target="_blank" rel="noopener">e2e-nightwatch</a></li>
    </ul>
    <h3>Essential Links</h3>
    <ul>
      <li><a href="https://vuejs.org" target="_blank" rel="noopener">Core Docs</a></li>
      <li><a href="https://forum.vuejs.org" target="_blank" rel="noopener">Forum</a></li>
      <li><a href="https://chat.vuejs.org" target="_blank" rel="noopener">Community Chat</a></li>
      <li><a href="https://twitter.com/vuejs" target="_blank" rel="noopener">Twitter</a></li>
      <li><a href="https://news.vuejs.org" target="_blank" rel="noopener">News</a></li>
    </ul>
    <h3>Ecosystem</h3>
    <ul>
      <li><a href="https://router.vuejs.org" target="_blank" rel="noopener">vue-router</a></li>
      <li><a href="https://vuex.vuejs.org" target="_blank" rel="noopener">vuex</a></li>
      <li><a href="https://github.com/vuejs/vue-devtools#vue-devtools" target="_blank" rel="noopener">vue-devtools</a></li>
      <li><a href="https://vue-loader.vuejs.org" target="_blank" rel="noopener">vue-loader</a></li>
      <li><a href="https://github.com/vuejs/awesome-vue" target="_blank" rel="noopener">awesome-vue</a></li>
    </ul>
     


      

      
     
  </div>

      
</template>


<script>





import axios from "axios";




 
export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
  data() {
    return {
      response: [],
      errors: [],
      file  : "",
      traite_image:false,
      imageid:null,
      
    };
  },


 

methods: {


   
callRestService() {
           
mounted : {
        axios
        .get(`images`)
        .then((response) => {
          // JSON responses are automatically parsed.
          this.response = response.data;
        })
        .catch((e) => {
          this.errors.push(e);
        });  
         }
}

,

lead_image: function (){
     this.traite_image=true;
    
    var image = document.getElementById('image1');
     
  
    axios.get("images/"+this.imageid, { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

});
},
handleFileUpload(){
    this.file = this.$refs.file.files[0];
    alert(this.file);
  }
  ,

  submitFile(){
        /*
                Initialize the form data
            */
            let formData = new FormData();

            /*
                Add the form data we need to submit
            */
            formData.append('file', this.file);
            
        /*
          Make the request to the POST /single-file URL
        */
            axios.post( 'images',
                formData,
                {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
              }
            ).then(function(){
          console.log('SUCCESS!!');
        })
        .catch(function(){
          console.log('FAILURE!!');
        });
      },

afficher_galerie(){

//alert(this.response);

 for(var i=0;i<this.response.length;i++){
           //  alert(i);
             
           axios
           .get("images/"+i, { responseType:"blob" })
           
          .then((response) => {
           
            var reader = new window.FileReader();
            reader.readAsDataURL(response.data); 
            reader.onload = function() {
              var divgal = document.getElementById("images");
              var fileLink = divgal.createElement('img');
              fileLink.setAttribute("src", imageDataUrl);
             



             


              
            }

          });
  }



}
,
 loadImage(){ // load image which have been selected
 
     axios({
        url: 'http://localhost:8080/images/'+this.imageid,
        method:'GET',
        responseType:'blob',
      }).then((response) => {
        var fileUrl = window.URL.createObjectURL(new Blob([response.data]));
        var fileLink = document.createElement('a');
        fileLink.href =  fileUrl;
        fileLink.setAttribute('download','image_'+this.imageid);
        document.body.appendChild(fileLink);
        fileLink.click();
      })
    }
    ,
    deleteImage(){
         
        axios({
        url: 'http://localhost:8080/images/'+this.imageid,
        method:'DELETE',
        responseType:'blob',
      }).then((response) => {
        alert("IMAGE SUPPRIMER (F5) POUR VOIR L'EFFET");
      })
    }
    ,
    RE_LIM(){
     
    document.getElementById('hid3').removeAttribute("hidden");
    var image = document.getElementById('image1');
    var val_lum = document.getElementById('val_lum');
    
    axios.get("images/"+this.imageid+"?algorithm=0&p1="+val_lum.value, { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

   });
    }
    ,
    Valeur_LIM(){
      document.getElementById('val_lum').value=0;
      document.getElementById('hid').removeAttribute("hidden");
      document.getElementById('hid1').setAttribute("hidden","hidden");
      document.getElementById('hid2').setAttribute("hidden","hidden");
      document.getElementById('hid5').setAttribute("hidden","hidden");
      document.getElementById('colorto_gris').setAttribute("hidden","hidden");
      document.getElementById('modifierconstract').setAttribute("hidden","hidden");
    }
    ,
    Valeur_TEINTE(){
      document.getElementById('val_teinte').value=0;
      document.getElementById('hid1').removeAttribute("hidden");
      document.getElementById('hid').setAttribute("hidden","hidden");
      document.getElementById('hid2').setAttribute("hidden","hidden");
      document.getElementById('hid5').setAttribute("hidden","hidden");
      document.getElementById('colorto_gris').setAttribute("hidden","hidden");
      document.getElementById('modifierconstract').setAttribute("hidden","hidden");
    }
    ,
      FILTRE_COLORE(){

    document.getElementById('hid4').removeAttribute("hidden");
    var image = document.getElementById('image1');
    var val_lum = document.getElementById('val_teinte');
    
    axios.get("images/"+this.imageid+"?algorithm=2&p1="+val_lum.value, { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

});
    }
    ,

    Valeur_VS(){
       document.getElementById('hid2').removeAttribute("hidden");
       document.getElementById('hid').setAttribute("hidden","hidden");
       document.getElementById('hid5').setAttribute("hidden","hidden");
       document.getElementById('hid1').setAttribute("hidden","hidden");
       document.getElementById('colorto_gris').setAttribute("hidden","hidden");
       document.getElementById('modifierconstract').setAttribute("hidden","hidden");
    },
    RE_LIMAJOU_SERVEUR(){
      var val_lum = document.getElementById('val_lum');
        axios.get("images/"+this.imageid+"?algorithm=7&p1="+val_lum.value, { responseType:"blob" })
    .then(function (response) {
});

    },
    FITRECOLOR_JOU_SERVEUR(){
      var val_teinte= document.getElementById('val_teinte');
       axios.get("images/"+this.imageid+"?algorithm=8&p1="+val_teinte.value, { responseType:"blob" })
      .then(function (response) {
      });
    },
    CANAL_V(){
  
    var image = document.getElementById('image1');
    
    
    axios.get("images/"+this.imageid+"?algorithm=5", { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

});
    },
    CANAL_S(){
      
       document.getElementById('hid4').removeAttribute("hidden");
    var image = document.getElementById('image1');
    
    
    axios.get("images/"+this.imageid+"?algorithm=6", { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

});


    },
VALEUR_FITRE(){
       document.getElementById('hid5').removeAttribute("hidden");
       document.getElementById('hid').setAttribute("hidden","hidden");
       document.getElementById('hid1').setAttribute("hidden","hidden");
       document.getElementById('hid2').setAttribute("hidden","hidden");
       document.getElementById('colorto_gris').setAttribute("hidden","hidden");
       document.getElementById('modifierconstract').setAttribute("hidden","hidden");
      
}
,
FILTRE_G_VALUE(){
  document.getElementById('hid6').removeAttribute("hidden");
}
,
FILTRE_G(){

 document.getElementById('hid7').removeAttribute("hidden");
    var image = document.getElementById('image1');
    var val_filtre = document.getElementById('val_filtre');
    
    axios.get("images/"+this.imageid+"?algorithm=9&p1="+val_filtre.value, { responseType:"blob" })
    .then(function (response) {

   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

});


}
,
 FITREGAUSSIEN_JOU_SERVEUR(){
      var val_filtre = document.getElementById('val_filtre');
       axios.get("images/"+this.imageid+"?algorithm=10&p1="+val_filtre.value, { responseType:"blob" })
      .then(function (response) {
      });
    },

    Image_color_to_gris(){
      document.getElementById('hid').setAttribute("hidden","hidden");
      document.getElementById('hid2').setAttribute("hidden","hidden");
      document.getElementById('hid1').setAttribute("hidden","hidden");
      document.getElementById('hid5').setAttribute("hidden","hidden");
      document.getElementById("colorto_gris").removeAttribute("hidden");
      document.getElementById('modifierconstract').setAttribute("hidden","hidden");
     axios.get("images/"+this.imageid+"?algorithm=1", { responseType:"blob" })
    .then(function (response) {
     var image = document.getElementById('image1');
   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

   });

    },

    Image_color_to_gris_ajou_serve(){

      axios.get("images/"+this.imageid+"?algorithm=11&", { responseType:"blob" })
      .then(function (response) {
      });
    },

    modifier_constract(){

      document.getElementById("modifierconstract").removeAttribute("hidden");
      document.getElementById('colorto_gris').setAttribute("hidden","hidden");
      document.getElementById('hid').setAttribute("hidden","hidden");
      document.getElementById('hid2').setAttribute("hidden","hidden");
      document.getElementById('hid1').setAttribute("hidden","hidden");
      document.getElementById('hid5').setAttribute("hidden","hidden");
    },

    modifierconstract_exetention(){
      document.getElementById('modifierconstracthistogramme').setAttribute("hidden","hidden");
      document.getElementById('modifierconstractextention').removeAttribute("hidden");
     axios.get("images/"+this.imageid+"?algorithm=3", { responseType:"blob" })
    .then(function (response) {
     var image = document.getElementById('image1');
   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

   });

    }
    ,
    modifierconstract_histogramme(){
      document.getElementById('modifierconstracthistogramme').removeAttribute("hidden");
      document.getElementById('modifierconstractextention').setAttribute("hidden","hidden");
      axios.get("images/"+this.imageid+"?algorithm=4", { responseType:"blob" })
    .then(function (response) {
     var image = document.getElementById('image1');
   	var reader = new window.FileReader();
  	reader.readAsDataURL(response.data); 
	  reader.onload = function() {
					
	    var imageDataUrl = reader.result;
	    image.setAttribute("src", imageDataUrl);
     
	}

   });
       

    },
    modifierconstract_exetention_ajou_serve(){
      
     
       axios.get("images/"+this.imageid+"?algorithm=12&", { responseType:"blob" })
      .then(function (response) {
      });

    }
    ,
     modifierconstract_histogramme_ajou_serve(){
     
       axios.get("images/"+this.imageid+"?algorithm=13&", { responseType:"blob" })
      .then(function (response) {
      });

    }
    ,


}
};



</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
.Search__button {

background-color: #42b983;
padding: 7px;
border-radius: 10px;

}
button {

background-color: #42b983;
padding: 7px;
border-radius: 10px;

}



</style>