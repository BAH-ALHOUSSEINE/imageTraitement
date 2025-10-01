<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/>
     <Gallerie msg="my galerie"/>

  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from  '@/components/HelloWorld.vue'
import Gallerie from    '@/components/Gallerie.vue'

export default {
  name: 'Home',
  components: {
    HelloWorld,
    Gallerie
  }
}
</script>
