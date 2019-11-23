
<template>

  <div id="app">
    <Menu></Menu>
    <router-view></router-view>
  </div>

</template>


<script>

  import Menu from "./components/Menu";
  export default {
    name: 'app',
    components: {
      Menu,
    }
  }
</script>

<style>
  body{
    background: lightskyblue;
  }
  #app {
    font-family: 'Roboto', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    padding: 3px;
    margin: 0;
  }
</style>
