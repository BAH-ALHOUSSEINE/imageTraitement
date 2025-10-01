export default {
    name: "HelloWorld",
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

    galerie(){
        alert("bonjour");
    }

}




};