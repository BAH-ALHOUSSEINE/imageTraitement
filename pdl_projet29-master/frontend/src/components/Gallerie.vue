<template id="galerie">


</template>

<script>
import axios from "axios";

export default {
    name: "Gallerie",
    props: {
      msg: String,
    },
    data() {
      return {
        response: [],
        
        errors: [],
        file  : ""
      };
    },
methods:{

    
    afficher_image(){

      axios
        .get(`images`)
        .then((response) => {
          // JSON responses are automatically parsed.
          this.response=response.data ;

          
    
        })

       for(var i=0;i<this.response.length;i++){
             var  sauva = this.response[i];
             
           axios
           .get("images/"+Object.values(sauva), { responseType:"blob" })
           alert(Object.values(sauva).[0])
          .then((res) => {
           
	        var reader = new window.FileReader();
	        reader.readAsDataURL(res.data); 
	        reader.onload = function() {
					
	        var imageDataUrl = reader.result;
          var galerie = document.getElementById("galerie")
          var image = galerie.createElement("img")
	        image.setAttribute("src", imageDataUrl);
          }

          });











           }












    }

}




};





</script>